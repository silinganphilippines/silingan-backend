# Silingan Backend

A Spring Boot backend application for community management with Keycloak authentication.

## Prerequisites

- **Java 21** - Required for building and running the application
- **Maven 3.9+** - For dependency management and building
- **Docker & Docker Compose** - For running Keycloak and PostgreSQL
- **Git** - For version control


## Running with Docker Compose

### Step 1: Start PostgreSQL and Keycloak

The `docker-compose.yml` sets up two services:
- **PostgreSQL** (port 5432) - Database for Keycloak
- **Keycloak** (port 8080) - Authentication server

```bash
# Start the services
docker-compose up -d

# Check if services are running
docker-compose ps

# View logs
docker-compose logs -f keycloak
```

### Step 2: Wait for Keycloak to be Ready

Keycloak may take 30-60 seconds to start. Check when it's ready:

```bash
# Check Keycloak health
curl -f http://localhost:8080/realms/master
```

## Keycloak Configuration

### Realm Setup

The application expects a realm named **`silingan-platform`**. You need to create this realm in Keycloak.

#### Creating the Realm

1. Access Keycloak Admin Console: http://localhost:8080
2. Login with credentials:
   - Username: `admin`
   - Password: `admin`
3. Click **Realm Settings** → **Create Realm**
4. Enter `silingan-platform` as the realm name
5. Click **Create**

### Client Configuration

#### 1. Backend Client (for API authentication)

Create a client for the backend service:

1. Go to **Clients** → **Create Client**
2. **Client ID**: `silingan-backend`
3. **Name**: `silingan-backend`
4. **Client Authentication**: `ON`
5. **Authentication Flow**: `Service account roles`
6. Click **Next/Save**
7. Go to **Credentials** tab and copy the **Client Secret**
8. Place the client secret in the application.yaml under keycloak.client-secret

#### 2. API Client (for OAuth2 login)

Create a client for OAuth2 authentication:

1. Go to **Clients** → **Create Client**
2. **Client ID**: `silingan-api`
3. **Name**: `silingan-api`
4. Click **Next**
5. **Client Authentication**: `ON`
6. **Authentication Flow**: `Direct Access Grants`
7. Click **Next**
8. **Valid Redirect URIs**: `http://localhost:{FE_PORT}/login/oauth2/code/silingan`
9. **Web Origins**: `http://localhost:{FE_PORT}`
10. Click **Save**
11. Go to **Credentials** tab and copy the **Client Secret**
12. Place the client secret in the application.yaml under security.oauth2.client-secret
13. Update the application.yaml: security.oauth2.redirect-uri to `http://localhost:{FE_PORT}/login/oauth2/code/silingan`


#### 3. Client for Mobile App

For mobile app, create another client:
1. Go to **Clients** → **Create Client**
2. Client ID: `silingan-mobile`
3. Name: `silingan-mobile`
4. Client Authentication: `OFF`
5. Authentication Flow: `Standard Flow`
6. Valid redirect URIs: `silingan://callback`
6. Click **Next/Save**


### Realm Roles

Create the following realm roles in Keycloak:

1. Go to **Realm Settings** → **Roles** → **Add Role**
2. Create these roles:
   - `PLATFORM_ADMIN` - Full system access
   - `COMMUNITY_ADMIN` - Community management access
   - `RESIDENT` - Resident/user access

### Initial Users

Create a test user with appropriate roles:

1. Go to **Users** → **Add User**
2. Set username (e.g., `admin-user`)
3. Go to **Credentials** tab and set a password
4. uncheck "Temporary" to make the password permanent
4. Go to **Role Mappings** tab
5. Assign realm roles: `PLATFORM_ADMIN`

## Running the Application

### Development Mode

The application uses H2 database for local development.

```bash
# Build the application
./mvnw clean package

# Run the application
./mvnw spring-boot:run
```

Or with Java directly:

```bash
# Build
mvn clean package

# Run
java -jar target/silingan-0.0.1.jar
```

The application will start on port **8082** by default.


### H2 Console

For development, the H2 console is available at:
- URL: http://localhost:8082/h2-console
- JDBC URL: `jdbc:h2:file:./data/silingan-db`
- Username: `sa`
- Password: (empty)


### Initial Data

The `data.sql` file seeds the database with:
- Sample addresses
- Two tenants (San Isidro LGU, Tower A Property Management)
- Two communities (Barangay San Isidro, Silingan Tower A)
- Sample users and their community roles
- Issue categories

## Troubleshooting

### Keycloak Connection Issues

If the application fails to connect to Keycloak:
1. Ensure Keycloak is running: `docker-compose ps`
2. Check Keycloak logs: `docker-compose logs keycloak`
3. Verify the realm `silingan-platform` exists
4. Verify the client `silingan-backend` exists with correct secret

### Database Issues

If you see database errors:
1. Check if `./data/silingan-db` files exist and are writable
2. For H2 issues, delete the `data/` directory to reset the database

### Port Conflicts

If port 8082 is in use:
```bash
# Run on a different port
SERVER_PORT=8083 ./mvnw spring-boot:run
```
