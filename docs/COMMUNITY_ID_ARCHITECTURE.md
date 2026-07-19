# Community ID Architecture

Technical documentation for how `communityId` is managed as the single source of truth using Keycloak user attributes.

## Overview

The `communityId` represents a user's currently active community. It is managed **exclusively through Keycloak user attributes** to ensure:
- 🔒 Consistency - Single source of truth
- 🔄 Synchronization - JWT reflects current state
- 🛡️ Security - Keycloak-backed validation
- 📊 Auditability - Changes tracked in Keycloak

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Silingan Backend                          │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  User Authentication Flow:                                   │
│                                                               │
│  [1] OTP Verification                                        │
│       ↓                                                       │
│  [2] Fetch User from Database                               │
│       ↓                                                       │
│  [3] Query Keycloak User Attributes ←─────────┐            │
│       │                                         │            │
│       │ (GET communityId from Keycloak)       │            │
│       │                                         │            │
│       ↓                                         │            │
│  [4] Validate communityId exists               │            │
│       ↓                                         │            │
│  [5] Generate JWT with communityId ←──────────┤            │
│       ↓                                         │            │
│  [6] Return LoginResponse                      │            │
│                                                 │            │
│                                                 │            │
│  Community Switch Flow:                        │            │
│                                                 │            │
│  [1] User requests: POST /communities/{id}/switch           │
│       ↓                                         │            │
│  [2] Update Keycloak user attribute ──────────→│            │
│       │                                         │            │
│       │ (SET communityId in Keycloak)         │            │
│       │                                         │            │
│       ↓                                         │            │
│  [3] Return 204 No Content                     │            │
│       ↓                                         │            │
│  [4] Next JWT generated will use new ID ◄─────┘            │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                          ↕
┌─────────────────────────────────────────────────────────────┐
│                    Keycloak (IdP)                            │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  User Representation:                                        │
│  {                                                            │
│    "id": "abc123-def456-ghi789",                           │
│    "username": "jdelacruz",                                │
│    "email": "juan@example.com",                            │
│    "attributes": {                                          │
│      "communityId": [                                       │
│        "550e8400-e29b-41d4-a716-446655440000"             │
│      ],                                                      │
│      "mobileNumber": ["+639171234567"],                   │
│      "phone_number": ["+639171234567"],                   │
│      "phone_number_verified": ["true"]                     │
│    },                                                        │
│    "realmRoles": ["RESIDENT"],                            │
│    "createdTimestamp": 1626468399000                       │
│  }                                                            │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                          ↕
┌─────────────────────────────────────────────────────────────┐
│                 Application Database                         │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  User (stores app identity):                                │
│  - id: UUID (primary key)                                  │
│  - keycloakUserId: String (FK to Keycloak)                │
│  - mobileNumber: String                                     │
│  - firstName, lastName: String                             │
│                                                               │
│  UserCommunity (many-to-many relationship):                │
│  - userId: UUID (FK)                                       │
│  - communityId: UUID (FK)                                  │
│  - role: Enum (RESIDENT, COMMUNITY_ADMIN, etc.)          │
│                                                               │
│  Community:                                                  │
│  - id: UUID (primary key)                                  │
│  - name: String                                             │
│  - status: Enum (ACTIVE, ARCHIVED, etc.)                  │
│                                                               │
│  NOTE: communityId is NOT stored in User table!           │
│        It's retrieved from Keycloak at login.             │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

## Implementation Details

### 1. User Creation (Registration)

```java
// Service: UserService.createUser()

// 1. Create user in Keycloak
String keycloakUserId = keycloakService.createUser(createUserRequest);

// 2. Initialize Keycloak attributes
Map<String, List<String>> attributes = new HashMap<>();
attributes.put("communityId", List.of(createUserRequest.communityId().toString()));
attributes.put("mobileNumber", List.of(createUserRequest.mobileNumber()));
attributes.put("phone_number", List.of(createUserRequest.mobileNumber()));
attributes.put("phone_number_verified", List.of("true"));

keycloakService.updateUserAttributes(keycloakUserId, attributes);

// 3. Create user in database
User user = new User();
user.setKeycloakUserId(keycloakUserId);
user.setMobileNumber(normalizePhoneNumber(createUserRequest.mobileNumber()));
// ... other fields
userRepository.save(user);

// 4. Create user-community relationship
UserCommunity userCommunity = new UserCommunity();
userCommunity.setUser(user);
userCommunity.setCommunity(community);
userCommunity.setRole(createUserRequest.communityRole());
userCommunityRepository.save(userCommunity);
```

