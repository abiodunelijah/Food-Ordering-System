# Food Ordering System

A full-stack food ordering application with a Spring Boot backend and a React + Vite frontend.

This repository contains:
- backend/ — Spring Boot (Maven) backend
- frontend/ — React (Vite) frontend

---

## Features
- User authentication and roles
- Menu management
- Shopping cart and checkout
- Order processing
- AWS S3 integration for file storage
- Stripe payment integration
- Email notifications

---

## Table of Contents
- Prerequisites
- Environment variables
- Backend: build & run
- Frontend: build & run
- Troubleshooting
  - S3 placeholder errors
  - esbuild platform binary errors
- Tests
- Contributing
- License

---

## Prerequisites
- Java 17+ (or the version used by the project)
- Maven (the project uses the included Maven wrapper `mvnw.cmd` on Windows)
- Node.js (16+ recommended) and npm (or pnpm/yarn if you prefer)
- A running PostgreSQL instance (or update `spring.datasource.url` to your DB)

On Windows PowerShell, verify:

```powershell
java -version
.\mvnw.cmd -v
node -v
npm -v
```

---

## Environment variables
The application expects several environment variables. You can set them in PowerShell for the current session like:

```powershell
$env:S3_REGION = "us-east-1"
$env:S3_BUCKET = "my-bucket"
$env:ACCESS_KEY_ID = "<your-aws-access-key-id>"
$env:SECRET_KEY = "<your-aws-secret-key>"
$env:MAIL_USERNAME = "your-email@example.com"
$env:MAIL_PASSWORD = "email-password"
$env:SECRETE_JWT = "a-strong-secret"
$env:BASE_PAYMENT_LINK = "http://localhost:8080/payment"
$env:FRONTEND_URL = "http://localhost:5173"
$env:STRIPE_PUBLIC_KEY = "pk_test_..."
$env:STRIPE_SECRETE_KEY = "sk_test_..."
```

Note: `aws.s3.region` now has a default value set in `backend/src/main/resources/application.properties` so the backend will not fail to start if `S3_REGION` is not set. The default is `us-east-1`.

---

## Backend: build & run (Windows PowerShell)
From the repository root:

Build (skip tests if you want a faster build):

```powershell
cd backend
.\mvnw.cmd clean package -DskipTests
```

Run tests:

```powershell
cd backend
.\mvnw.cmd test
```

Run in dev mode (ensure required env vars are set as needed):

```powershell
$env:S3_REGION = "us-east-1"
cd backend
.\mvnw.cmd spring-boot:run
```

Run the packaged jar:

```powershell
cd backend
java -jar target\*.jar
```

---

## Frontend: build & run
The frontend uses Vite. From the `frontend` folder:

Install dependencies (recommended to use `npm ci` if you have a lockfile):

```powershell
cd frontend
npm ci
```

Start dev server:

```powershell
npm run dev
```

Build production:

```powershell
npm run build
```

Preview production build:

```powershell
npm run preview
```

### Fixing esbuild platform binary errors
If you see an error like:

"You installed esbuild for another platform than the one you're currently using. This won't work because esbuild is written with native code and needs to install a platform-specific binary executable." — it means the esbuild native binary is for a different OS/CPU.

Try the following from the `frontend` folder (PowerShell):

```powershell
cd frontend
# Rebuild the esbuild binary for this platform
npm rebuild esbuild --update-binary
# or reinstall dependencies cleanly
Remove-Item -Recurse -Force node_modules
Remove-Item package-lock.json -ErrorAction SilentlyContinue
npm install
```

If you cloned this repo from a system that committed `node_modules`, delete that folder and reinstall.

---

## Troubleshooting

### S3 placeholder error on backend startup
If Spring fails with "Could not resolve placeholder 'S3_REGION' in value \"${S3_REGION}\"" the simplest fixes are:

- Provide the `S3_REGION` environment variable before starting the app:

```powershell
$env:S3_REGION = "us-east-1"
.\mvnw.cmd spring-boot:run
```

- Or rely on the default: `backend/src/main/resources/application.properties` now contains `aws.s3.region=${S3_REGION:us-east-1}` so the service will fall back to `us-east-1` if `S3_REGION` is unset.

### esbuild platform binary error (frontend)
See the section above "Fixing esbuild platform binary errors".

---

## Tests
- Backend unit tests: `cd backend; .\mvnw.cmd test`
- Frontend tests: (none configured by default)

---

## Contributing
Feel free to open issues or pull requests. Please follow best practices: create small commits, keep changes scoped, and update this README when you add new env variables or setup steps.

---

## License
Include your license here. (Update this section as needed.)

