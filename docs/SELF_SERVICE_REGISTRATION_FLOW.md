# Self-Service Registration Flow - OTP Verification

## Overview
This document verifies the self-service registration flow and confirms that OTP verification is required **before** registration.

## Current Implementation Status
 **CORRECT BEHAVIOR CONFIRMED (with optional SMS bypass mode)**

When a frontend makes a request to register in self-service mode **WITHOUT** prior OTP verification, it will receive an **error** (not a prompt), which is the intended behavior.

---

## Self-Service Registration Flow - Visual Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                         SELF-SERVICE REGISTRATION FLOW                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  Frontend                          Backend                      Keycloak        │
│     │                                 │                            │            │
│     │──── Step 1: Request OTP ───────>│                            │            │
│     │ POST /otp/request               │─── Create OTP Challenge ──>│            │
│     │ {mobileNumber}                  │                            │            │
│     │                                 │─── Send SMS (or bypass) ───│            │
│     │<─── OTP Challenge Created ──────│                            │            │
│     │ {status: "pending"}             │                            │            │
│     │                                 │                            │            │
│     │──── Step 2: Verify OTP ───────>│                            │            │
│     │ POST /otp/verify-registration   │─── Validate OTP Code ─────>│            │
│     │ {mobileNumber, otp}             │                            │            │
│     │                                 │─── Mark OTP Proof (cache)  │            │
│     │<─── OTP Verified ───────────────│                            │            │
│     │ {status: "verified"}            │                            │            │
│     │                                 │                            │            │
│     │──── Step 3: Register User ────>│                            │            │
│     │ POST /register/self-service     │─── Check OTP Proof ────────│            │
│     │ {username, email, password,     │                            │            │
│     │  mobileNumber, ...}             │─── Create User in Keycloak │            │
│     │                                 │───────────────────────────>│            │
│     │                                 │<─── User Created ──────────│            │
│     │                                 │─── Issue Auth Proof (cache)│            │
│     │<─── User Created ───────────────│                            │            │
│     │ {registrationAuthProof: UUID}   │                            │            │
│     │                                 │                            │            │
│     │──── Step 4: Exchange Proof ───>│                            │            │
│     │ POST /token/by-registration-proof                           │            │
│     │ {registrationAuthProof}         │─── Password Grant ────────>│            │
│     │                                 │ (with username/password)   │            │
│     │                                 │<─── Access Token ──────────│            │
│     │                                 │<─── Refresh Token ─────────│            │
│     │<─── Tokens Issued ──────────────│                            │            │
│     │ {accessToken, refreshToken,     │                            │            │
│     │  expiresIn, tokenType: Bearer}  │                            │            │
│     │                                 │                            │            │
│  [Frontend now has access token]      │                            │            │
│  [Can make authenticated API calls]   │                            │            │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## Self-Service Registration Flow (Correct Order)

### Step 1: Request OTP
**Endpoint:** `POST /api/v1/auth/otp/request`
- **Authentication:** Not required (public endpoint)
- **Request Body:**
  ```json
  {
    "mobileNumber": "+639171234567"
  }
  ```
- **Response:** OTP challenge is created; SMS is sent when transport is enabled
- **Status Code:** 200 OK

---

### Step 2: Verify OTP (Registration Proof)
**Endpoint:** `POST /api/v1/auth/otp/verify-registration`
- **Authentication:** Not required (public endpoint)
- **Request Body:**
  ```json
  {
    "mobileNumber": "+639171234567",
    "otp": "123456"
  }
  ```
- **Key Action:** On successful verification, the backend calls:
  ```java
  registrationOtpProofService.markVerifiedForRegistration(request.getMobileNumber());
  ```
  This marks the mobile number as OTP-verified for registration purposes.
- **Response:** OTP verification successful
- **Status Code:** 200 OK
- **Bypass Mode Note:** If `sms.bypass-sending=true`, any numeric OTP can pass **after** Step 1 creates a challenge.

---

### Step 3: Register User (Self-Service)
**Endpoint:** `POST /api/v1/auth/register/self-service`
- **Authentication:** Not required (public endpoint)
- **Request Body:**
  ```json
  {
    "username": "jdelacruz",
    "email": "juan@example.com",
    "firstName": "Juan",
    "lastName": "Dela Cruz",
    "password": "SecurePass123!",
    "mobileNumber": "+639171234567",
    "enabled": true,
    "emailVerified": true,
    "communityRole": "RESIDENT",
    "communityId": "550e8400-e29b-41d4-a716-446655440000",
    "address": {
      "street": "123 Main St",
      "barangay": "Poblacion",
      "city": "Makati",
      "province": "Metro Manila",
      "region": 13,
      "postalCode": "1200",
      "country": "Philippines"
    }
  }
  ```
- **Key Check:** Inside `UserServiceImpl.createSelfServiceUser()`:
  ```java
  boolean hasOtpProof = registrationOtpProofService.consumeRegistrationProof(request.mobileNumber());
  if (!hasOtpProof) {
    throw new ForbiddenException("Mobile number must be OTP verified before self-service registration");
  }
  ```
  - If OTP proof exists and is consumed → registration succeeds 
  - If OTP proof does NOT exist → ForbiddenException is thrown 
- **Success Response (HTTP 201):**
  ```json
  {
    "success": true,
    "message": "User registered successfully",
    "username": "jdelacruz",
    "registrationAuthProof": "3b9d3b2a-7f4e-45c1-8a4d-1d0b3a11d65f"
  }
  ```