### 2. OTP Login (Resolution)

```java
// Service: AuthenticationServiceImpl.loginWithOtp()

@Transactional
public LoginResponse loginWithOtp(OtpVerifyRequest request, String ipAddress, String userAgent) {
    // Step 1: Verify OTP
    otpService.verifyOtp(request, ipAddress, userAgent);
    
    // Step 2: Find user in database
    User user = userRepository.findByMobileNumber(normalizePhoneNumber(request.getMobileNumber()))
        .orElseThrow(() -> new NotFoundException("User not found for mobile number"));
    
    // Step 3: Resolve active communityId from Keycloak
    UUID communityId = resolveActiveCommunityId(user);
    
    // Step 4: Build JWT with communityId
    return buildLoginResponse(user, communityId);
}

// Helper: Resolve active community from Keycloak
private UUID resolveActiveCommunityId(User user) {
    // Fetch Keycloak user attributes
    Map<String, List<String>> attributes = keycloakService.getUserAttributes(user.getKeycloakUserId());
    List<String> communityIds = attributes.get("communityId");
    
    // Validate community is set
    if (communityIds == null || communityIds.isEmpty() || communityIds.get(0).isBlank()) {
        throw new ValidationException("No active community selected for user");
    }
    
    // Parse and return UUID
    try {
        return UUID.fromString(communityIds.get(0));
    } catch (IllegalArgumentException ex) {
        throw new ValidationException("Invalid communityId value in user profile");
    }
}

// Build response with JWT
private LoginResponse buildLoginResponse(User user, UUID communityId) {
    List<String> roles = keycloakService.getRealmRoles(user.getKeycloakUserId());
    String token = jwtService.generateToken(user, roles, communityId);
    return new LoginResponse(
        token,
        "Bearer",
        jwtProperties.getAccessTokenTtlSeconds(),
        user.getId().toString(),
        user.getKeycloakUserId(),
        user.getMobileNumber(),
        roles
    );
}
```

### 3. Community Switch

```java
// Service: CommunityService.switchCommunity()

@Transactional
public void switchCommunity(UUID communityId) {
    // Validate community exists and is accessible
    Community community = communityRepository.findById(communityId)
        .orElseThrow(() -> new NotFoundException("Community not found"));
    
    if (community.getStatus() == CommunityStatus.ARCHIVED) {
        throw new ConflictException("Cannot switch to archived community");
    }
    
    // Get current user from security context
    String keycloakUserId = UserContextHolder.get().keycloakUserId();
    
    // Update Keycloak attribute (single source of truth)
    keycloakService.updateUserAttributes(
        keycloakUserId,
        Map.of("communityId", List.of(communityId.toString()))
    );
    
    // Next JWT token will automatically include new communityId
    // because it's fetched from Keycloak at token generation time
}
```

### 4. JWT Token Generation

```java
// Service: JwtServiceImpl.generateToken()

@Override
public String generateToken(User user, List<String> roles, UUID communityId) {
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plusSeconds(jwtProperties.getAccessTokenTtlSeconds());
    
    JwtClaimsSet claimsSet = JwtClaimsSet.builder()
        .subject(user.getId().toString())              // App user ID
        .issuer(jwtProperties.getIssuer())
        .issuedAt(issuedAt)
        .expiresAt(expiresAt)
        .id(UUID.randomUUID().toString())
        .claim("keycloakId", user.getKeycloakUserId()) // Keycloak ID
        .claim("mobileNumber", user.getMobileNumber())
        .claim("firstName", user.getFirstName())
        .claim("lastName", user.getLastName())
        .claim("roles", roles)                         // From Keycloak
        .claim("communityId", communityId)             // From Keycloak attribute
        .build();
    
    JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
}
```

