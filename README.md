# Food Ordering System

Full-stack food ordering system (Spring Boot backend + Vite/React frontend).

This repository contains two main projects:

- `backend` — Spring Boot application (Java, Maven) that provides REST APIs, authentication, AWS S3 file uploads, Stripe payments, email notifications, and PostgreSQL persistence.
- `frontend` — Vite + React frontend that consumes the backend APIs.

This README explains how to build, run, and configure the project and includes troubleshooting steps for common errors (including the "Could not resolve placeholder 'S3_REGION'" error).

---

## Table of contents

- Project structure
- Requirements
- Environment variables (complete list)
- Fixing the S3_REGION placeholder error
- Backend: build & run
- Frontend: build & run
- Tests
- Common troubleshooting
- Contributing
- License

---

## Project structure

Top-level folders:

- `backend/` — Spring Boot application (Maven)
  - `src/main/java/...` — Java source
  - `src/main/resources/application.properties` — Spring configuration
  - `pom.xml` — Maven build file

- `frontend/` — Vite + React app
  - `package.json`, `vite.config.js`, `src/` — frontend code

---

## Requirements

- Java JDK matching the `pom.xml` setting. The `backend/pom.xml` currently sets `<java.version>25</java.version>` — ensure you have a matching JDK installed or update the pom to match your installed JDK (for example 17 or 21).
- Maven (or use the included `mvnw`/`mvnw.cmd` wrapper in `backend/`).
- Node.js 18+ and npm/yarn (for frontend).
- PostgreSQL database (or update `application.properties` to point to your DB).
- (Optional) AWS account + S3 bucket if using file uploads.
- (Optional) Stripe account for payments and SMTP credentials for email notifications.

---

## Environment variables (complete list used by the project)

These variables are referenced from `backend/src/main/resources/application.properties`:

- SECRETE_JWT — secret used for signing JWT tokens (mapped in properties as `secretJwtString = ${SECRETE_JWT}`)
- MAIL_USERNAME — email account username used by Spring Mail
- MAIL_PASSWORD — email account password used by Spring Mail
- S3_REGION — AWS S3 region used by AWS SDK
- S3_BUCKET — AWS S3 bucket name
- ACCESS_KEY_ID — AWS access key id
- SECRET_KEY — AWS secret access key

Also database values are set directly in `application.properties` (you may prefer to move these to env vars):

- spring.datasource.url (currently: `jdbc:postgresql://localhost:5432/food_db`)
- spring.datasource.username (currently: `coder2client`)
- spring.datasource.password (currently: `pastoral2u`)

Important: Spring resolves property placeholders like `${S3_REGION}` at startup. If any referenced environment variable or property is missing, Spring will fail to start with the "Could not resolve placeholder" error.

---

## Fixing the "Could not resolve placeholder 'S3_REGION'" error

Error example:

  Could not resolve placeholder 'S3_REGION' in value "${S3_REGION}" <-- "${aws.s3.region}"

What it means: your `application.properties` contains a line like:

  aws.s3.region = ${S3_REGION}

When Spring resolves `${aws.s3.region}` it reads the right-hand expression and tries to substitute `${S3_REGION}` from environment variables or other property sources. If `S3_REGION` is not defined, Spring throws the placeholder resolution error.

How to fix (pick one that fits your workflow):

1) Set the environment variable (temporary, for current PowerShell session):

```powershell
$env:S3_REGION = 'us-east-1'  # or your AWS region, e.g. eu-west-1
$env:S3_BUCKET = 'your-bucket'
$env:ACCESS_KEY_ID = 'AKIA...'
$env:SECRET_KEY = '...' 
# Run the backend in the same terminal so the env vars are available
.\backend\mvnw.cmd spring-boot:run
```

2) Set env variables permanently (Windows): use System Settings > Environment Variables, or set them in PowerShell profile, or use setx (requires reopening terminal):