- **Error Response (HTTP 400):**
  ```json
  {
    "success": false,
    "message": "Mobile number must be OTP verified before self-service registration",
    "errorType": "ForbiddenException"
  }
  ```

---

### Step 4: Exchange Registration Proof for Access Token
**Endpoint:** `POST /api/v1/auth/token/by-registration-proof`
- **Authentication:** Not required (public endpoint)
- **Request Body:**
  ```json
  {
    "registrationAuthProof": "3b9d3b2a-7f4e-45c1-8a4d-1d0b3a11d65f"
  }
  ```
- **Behavior:**
  - Proof is one-time and consumed immediately.
  - Backend exchanges the proof for Keycloak tokens.
- **Success Response (HTTP 200):**
  ```json
  {
    "accessToken": "eyJ...",
    "refreshToken": "eyJ...",
    "expiresIn": 300,
    "refreshExpiresIn": 1800,
    "tokenType": "Bearer"
  }
  ```
- **Error Response (HTTP 401):** invalid/expired/already-consumed proof.

---

## Error Scenario: Registration Without OTP Verification

### ❌ INCORRECT Frontend Flow
```
Frontend directly calls POST /api/v1/auth/register/self-service 
WITHOUT calling OTP request/verify first
↓
Backend receives registration request
↓
Checks: registrationOtpProofService.consumeRegistrationProof(mobileNumber)
↓
Returns FALSE (no OTP proof in cache)
↓
Throws ForbiddenException
↓
Frontend receives HTTP 400 with error message:
{
  "success": false,
  "message": "Mobile number must be OTP verified before self-service registration",
  "errorType": "ForbiddenException"
}
```



## Implementation Details

### RegistrationOtpProofService
- **Location:** `src/main/java/com/ria/olita/tech/silingan/service/otp/RegistrationOtpProofService.java`
- **Methods:**
  - `markVerifiedForRegistration(mobileNumber)` - Called after OTP verification
  - `consumeRegistrationProof(mobileNumber)` - Called during registration (one-time, removes proof)

### OtpVerificationStateService (Different from above)
- Handles authentication OTP state (JWT-based, for MFA after login)
- **NOT** used for self-service registration
- Used only for authenticated endpoints requiring OTP verification

---

## Endpoint Summary (Current)

- `POST /api/v1/auth/otp/request` (public; registration OTP challenge)
- `POST /api/v1/auth/otp/verify-registration` (public; marks registration proof)
- `POST /api/v1/auth/register/self-service` (public; returns one-time `registrationAuthProof`)
- `POST /api/v1/auth/token/by-registration-proof` (public; returns access/refresh token)
- `POST /api/v1/auth/otp/resend` (authenticated; MFA flow)
- `POST /api/v1/auth/otp/verify` (authenticated; MFA flow)
- `GET /api/v1/auth/otp/status` (authenticated; MFA flow)

---

## Proof State Lifecycle

### OTP Registration Proof (Mobile Number)
```
NOT_EXISTS
    │
    ├─ POST /otp/request ─────────> OTP Challenge Created
    │                                (cooldown, attempts tracking)
    │
    └─ POST /otp/verify-registration
       (with valid OTP)            ─────────> ✓ MARKED (in cache)
                                                 │
                                                 │
                                    POST /register/self-service
                                    (checks & consumes proof)
                                                 │
                                                 └─> CONSUMED
                                                     (removed from cache)
```

### Registration Auth Proof (UUID)
```
NOT_EXISTS
    │
    └─ POST /register/self-service ────────> ✓ ISSUED (in cache)
       (after user creation)                    │
                                                │ (TTL = OTP config expiration)
                                                │
                                    POST /token/by-registration-proof
                                    (exchanges for Keycloak tokens)
                                                │
                                                └─> CONSUMED
                                                    (removed from cache after success)
```

---

## Frontend Implementation Checklist

- [ ] **Step 1**: Request OTP → Store mobile number locally for Steps 2-3
- [ ] **Step 2**: Verify OTP → User enters 6-digit code from SMS (or any code if bypass enabled)
- [ ] **Step 3**: Register User → Submit form with all user details + mobile number
  - Extract `registrationAuthProof` from response and store temporarily
- [ ] **Step 4**: Exchange Proof → Send `registrationAuthProof` to token endpoint
  - Store returned `accessToken` and `refreshToken` in secure storage (e.g., httpOnly cookies or encrypted local storage)
- [ ] **Done**: Frontend is now authenticated with Bearer token
  - Use `Authorization: Bearer {accessToken}` for all subsequent API requests
  - Use `refreshToken` to get a new `accessToken` before expiration

---

## SMS Bypass Mode (Development/Testing)

When `sms.bypass-sending=true` is set in `application.yaml`:

1. **Step 1 (Request OTP)**: OTP challenge is created but SMS is **NOT sent**
   - No HTTP call to SMS provider
   - Cooldown and attempt limits still enforced
   - Log warning: `"SMS bypass is enabled; skipping OTP send to ***7890"`

2. **Step 2 (Verify OTP)**: Any numeric OTP code is accepted
   - No hash verification against stored OTP
   - Mobile number is marked as verified for registration
   - Log warning: `"SMS bypass enabled: accepting OTP verification without code check for ***7890"`

3. **Steps 3-4**: Unchanged (registration and token exchange work normally)

**Use case:** Bypass SMS when SMS provider is unavailable or during development, but keep OTP flow logic intact.


