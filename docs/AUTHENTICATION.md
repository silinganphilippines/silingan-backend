# Authentication & Authorization Guide

Complete guide for OTP-based authentication, JWT tokens, and community management in Silingan Backend.

## Table of Contents

1. [JWT Configuration](#jwt-configuration)
2. [OTP Authentication Flow](#otp-authentication-flow)
3. [Self-Service Registration](#self-service-registration)
4. [JWT Token Structure](#jwt-token-structure)
5. [Community Management](#community-management)
6. [Error Handling](#error-handling)
7. [Testing](#testing)

---

## JWT Configuration

### Generating a JWT Secret

The JWT secret must be at least 256 bits (32 bytes) for HS256 encryption.

#### Option 1: OpenSSL (Recommended)
```bash
openssl rand -base64 32
```

Output example:
```
X9kY7mN2pQ8rT5vW3xZaBcDeFgHiJkLmNoPqRsTuVw=
```

#### Option 2: Python
```bash
python3 -c "import secrets; print(secrets.token_urlsafe(32))"
```

#### Option 3: JavaScript/Node.js
```bash
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

### Configuring the Secret

#### Development (Local)
```bash
# Export as environment variable
export JWT_SECRET="X9kY7mN2pQ8rT5vW3xZaBcDeFgHiJkLmNoPqRsTuVw="

# Then run the application
java -jar target/silingan-0.0.1.jar
```

#### Production
Set via environment variables in your deployment:
- Kubernetes: Secret resource
- Docker: Environment variable in container run command
- Cloud Platform (AWS/Azure/GCP): Secrets Manager

**Never hardcode secrets in configuration files or commit to version control.**

### JWT Properties

Configure additional JWT properties as needed:

```yaml
jwt:
  secret: ${JWT_SECRET}
  issuer: ${JWT_ISSUER:silingan-backend}
  access-token-ttl-seconds: ${JWT_ACCESS_TOKEN_TTL_SECONDS:3600}
```

| Property | Default | Description | Range |
|----------|---------|-------------|-------|
| `secret` | - | Secret key (min 32 bytes) | Required |
| `issuer` | `silingan-backend` | JWT issuer claim | Any string |
| `access-token-ttl-seconds` | `3600` | Token lifetime | 60+ seconds |

---

## OTP Authentication Flow

### Overview

```
User Request
    ↓
[1] Request OTP → SMS Sent to Mobile
    ↓
[2] User Receives OTP via SMS
    ↓
[3] Verify OTP + Get JWT
    ↓
JWT Token Issued ← Valid OTP
```

### Step 1: Request OTP

**Endpoint:**
```http
POST /api/v1/otp/request
Content-Type: application/json

{
  "mobileNumber": "+639171234567"
}
```

**Success Response (202 Accepted):**
```json
{
  "message": "OTP sent successfully",
  "mobileNumber": "+639171234567",
  "expirationMinutes": 5
}
```

**Error Response:**
```json
{
  "code": "INVALID_PHONE_NUMBER",
  "message": "Invalid phone number format",
  "path": "/api/v1/otp/request"
}
```

### Step 2: Verify OTP and Obtain JWT

**Endpoint:**
```http
POST /api/v1/auth/login/otp
Content-Type: application/json

{
  "mobileNumber": "+639171234567",
  "otp": "123456"
}
```

### OTP-Protected Endpoint Behavior

For endpoints protected by OTP middleware, access is allowed when either condition is true:

1. OTP verification exists for the current `keycloakUserId` + JWT `jti` pair, or
2. OTP verification exists for the current `keycloakUserId` (user-level fallback).

This user-level fallback keeps access working when a user has already verified OTP but receives a different token id.

**Success Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDEiLCJrZXljbG9ha0lkIjoiYWJjMTIzLWRlZjQ1Ni1naGk3ODkiLCJtb2JpbGVOdW1iZXIiOiIrNjM5MTcxMjM0NTY3IiwiY29tbXVuaXR5SWQiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJyb2xlcyI6WyJSRVNJREVOVCJdLCJleHAiOjE2MjY0NzE5OTksImlhdCI6MTYyNjQ2ODM5OSwiaXNzIjoic2lsaW5nYW4tYmFja2VuZCJ9.signature",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "userId": "550e8400-e29b-41d4-a716-446655440001",
  "keycloakUserId": "abc123-def456-ghi789",
  "mobileNumber": "+639171234567",
  "roles": ["RESIDENT"]
}
```

**Error Responses:**

Invalid OTP:
```json
{
  "code": "INVALID_OTP",
  "message": "Invalid OTP",
  "path": "/api/v1/auth/login/otp"
}
```

Expired OTP:
```json
{
  "code": "EXPIRED_OTP",
  "message": "OTP has expired",
  "path": "/api/v1/auth/login/otp"
}
```

Too many attempts:
```json
{
  "code": "TOO_MANY_ATTEMPTS",
  "message": "Too many OTP verification attempts. Try again later.",
  "path": "/api/v1/auth/login/otp"
}
```

No active community:
```json
{
  "code": "VALIDATION_ERROR",
  "message": "No active community selected for user",
  "path": "/api/v1/auth/login/otp"
}
```

---

## Self-Service Registration

### Overview

Users can self-register with immediate JWT token issuance after OTP verification.

```
[1] Request OTP
    ↓
[2] Verify OTP for registration
    ↓
[3] Submit registration details
    → Backend creates user (DB + Keycloak), assigns community, and returns JWT
    ↓
Ready to use!
```

### Registration Endpoint

**Endpoint:**
```http
POST /api/v1/auth/register/self-service
Content-Type: application/json

{
  "username": "jdelacruz",
  "email": "juan@example.com",
  "firstName": "Juan",
  "lastName": "Dela Cruz",
  "mobileNumber": "+639171234567",
  "password": "SecurePass@123",
  "enabled": true,
  "emailVerified": true,
  "communityId": "550e8400-e29b-41d4-a716-446655440000",
  "communityRole": "RESIDENT",
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

### Field Requirements

| Field | Type | Required | Rules | Example |
|-------|------|----------|-------|---------|
| `username` | string | ✅ | Unique in Keycloak | `jdelacruz` |
| `email` | string | ✅ | Valid email format | `juan@example.com` |
| `firstName` | string | ✅ | 1-100 chars | `Juan` |
| `lastName` | string | ✅ | 1-100 chars | `Dela Cruz` |
| `mobileNumber` | string | ✅ | Valid phone format | `+639171234567` or `09171234567` |
| `password` | string | ✅ | Min 8 chars, 1 uppercase, 1 number, 1 special | `SecurePass@123` |
| `enabled` | boolean | ✅ | User account status | `true` |
| `emailVerified` | boolean | ✅ | Email verification status | `true` |
| `communityId` | UUID | ✅ | Valid community UUID | `550e8400-e29b-41d4-a716-446655440000` |
| `communityRole` | enum | ✅ | `RESIDENT`, `COMMUNITY_ADMIN`, etc. | `RESIDENT` |
| `address` | object | ✅ | User address details | See below |
| `address.street` | string | ✅ | Street address | `123 Main St` |
| `address.barangay` | string | ✅ | Barangay name (PH) | `Poblacion` |
| `address.city` | string | ✅ | City name | `Makati` |
| `address.province` | string | ✅ | Province name | `Metro Manila` |
| `address.region` | integer | ✅ | Region code (PH) | `13` |
| `address.postalCode` | string | ✅ | Postal code | `1200` |
| `address.country` | string | ✅ | Country name | `Philippines` |

### Phone Number Normalization

Phone numbers are automatically normalized:

| Input | Normalized | Notes |
|-------|-----------|-------|
| `09171234567` | `+639171234567` | Leading `0` replaced with `+63` |
| `9171234567` | `+639171234567` | `+63` prefix added |
| `+639171234567` | `+639171234567` | Already normalized |
| `639171234567` | `+639171234567` | `+` prefix added |

### Success Response (201 Created)

```json
{
  "success": true,
  "message": "User registered successfully",
  "username": "jdelacruz",
  "login": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "keycloakUserId": "abc123-def456-ghi789",
    "mobileNumber": "+639171234567",
    "roles": ["RESIDENT"]
  },
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "roles": ["RESIDENT"]
}
```

### Error Response (400 Bad Request)

```json
{
  "success": false,
  "message": "Username already exists: jdelacruz",
  "errorType": "ConflictException"
}
```

---

## JWT Token Structure

### Token Format

JWT tokens follow the standard format: `header.payload.signature`

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDEiLCJrZXljbG9ha0lkIjoiYWJjMTIzLWRlZjQ1Ni1naGk3ODkiLCJtb2JpbGVOdW1iZXIiOiIrNjM5MTcxMjM0NTY3IiwiY29tbXVuaXR5SWQiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJyb2xlcyI6WyJSRVNJREVOVCJdLCJleHAiOjE2MjY0NzE5OTksImlhdCI6MTYyNjQ2ODM5OSwiaXNzIjoic2lsaW5nYW4tYmFja2VuZCJ9.
signature
```

### Decoded Payload

```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440001",
  "keycloakId": "abc123-def456-ghi789",
  "mobileNumber": "+639171234567",
  "firstName": "Juan",
  "lastName": "Dela Cruz",
  "communityId": "550e8400-e29b-41d4-a716-446655440000",
  "roles": ["RESIDENT"],
  "iss": "silingan-backend",
  "exp": 1626471999,
  "iat": 1626468399,
  "jti": "550e8400-e29b-41d4-a716-446655440002"
}
```

### Claim Reference

| Claim | Type | Required | Description | Source |
|-------|------|----------|-------------|--------|
| `sub` | UUID | ✅ | Subject - App user ID (primary identifier) | Database |
| `keycloakId` | String | ✅ | Keycloak user ID | Keycloak |
| `mobileNumber` | String | ✅ | Registered mobile number | Database |
| `firstName` | String | ✅ | User's first name | Database |
| `lastName` | String | ✅ | User's last name | Database |
| `communityId` | UUID | ✅ | Active community (source of truth: Keycloak) | Keycloak Attribute |
| `roles` | Array<String> | ✅ | Realm roles | Keycloak |
| `iss` | String | ✅ | Token issuer | JWT Config |
| `exp` | Timestamp | ✅ | Expiration time (Unix epoch) | Calculated |
| `iat` | Timestamp | ✅ | Issued-at time (Unix epoch) | Generated |
| `jti` | String | ✅ | Unique JWT ID | Generated |

### Using the Token

**Include in Authorization Header:**
```http
GET /api/v1/protected-endpoint
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Validation

The backend validates:
1. ✅ Signature matches (using JWT_SECRET)
2. ✅ Token not expired (`exp` > current time)
3. ✅ Issuer matches (`iss` == configured issuer)
4. ✅ Required claims present

---

## Community Management

### Architecture

```
User (1) ──────→ (N) UserCommunity (N) ←────── (1) Community
                       │
                       └─→ Keycloak
                           │
                           └─→ communityId attribute
                               (Active/Current)
```

### Community Storage

Communities are stored in two places:

#### 1. Database (Persistent)
- `user_community` join table links users to their communities
- Stores user's role in each community
- Reflects all communities user has access to

#### 2. Keycloak (Active Community)
- `communityId` attribute on user
- Represents currently active community
- Updated on community switch
- Embedded in JWT token

### Community Assignment

#### During Registration
```java
// CreateUserRequest includes communityId
CreateUserRequest {
  communityId: "550e8400-e29b-41d4-a716-446655440000",
  communityRole: "RESIDENT"
}

// Keycloak user is created with attribute
attributes {
  "communityId": ["550e8400-e29b-41d4-a716-446655440000"]
}

// JWT token includes this communityId
```

#### During Login (OTP)
```java
// Backend retrieves active communityId from Keycloak
Map<String, List<String>> attributes = keycloakService.getUserAttributes(keycloakUserId);
UUID activeCommunityId = UUID.fromString(attributes.get("communityId").get(0));

// JWT is issued with this communityId
```

### Switching Communities

**Endpoint:**
```http
POST /api/v1/communities/{communityId}/switch
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**Request:**
```json
{
  // No body required - communityId in URL
}
```

**Success Response (204 No Content):**
```
HTTP/1.1 204 No Content
```

**Effect:**
1. Keycloak attribute `communityId` is updated
2. Next JWT generated will use new `communityId`
3. Current JWT remains valid until expiration
4. Subsequent requests use new community context

**Example Flow:**
```
User has JWT with communityId: "community-A"
    ↓
POST /api/v1/communities/community-B/switch
    ↓
Backend updates Keycloak attribute
    ↓
User makes new request → JWT refresh/re-issue
    ↓
New JWT has communityId: "community-B"
```

### Validation

When a user tries to switch communities:
1. ✅ User must exist in Keycloak
2. ✅ Community must exist in database
3. ✅ User must have access to that community
4. ✅ Community must not be archived

---

## Error Handling

### HTTP Status Codes

| Status | Meaning | Example |
|--------|---------|---------|
| 200 | Success | OTP verified, JWT returned |
| 201 | Created | User registered |
| 202 | Accepted | OTP request accepted, SMS sent |
| 204 | No Content | Community switched successfully |
| 400 | Bad Request | Invalid input, validation error |
| 401 | Unauthorized | Invalid credentials, missing token |
| 403 | Forbidden | OTP required, insufficient permissions |
| 404 | Not Found | User/community not found |
| 409 | Conflict | Username already exists |
| 410 | Gone | OTP expired |
| 429 | Too Many Requests | OTP attempts exceeded, cooldown active |
| 500 | Server Error | Unexpected error |

### Error Response Format

```json
{
  "code": "ERROR_CODE",
  "message": "Human-readable error message",
  "path": "/api/endpoint"
}
```

### Common Errors

#### Missing JWT Secret
```json
{
  "code": "INTERNAL_ERROR",
  "message": "An error occurred while attempting to encode the Jwt: Failed to create a JWS Signer -> The secret length must be at least 256 bits",
  "path": "/api/v1/auth/login/otp"
}
```

**Solution:** Set `JWT_SECRET` environment variable with valid 256+ bit secret

#### No Active Community
```json
{
  "code": "VALIDATION_ERROR",
  "message": "No active community selected for user",
  "path": "/api/v1/auth/login/otp"
}
```

**Solution:** Ensure `communityId` is set in Keycloak user attributes

#### Invalid OTP
```json
{
  "code": "INVALID_OTP",
  "message": "Invalid OTP",
  "path": "/api/v1/auth/login/otp"
}
```

**Solution:** User must enter correct OTP before expiration (default: 5 minutes)

#### OTP Expired
```json
{
  "code": "EXPIRED_OTP",
  "message": "OTP has expired",
  "path": "/api/v1/auth/login/otp"
}
```

**Solution:** Request new OTP via `/api/v1/otp/request`

#### Too Many Attempts
```json
{
  "code": "TOO_MANY_ATTEMPTS",
  "message": "Too many OTP verification attempts. Try again later.",
  "path": "/api/v1/auth/login/otp"
}
```

**Solution:** Wait for cooldown period (default: 60 seconds) before retrying

---

## Testing

### Using Swagger UI

1. Navigate to http://localhost:8082/swagger-ui/index.html
2. Expand "Auth" section
3. Try OTP and registration endpoints

### Manual API Testing with cURL

#### Request OTP
```bash
curl -X POST http://localhost:8082/api/v1/otp/request \
  -H "Content-Type: application/json" \
  -d '{"mobileNumber": "+639171234567"}'
```

#### Verify OTP (bypass mode for testing)
```bash
curl -X POST http://localhost:8082/api/v1/auth/login/otp \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "+639171234567",
    "otp": "000000"
  }'
```

#### Self-Service Registration
```bash
curl -X POST http://localhost:8082/api/v1/auth/register/self-service \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "mobileNumber": "+639171234567",
    "password": "TestPass@123",
    "enabled": true,
    "emailVerified": true,
    "communityId": "550e8400-e29b-41d4-a716-446655440000",
    "communityRole": "RESIDENT",
    "address": {
      "street": "123 Test St",
      "barangay": "Test",
      "city": "Test City",
      "province": "Test Province",
      "region": 13,
      "postalCode": "1234",
      "country": "Philippines"
    }
  }'
```

#### Using JWT Token
```bash
curl -X GET http://localhost:8082/api/v1/protected-endpoint \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Decoding JWT (for debugging)

Use https://jwt.io or command line:

```bash
# Extract and decode payload
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
echo $TOKEN | cut -d'.' -f2 | base64 -d | jq
```

### Testing Checklist

- [ ] Generate JWT_SECRET and set environment variable
- [ ] Start application and verify it starts without JWT errors
- [ ] Request OTP for valid phone number
- [ ] Verify OTP with correct code (or bypass code in test mode)
- [ ] Confirm JWT contains expected claims (sub, keycloakId, communityId)
- [ ] Register new user via self-service endpoint
- [ ] Verify registered user receives JWT immediately
- [ ] Test OTP expiration by waiting 5+ minutes
- [ ] Test too many attempts error (3+ wrong OTPs)
- [ ] Test invalid phone number format
- [ ] Test missing communityId in Keycloak

---

## References

- [JWT.io](https://jwt.io) - JWT specification and debugging
- [Keycloak Documentation](https://www.keycloak.org/documentation) - User attributes
- [OWASP Password Guidelines](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [RFC 7519 - JSON Web Token (JWT)](https://tools.ietf.org/html/rfc7519)

