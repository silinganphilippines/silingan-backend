# Quick Reference Guide

Cheat sheet for common developer tasks in Silingan Backend.

## JWT Secret Setup

**Generate a new secret:**
```bash
openssl rand -base64 32
```

**Use the secret:**
```bash
export JWT_SECRET="your-generated-secret-here"
java -jar target/silingan-0.0.1.jar
```

**Error if missing:**
```
JwtEncodingException: The secret length must be at least 256 bits
```

Solution: Generate and export `JWT_SECRET` before running

---

## Running the Application

**Development (local):**
```bash
export JWT_SECRET="$(openssl rand -base64 32)"
docker-compose up -d
mvn clean package
java -jar target/silingan-0.0.1.jar
```

**Check if running:**
```bash
curl http://localhost:8082/swagger-ui/index.html
```

**Different port:**
```bash
export SERVER_PORT=8083
java -jar target/silingan-0.0.1.jar
```

---

## OTP Testing

**Send OTP (initiates SMS):**
```bash
curl -X POST http://localhost:8082/api/v1/otp/request \
  -H "Content-Type: application/json" \
  -d '{"mobileNumber": "+639171234567"}'
```

**Verify OTP (in bypass/test mode, use `000000`):**
```bash
curl -X POST http://localhost:8082/api/v1/auth/login/otp \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "+639171234567",
    "otp": "000000"
  }'
```

**Response contains JWT:**
```json
{
  "accessToken": "eyJ...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "communityId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## Self-Service Registration

**3-step flow (no registration proof exchange):**
1. `POST /api/v1/auth/otp/request`
2. `POST /api/v1/auth/otp/verify-registration`
3. `POST /api/v1/auth/register/self-service` (returns JWT immediately)

**Register new user (immediate JWT):**
```bash
curl -X POST http://localhost:8082/api/v1/auth/register/self-service \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
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
  }'
```

---

## JWT Token Usage

**Decode JWT online:**
- Go to https://jwt.io
- Paste your token in the top field
- Check claims on the right

**Decode JWT in terminal:**
```bash
TOKEN="eyJ..."
echo $TOKEN | cut -d'.' -f2 | base64 -d | jq
```

**Use JWT in requests:**
```bash
curl -X GET http://localhost:8082/api/v1/protected-endpoint \
  -H "Authorization: Bearer eyJ..."
```

---

## Common JWT Claims

| Claim | Description | Example |
|-------|-------------|---------|
| `sub` | App user ID | `550e8400-e29b-41d4-a716-...` |
| `keycloakId` | Keycloak user ID | `abc123-def456-ghi789` |
| `mobileNumber` | Phone number | `+639171234567` |
| `communityId` | Active community (Keycloak source) | `550e8400-e29b-41d4-a716-...` |
| `roles` | User roles | `["RESIDENT"]` |
| `exp` | Expiration time (Unix) | `1626471999` |

---

## Keycloak Operations

**Access Keycloak Admin:**
http://localhost:8080
- Username: `admin`
- Password: `admin`

**Create realm:**
1. Click "Master" dropdown → "Create Realm"
2. Name: `silingan-platform`
3. Click "Create"

**View user attributes:**
1. Go to Users → select user
2. Click "Attributes" tab
3. See `communityId`, `mobileNumber`, etc.

**Update user attribute:**
1. Users → select user → Attributes
2. Edit `communityId` value
3. Click "Add" or "Update"
4. Click "Save"

---

## Database Operations

**Reset H2 database (dev only):**
```bash
rm -rf ./data/
# Restart application - it will recreate with seed data
```

**Seed data location:**
```
src/main/resources/data.sql
```

**Check H2 console (dev only):**
http://localhost:8082/h2-console
- URL: `jdbc:h2:file:./data/silingan-db`
- User: `sa`
- Password: (empty)

---

## Environment Variables

**Set for development:**
```bash
export JWT_SECRET="your-secret-key"
export SERVER_PORT="8082"
export SPRING_PROFILES_ACTIVE="dev"
```

**Set for testing:**
```bash
export DATABASE_URL="jdbc:postgresql://localhost:5432/silingan"
export DATABASE_USERNAME="postgres"
export DATABASE_PASSWORD="password"
export KEYCLOAK_URL="http://localhost:8080"
export KEYCLOAK_REALM="silingan-platform"
```

---

## Phone Number Format

**Supported formats (auto-normalized to +63X):**
- `09171234567` → `+639171234567`
- `9171234567` → `+639171234567`
- `639171234567` → `+639171234567`
- `+639171234567` → `+639171234567`

---

## Error Troubleshooting

**OTP middleware note:** if a user already passed OTP verification, protected endpoints can still be allowed by user-level verification fallback even when JWT `jti` changes.

| Error | Cause | Solution |
|-------|-------|----------|
| `JwtEncodingException: secret length must be at least 256 bits` | Missing JWT_SECRET | Export JWT_SECRET |
| `No active community selected for user` | communityId not in Keycloak | Add communityId to user attributes in Keycloak |
| `INVALID_OTP` | Wrong OTP code | Check SMS or bypass code (000000) in test |
| `EXPIRED_OTP` | OTP is expired (5+ min old) | Request new OTP |
| `TOO_MANY_ATTEMPTS` | Too many wrong attempts | Wait 60 seconds before retry |
| `User not found for mobile number` | Phone not registered | Register user first via self-service |
| `Username already exists` | Username taken | Use different username |

---

## Useful Commands

**Build application:**
```bash
mvn clean package
```

**Run tests:**
```bash
mvn test
```

**Run specific test:**
```bash
mvn -Dtest=JwtServiceImplTest test
```

**Format code:**
```bash
mvn spotless:apply
```

**Check dependencies for vulnerabilities:**
```bash
mvn dependency-check:check
```

**View dependency tree:**
```bash
mvn dependency:tree
```

**Run application with Maven:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--JWT_SECRET=$JWT_SECRET"
```

---

## Documentation Files

| File | Purpose |
|------|---------|
| `README.md` | Main project documentation |
| `docs/AUTHENTICATION.md` | Complete auth & JWT guide |
| `docs/COMMUNITY_ID_ARCHITECTURE.md` | Technical deep-dive on communityId |
| `QUICK_REFERENCE.md` | This file - quick lookups |

---

## Links

- **Swagger UI:** http://localhost:8082/swagger-ui/index.html
- **Keycloak Admin:** http://localhost:8080
- **H2 Console:** http://localhost:8082/h2-console (dev only)
- **JWT.io:** https://jwt.io (decode online)
- **OpenSSL Docs:** https://www.openssl.org/docs/

---

## Getting Help

1. Check docs/AUTHENTICATION.md for auth questions
2. Check docs/COMMUNITY_ID_ARCHITECTURE.md for design questions
3. Look at error messages and check Troubleshooting above
4. Check Swagger UI for endpoint details
5. Run tests to verify setup: `mvn test`

