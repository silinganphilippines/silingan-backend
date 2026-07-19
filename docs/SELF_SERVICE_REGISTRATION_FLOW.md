# Self-Service Registration Flow (3 Steps)

## Overview
Self-service registration now completes in exactly **3 steps**. There is **no** `registrationAuthProof` and no token exchange endpoint after registration.

1. Request OTP
2. Verify OTP for registration
3. Register user and receive login token in the same response

---

## Visual Flow

```
Frontend                          Backend
   |                                 |
   |-- Step 1: POST /auth/otp/request -->
   |   { mobileNumber }               |
   |<-- 200 OTP challenge created ----|
   |                                  |
   |-- Step 2: POST /auth/otp/verify-registration -->
   |   { mobileNumber, otp }          |
   |<-- 200 verified -----------------|
   |                                  |
   |-- Step 3: POST /auth/register/self-service -->
   |   { user payload... }            |
   |<-- 201 user created + login -----|
   |    { accessToken, tokenType, expiresIn, roles, ... }
```

---

## Step 1: Request OTP

- **Endpoint:** `POST /api/v1/auth/otp/request`
- **Auth:** Public
- **Body:**

```json
{
  "mobileNumber": "+639171234567"
}
```

- **Expected:** OTP challenge is created (SMS sent if transport is enabled).

---

## Step 2: Verify OTP For Registration

- **Endpoint:** `POST /api/v1/auth/otp/verify-registration`
- **Auth:** Public
- **Body:**

```json
{
  "mobileNumber": "+639171234567",
  "otp": "123456"
}
```

- **Expected:** mobile number is marked verified for registration using `RegistrationOtpProofService`.

---

## Step 3: Register Self-Service User (Token Returned Immediately)

- **Endpoint:** `POST /api/v1/auth/register/self-service`
- **Auth:** Public
- **Body:**

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

- **Success response (201):**

```json
{
  "success": true,
  "message": "User registered successfully",
  "username": "jdelacruz",
  "login": {
    "accessToken": "eyJ...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "keycloakUserId": "abc123-def456-ghi789",
    "mobileNumber": "+639171234567",
    "roles": ["RESIDENT"]
  },
  "accessToken": "eyJ...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "roles": ["RESIDENT"]
}
```

- **Important:** No `registrationAuthProof` is returned.

---

## Invalid Flow (Skipping OTP Verification)

If frontend calls self-service registration without completing Step 2, backend rejects the request.

Example error:

```json
{
  "success": false,
  "message": "Mobile number must be OTP verified before self-service registration",
  "errorType": "ForbiddenException"
}
```

---

## Endpoint Summary (Current)

- `POST /api/v1/auth/otp/request` (public)
- `POST /api/v1/auth/otp/verify-registration` (public)
- `POST /api/v1/auth/register/self-service` (public, returns token immediately)
- `POST /api/v1/auth/login/otp` (public, for existing users)

---

## Frontend Checklist

- [ ] Call Step 1 and store mobile number
- [ ] Call Step 2 using the OTP code
- [ ] Call Step 3 and store returned `accessToken`
- [ ] Send `Authorization: Bearer {accessToken}` for protected APIs

---

## SMS Bypass Mode (Dev/Test)

If `sms.bypass-sending=true`:

1. Step 1 still creates challenge, but SMS is not sent.
2. Step 2 accepts numeric OTP for development flow.
3. Step 3 behavior is unchanged.


