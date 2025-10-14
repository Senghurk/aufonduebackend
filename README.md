# AU Fondue Backend [ Senior Project - 1 & 2 ]

## Overview
AU Fondue Backend is a Spring Boot application that serves as the server-side component of the AU Fondue campus maintenance reporting system. It provides RESTful APIs for the mobile application and web admin panel, handling issue reporting, and file storage.

## Prerequisites
- Java Development Kit (JDK) 17 or later
- Maven 3.6.3 or later
- PostgreSQL 12 or later
- Docker (for containerization)

## Tech Stack
- Spring Boot 3.3.6
- Spring Security
- Spring Data JPA
- PostgreSQL Database
- Azure Cloud Services Integration
- Springdoc OpenAPI (Swagger UI)

## Getting Started

### Database Setup
1. Install PostgreSQL
2. Create a new database:
```sql
CREATE DATABASE aufondue_db;
```

### Configuration
1. Clone the repository
2. Configure database connection in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/aufondue_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```
3. Add Firebase service account JSON to `src/main/resources/firebase-service-account.json`
4. Configure Azure Blob Storage connection string in `application.properties`

### Building the Project
```bash
mvn clean install
```

### Running the Application
```bash
mvn spring-boot:run
```
The application will start on `http://localhost:8080`

## API Documentation
OpenAPI documentation available at: `http://localhost:8080/swagger-ui.html`

## Key Features
- Issue management with photo/video uploads
- Azure Blob Storage for media files
- Firebase Authentication
- Admin and Staff role management
- Multi-language support

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login with email/password
- `POST /api/auth/reset-password` - Reset password

### Issues (User)
- `POST /api/issues` - Create issue with photos/videos
- `GET /api/issues/user/{userId}/submitted` - Get user's submitted issues

### Admin
- `GET /api/admin/issues` - Get all issues
- `PUT /api/admin/issues/{id}/assign` - Assign issue to staff
- `GET /api/admin/users` - Get all users
- `POST /api/admin/users` - Create staff account

### Staff
- `GET /api/staff/assigned` - Get assigned issues
- `PUT /api/staff/issues/{id}/status` - Update issue status
- `POST /api/staff/issues/{id}/remark` - Add remark to issue

## Database Schema
- `admin` - Admin accounts
- `staff` - Staff accounts
- `issue` - Reported facility issues
- `issue_update` - Status updates and remarks
- `attachment` - Photo/video files metadata

## Security
- Firebase Authentication for user verification
- Spring Security for API protection
- CORS enabled for frontend access
- Azure Blob Storage for secure file storage

## Related Documentation

### Developer Documentation
- [Backend Setup](./documentations/developer/backend-setup.md) - Spring Boot API Setup
- [Frontend Setup](./documentations/developer/frontend-setup.md) - Next.js Dashboard Setup
- [Mobile App Setup](./documentations/developer/mobile-app-setup.md) - Android App Setup
- [Swagger Setup](./documentations/SWAGGER_SETUP.md) - API Documentation Setup

### User Documentation
- [Mobile App Guide](./documentations/user/01-mobile-app-guide.md) - For users reporting issues
- [Admin Dashboard Guide](./documentations/user/02-admin-dashboard-guide.md) - For administrators
- [Staff Dashboard Guide](./documentations/user/03-staff-dashboard-guide.md) - For staff members

### System Architecture
- [Architecture Diagram](./documentations/AUFondue%20-%20System%20Architecture.png) - System overview

## Deployment

### Docker
Build the Docker image:
```bash
docker build -t aufondue-backend .
```

Run the container:
```bash
docker run -p 8080:8080 aufondue-backend
```

### Production Deployment
1. Set appropriate production properties
2. Configure Azure services
3. Deploy using Docker and Kubernetes

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to the branch
5. Create a Pull Request

## License
This project is part of AU Fondue, a Senior project - 1 & 2 at Assumption University.

## Contact
For questions or support, contact the development team:
- Developer/Contributor : [ Sai Oan Hseng Hurk, Min Thant Win]
- Team : [ UniMend ] 