## Data Flow Examples

### Example 1: User Registration

```
Request:
POST /api/v1/auth/register/self-service
{
  "username": "jdelacruz",
  "email": "juan@example.com",
  "firstName": "Juan",
  "lastName": "Dela Cruz",
  "mobileNumber": "+639171234567",
  "communityId": "550e8400-e29b-41d4-a716-446655440000",  ← User's initial community
  ...
}

Processing:
1. Create Keycloak user (jdelacruz)
2. Set Keycloak attributes:
   {
     "communityId": ["550e8400-e29b-41d4-a716-446655440000"],
     "mobileNumber": ["+639171234567"],
     ...
   }
3. Create database user (keycloakUserId: "abc123...")
4. Create UserCommunity relationship

Response:
{
  "accessToken": "eyJ....", 
  "communityId": "550e8400-e29b-41d4-a716-446655440000",  ← From Keycloak
  "roles": ["RESIDENT"]
}
```

### Example 2: OTP Login

```
Request:
POST /api/v1/auth/login/otp
{
  "mobileNumber": "+639171234567",
  "otp": "123456"
}

Processing:
1. Verify OTP is correct
2. Find user by mobile number
   User { id: "550e8400-...", keycloakUserId: "abc123-..." }
3. Query Keycloak for user attributes
   GET /admin/realms/silingan-platform/users/abc123-.../
   Response: {
     "attributes": {
       "communityId": ["550e8400-e29b-41d4-a716-446655440000"],  ← Active community
       ...
     }
   }
4. Generate JWT with communityId from Keycloak
5. Return token

Response:
{
  "accessToken": "eyJ....",
  "communityId": "550e8400-e29b-41d4-a716-446655440000",  ← From Keycloak
  "roles": ["RESIDENT"],
  "mobileNumber": "+639171234567"
}
```

### Example 3: Community Switch

```
Request:
POST /api/v1/communities/550e8400-e29b-41d4-a716-446655440001/switch
Authorization: Bearer eyJ....

Processing:
1. Validate user context (from JWT)
2. Validate community exists and is not archived
3. Update Keycloak user attribute:
   PUT /admin/realms/silingan-platform/users/abc123-.../
   {
     "attributes": {
       "communityId": ["550e8400-e29b-41d4-a716-446655440001"],  ← New community
       ...
     }
   }

Response:
204 No Content

Next Request:
POST /api/v1/protected-endpoint
Authorization: Bearer {OLD_TOKEN}

Processing:
1. Old token still valid (expires in original TTL)
2. User context reflects old communityId
3. When token expires and user logs in again:
   - Fresh JWT generated
   - New Keycloak attribute "550e8400-e29b-41d4-a716-446655440001" is read
   - New JWT contains new communityId
```

## Key Design Decisions

### Why Keycloak for communityId?

| Aspect | Reason |
|--------|--------|
| **Single Source of Truth** | Prevents inconsistency between DB and JWT |
| **Immediate Effect** | No DB sync delays; Keycloak is always current |
| **Audit Trail** | Keycloak tracks all attribute changes |
| **JWT Accuracy** | Token always reflects actual state at issuance |
| **No DB Migration** | No need to add `currentCommunityId` to User table |
| **Federation Ready** | Works with external Keycloak instances |

### Why NOT in Database?

```
❌ If stored in User table:
   User {
     id: UUID,
     currentCommunityId: UUID,  ← Must be kept in sync with Keycloak
     ...
   }

   Problem: What if Keycloak changes but DB doesn't?
   → JWT reflects stale communityId
   → Multiple sources of truth
   → Complex synchronization logic needed

✅ Using Keycloak only:
   Keycloak: attributes.communityId = "X"
   → Query Keycloak at JWT generation
   → Always current, no sync issues
   → Single source of truth
```

### Why Validate at Login?

