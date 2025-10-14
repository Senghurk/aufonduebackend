# AU Fondue - Developer Documentation

Technical documentation for developers maintaining and extending the AU Fondue system.

## 📖 Available Guides

### 1. [Mobile App Development Guide](./mobile-app-setup.md)
Documentation for the AU Fondue Android application.

**Key Components:**
- Kotlin with Jetpack Compose UI
- Microsoft Azure AD authentication
- Issue reporting with media attachments
- Retrofit for API communication

**Quick Start:**
```bash
cd AUFondue
./gradlew assembleDebug
```

---

### 2. [Frontend Development Guide](./frontend-setup.md)
Guide for the Next.js admin and staff dashboard.

**Key Components:**
- Next.js 15.0.4 with React 18
- Firebase Authentication
- Tailwind CSS + shadcn/ui components
- Multi-language support (English/Thai)
- Recharts for data visualization

**Quick Start:**
```bash
cd aufondue-frontend
npm install
npm run dev
```

---

### 3. [Backend API Development Guide](./backend-setup.md)
Documentation for the Spring Boot backend service.

**Key Components:**
- Spring Boot 3.3.6 with Java 17
- PostgreSQL on Azure Database
- Azure Blob Storage for files
- Firebase Admin SDK
- SpringDoc OpenAPI

**Quick Start:**
```bash
cd aufonduebackend/aufonduebackend
mvn spring-boot:run
```

---

## 🔧 Development Environment

### Required Software
- **JDK 17** - Backend development
- **Node.js 18+** - Frontend development
- **Android Studio** - Mobile development
- **PostgreSQL 14+** - Database (or use Azure Database)
- **Maven 3.8+** - Backend build tool

### Project Structure
```
SeniorProject1CSX3010/
├── AUFondue/                    # Android app
│   ├── app/src/main/java/      # Kotlin source code
│   └── app/src/main/res/        # Resources
├── aufondue-frontend/           # Next.js dashboard
│   ├── app/                     # App router pages
│   ├── components/              # UI components
│   └── public/                  # Static assets
└── aufonduebackend/
    └── aufonduebackend/        # Spring Boot API
        └── src/main/java/      # Java source code
```

## 🏗️ System Architecture

See the architecture diagram: [System Architecture](../AUFondue%20-%20System%20Architecture.png)

The system consists of:
- **Frontend**: Next.js web application for admin/staff
- **Backend**: Spring Boot REST API on Azure
- **Mobile**: Android app for issue reporting
- **Database**: PostgreSQL on Azure Database
- **Storage**: Azure Blob Storage for media files
- **Auth**: Firebase Authentication + Microsoft Azure AD

## 📊 Database Schema

### Main Entities
- **Admin** - System administrators
- **Staff** - Maintenance staff members
- **Issue** - Reported facility issues
- **IssueUpdate** - Status updates and comments
- **Attachment** - Photos/videos attached to issues

## 🔐 Authentication

### Mobile App
- Microsoft Azure AD OAuth 2.0 for @au.edu accounts
- Firebase fallback authentication

### Web Dashboard
- Firebase Authentication (email/password)
- Role-based access (Admin, Staff)

## 🧪 Testing

### Backend
```bash
mvn test
```

### Frontend
```bash
npm test
```

### Mobile
```bash
./gradlew test
```

## 📦 Deployment

### Backend Deployment
The backend is containerized with Docker:
```bash
docker build -t aufondue-backend .
docker run -p 8080:8080 aufondue-backend
```

### Frontend Deployment
```bash
npm run build
npm start
```

## 🐛 Common Issues

### Backend Issues
- **Database connection**: Check PostgreSQL credentials in `application.properties`
- **Firebase errors**: Ensure `firebase-service-account.json` is present
- **Azure Storage**: Verify connection string in properties

### Frontend Issues
- **API connection**: Check backend URL in `/app/config/api.js`
- **Auth errors**: Verify Firebase configuration

### Mobile Issues
- **Build errors**: Sync project with Gradle files
- **Auth failures**: Check Azure AD configuration in `local.properties`

---

*For detailed setup instructions, see the individual guides for each component.*

*Last updated: October 2025*