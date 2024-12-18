# AU Fondue Backend [Senior Project - 1]

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
After starting the application, access the Swagger UI documentation at:
`http://localhost:8080/swagger-ui.html`

## Key Features
- Issue management (CRUD operations)
- Photo upload and storage
- Geolocation-based issue tracking
- User authentication and authorization
- Real-time notifications (planned)

## API Endpoints

### Issues
- `POST /api/issues` - Create a new issue report
- `GET /api/issues` - Get all issues
- `GET /api/issues/{id}` - Get issue by ID
- `PUT /api/issues/{id}` - Update an issue
- `DELETE /api/issues/{id}` - Delete an issue
- `GET /api/issues/nearby` - Get issues near a location

## Security
The application uses Spring Security with the following features:
- CORS configuration
- CSRF protection (disabled for development)
- Stateless session management
- Azure Active Directory integration (planned)

## Development Guidelines

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
This project is part of AU Fondue, a Senior project - 1 at Assumption University.

## Contact
For questions or support, contact the development team:
- Developer/Contributor : [Sai Oan Hseng Hurk]
- Team : [UniMend]