```java
// During OTP login, validate communityId is set
private UUID resolveActiveCommunityId(User user) {
    Map<String, List<String>> attributes = keycloakService.getUserAttributes(user.getKeycloakUserId());
    List<String> communityIds = attributes.get("communityId");
    
    // Fail early if missing
    if (communityIds == null || communityIds.isEmpty()) {
        throw new ValidationException("No active community selected for user");
    }
    ...
}
```

**Why:** Prevents issuing JWTs with null/invalid communityId. Better to fail at login than have broken tokens in circulation.

## Related Code Files

| File | Purpose |
|------|---------|
| `AuthenticationServiceImpl.java` | OTP login & JWT generation |
| `JwtServiceImpl.java` | JWT token creation with claims |
| `KeycloakServiceImpl.java` | Keycloak user attribute management |
| `KeycloakService.java` | Interface: `getUserAttributes()`, `updateUserAttributes()` |
| `CommunityServiceImpl.java` | Community switching logic |
| `UserService.java` | User creation with initial community |

## Testing

### Unit Test: Community ID Resolution

```java
@Test
void loginWithOtp_ShouldResolveCommunityIdFromKeycloak() {
    // Arrange
    User user = User.builder()
        .id(UUID.randomUUID())
        .keycloakUserId("kc-user-123")
        .mobileNumber("+639171234567")
        .build();
    
    UUID expectedCommunityId = UUID.randomUUID();
    Map<String, List<String>> keycloakAttributes = Map.of(
        "communityId", List.of(expectedCommunityId.toString())
    );
    
    // Mock
    when(keycloakService.getUserAttributes("kc-user-123"))
        .thenReturn(keycloakAttributes);
    when(otpService.verifyOtp(...)).thenReturn(true);
    when(userRepository.findByMobileNumber(...)).thenReturn(Optional.of(user));
    
    // Act
    LoginResponse response = authenticationService.loginWithOtp(request, ipAddress, userAgent);
    
    // Assert
    Jwt decodedToken = jwtService.parse(response.accessToken()).orElseThrow();
    assertEquals(expectedCommunityId.toString(), 
        decodedToken.getClaimAsString("communityId"));
}
```

### Integration Test: Community Switch

```java
@Test
void switchCommunity_ShouldUpdateKeycloakAttribute() {
    // Arrange
    UUID newCommunityId = UUID.randomUUID();
    
    // Act
    communityService.switchCommunity(newCommunityId);
    
    // Assert - Verify Keycloak was called
    verify(keycloakService).updateUserAttributes(
        "kc-user-123",
        Map.of("communityId", List.of(newCommunityId.toString()))
    );
}
```

## Troubleshooting

### Issue: "No active community selected for user"

**Cause:** User exists but `communityId` is not set in Keycloak

**Solution:**
```bash
# Check user attributes in Keycloak Admin UI:
1. Go to Users → select user
2. Go to Attributes tab
3. Ensure communityId key has a valid UUID value

# Or via API:
GET /admin/realms/silingan-platform/users/{userId}
# Check response.attributes.communityId
```

### Issue: JWT has stale communityId

**Cause:** Token issued before community switch, now expired but still being used

**Solution:**
- This is expected behavior
- User must authenticate again (login) to get fresh token
- New token will reflect latest Keycloak attributes

### Issue: Community switch succeeds but JWT unchanged

**Cause:** Old JWT still valid; attributes changed in Keycloak but token was already issued

**Solution:**
- This is correct behavior
- Users should not try to use old token after switch
- Next login or token refresh will reflect new community
- Consider implementing token refresh endpoint that re-queries Keycloak

## Future Enhancements

1. **Token Refresh Endpoint**
   - Allow users to refresh JWT without full login
   - Query latest Keycloak attributes
   - Useful after community switch

2. **Multi-Community Access**
   - Allow JWT to include all user's communities
   - Let client select which community to work in
   - Better UX for power users

3. **Community Change Audit**
   - Log all community switches with timestamps
   - Track which user switched to which community
   - Compliance/audit trail

4. **Graceful Switch**
   - Track in-flight requests during community switch
   - Prevent race conditions
   - Queue new requests until attributes synced

