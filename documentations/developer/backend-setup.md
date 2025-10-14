# Backend Setup

## Prerequisites
- Java 17
- Maven
- PostgreSQL

## Setup
1. Clone the repo
2. Configure database in `application.properties`:
```properties
# For local dev
spring.datasource.url = jdbc:postgresql://localhost:5432/aufondue_db
spring.datasource.username = postgres
spring.datasource.password = yourpassword
```

3. Add Firebase service account JSON to `src/main/resources/firebase-service-account.json`

4. Run:
```bash
cd aufonduebackend/aufonduebackend
mvn spring-boot:run
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login with email/password
- `POST /api/auth/reset-password` - Reset password

### Issues (User)
- `POST /api/issues` - Create issue with photos/videos
- `GET /api/issues/user/{userId}/submitted` - Get user's issues

### Admin
- `GET /api/admin/issues` - Get all issues
- `PUT /api/admin/issues/{id}/assign` - Assign issue to staff
- `GET /api/admin/users` - Get all users
- `POST /api/admin/users` - Create staff account

### Staff
- `GET /api/staff/assigned` - Get assigned issues
- `PUT /api/staff/issues/{id}/status` - Update issue status
- `POST /api/staff/issues/{id}/remark` - Add remark

## Database Tables
- `admin` - Admin accounts
- `staff` - Staff accounts
- `issue` - Reported issues
- `issue_update` - Status updates
- `attachment` - Photo/video files

## Files
Files are uploaded to Azure Blob Storage. Connection string in `application.properties`.

## Running with Docker
```bash
docker build -t aufondue-backend .
docker run -p 8080:8080 aufondue-backend
```