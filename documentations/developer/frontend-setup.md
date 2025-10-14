# Frontend Setup

## Prerequisites
- Node.js 18+
- npm

## Setup
1. Clone repo
2. Install dependencies:
```bash
cd aufondue-frontend
npm install
```

3. Configure `.env.local`:
```env
NEXT_PUBLIC_FIREBASE_API_KEY=your-key
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=your-domain
NEXT_PUBLIC_FIREBASE_PROJECT_ID=your-project
NEXT_PUBLIC_API_URL=http://localhost:8080
```

4. Run:
```bash
npm run dev
```

## Pages
- `/Log-in` - Login page
- `/dashboard` - Main dashboard with stats
- `/reports` - View all issues
- `/assignedReports` - Staff's assigned issues
- `/staff-management` - Manage staff accounts
- `/admins` - Manage admin accounts
- `/daily-report` - Daily report generation

## Key Components
- `AuthContext` - Firebase authentication
- `LanguageContext` - Thai/English translation
- `ThemeContext` - Dark/light mode

## Features
- Issue management (view, assign, update status)
- Staff/admin account management
- Report generation (Excel export)
- Multi-language (Thai/English)
- Charts using Recharts

## Build for Production
```bash
npm run build
npm start
```