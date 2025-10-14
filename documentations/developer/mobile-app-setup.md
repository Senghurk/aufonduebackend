# Mobile App Setup

## Prerequisites
- Android Studio
- JDK 17

## Setup
1. Clone repo
2. Open `AUFondue` folder in Android Studio
3. Create `local.properties`:
```properties
MICROSOFT_CLIENT_ID=your-client-id
MICROSOFT_TENANT_ID=your-tenant-id
MICROSOFT_REDIRECT_URI=msauth://edu.au.aufondue/[signature]
```

4. Add `google-services.json` to `app/` folder (get from Firebase)

5. Update API URL in `RetrofitClient.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/" // for emulator
// or your backend URL for real device
```

6. Build and run:
```bash
./gradlew assembleDebug
```

## Key Features
- Microsoft Azure AD login
- Report issues with photos/videos
- View submitted reports
- Track issue status
- Push notifications

## Screens
- `LoginScreen` - Microsoft/email login
- `HomeScreen` - Main dashboard
- `ReportScreen` - Submit new issue
- `ReportsListScreen` - View all reports
- `NotificationScreen` - View notifications
- `ProfileScreen` - User profile

## API Service
Located in `app/src/main/java/edu/au/aufondue/api/ApiService.kt`

Main endpoints:
- Login with Microsoft token
- Create issue with attachments
- Get user's issues
- Update profile

## Building for Release
```bash
./gradlew assembleRelease
```

Make sure to sign the APK with your keystore.