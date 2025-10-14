# AU Fondue Documentation

![System Architecture](./AUFondue%20-%20System%20Architecture.png)

## Quick Start

### Backend
```bash
cd aufonduebackend/aufonduebackend
mvn spring-boot:run
```

### Frontend
```bash
cd aufondue-frontend
npm install
npm run dev
```

### Mobile
```bash
cd AUFondue
./gradlew assembleDebug
```

## Documentation
- [Backend Setup](./developer/backend-setup.md) - Spring Boot API
- [Frontend Setup](./developer/frontend-setup.md) - Next.js Dashboard
- [Mobile Setup](./developer/mobile-app-setup.md) - Android App

## What It Does
- **Mobile App**: Users report facility issues with photos
- **Web Dashboard**: Admins assign issues to staff, staff update status
- **Backend**: Handles all data, files go to Azure Blob Storage

## Tech Stack
- Backend: Spring Boot + PostgreSQL + Azure
- Frontend: Next.js + Firebase Auth
- Mobile: Kotlin + Jetpack Compose + Microsoft Auth