```powershell
setx S3_REGION "us-east-1"
setx S3_BUCKET "your-bucket"
# Note: setx requires opening a new terminal to take effect
```

3) Provide a default value in `application.properties` so startup won't fail when the env var is missing. Open `backend/src/main/resources/application.properties` and change the line to include a default:

```
aws.s3.region=${S3_REGION:us-east-1}
```

This tells Spring: use the environment variable S3_REGION if available, otherwise fall back to `us-east-1`.

4) Pass the value as a JVM property on startup:

```powershell
.\backend\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-DS3_REGION=us-east-1 -DACCESS_KEY_ID=... -DSECRET_KEY=..."
```

5) Put the values directly into `application.properties` (not recommended for secrets):

```
aws.s3.region=us-east-1
aws.s3.bucket=your-bucket
aws.accessKeyId=YOUR_KEY
aws.secretKey=YOUR_SECRET
```

6) Use AWS credentials provider chain (recommended for production): configure `~/.aws/credentials` and let the AWS SDK pick credentials from profile or environment.

Notes on property names: the application uses `aws.s3.region` as its property key. That key is assigned from `${S3_REGION}` in the repository's properties file. If you prefer, invert that and set `S3_REGION` once and keep property mapping as-is.

---

## Backend: build & run

From project root (Windows PowerShell):

1) Change to the backend folder

```powershell
cd .\backend
```

2) Use the included Maven wrapper to run the app (this uses the project's wrapper and avoids requiring a system Maven):

```powershell
# Ensure required env vars are set (see above). Then run:
.\mvnw.cmd spring-boot:run
```

3) Or build a jar and run it:

```powershell
.\mvnw.cmd clean package -DskipTests
# Find the built jar under target (artifact id + version). For example:
java -jar target\backend-0.0.1-SNAPSHOT.jar
```

If you get placeholder resolution errors on startup, follow the troubleshooting section above.

Important: The `pom.xml` currently sets `<java.version>25</java.version>` — if you do not have JDK 25 installed, either install the matching JDK or update `pom.xml` to a version you have (for example 17 or 21).

---

## Frontend: build & run

1) Change to the frontend folder

```powershell
cd .\frontend
```

2) Install dependencies and run the dev server

```powershell
npm install
npm run dev
```

3) Build for production

```powershell
npm run build
# Preview production build
npm run preview
```

By default the frontend will attempt to call the backend API URL you configure in the client (check `src/` for any base URL). You can run both frontend and backend locally; configure CORS in backend if needed.

---

## Tests

- Backend tests are in `backend/src/test/java/` (Spring Boot tests). Run them with Maven:

```powershell
cd .\backend
.\mvnw.cmd test
```

- Frontend tests (if any) follow the tooling you chose (e.g., Jest, Vitest).

---

## Common troubleshooting

- Placeholder resolution errors: set missing env vars or provide defaults as shown above.
- AWS S3 upload errors: verify `S3_REGION`, `S3_BUCKET`, and AWS credentials are available to the JVM or present in `~/.aws/credentials`.
- Email issues: verify `MAIL_USERNAME` and `MAIL_PASSWORD`. For Gmail, ensure the account allows SMTP (app passwords or less-secure-access settings may be required).
- Database: ensure PostgreSQL is running and the credentials in `application.properties` match a valid DB and user.
- Java version mismatch: if `mvnw` fails due to Java version, update `pom.xml` or install the JDK matching `<java.version>`.

---

## Contributing

1. Fork the repo and create a feature branch
2. Make changes and add tests where appropriate
3. Open a pull request describing your changes

---

## License

This project does not include a license file. Add one (for example MIT) if you intend to open-source the project.

---

If you'd like, I can:

- Add a `.env.example` with all required env vars (without secrets),
- Add a small startup script for Windows/macOS/Linux that sets required env vars for development, or
- Update `application.properties` to provide sensible defaults and avoid startup failure when env vars are missing.

Tell me which of the above you'd like and I'll implement it.

