# CEOMS Deployment Guide

## Prerequisites

- Java 21+
- Node.js 20+
- PostgreSQL 16+
- Maven 3.9+
- Docker & Docker Compose (optional)

## Local Development

### 1. Database Setup

```sql
CREATE DATABASE ceoms;
CREATE USER ceoms_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE ceoms TO ceoms_user;
```

### 2. Backend

```bash
cd backend
# Configure application-dev.yml or set environment variables
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=ceoms
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

mvn spring-boot:run
```

Backend runs at: `http://localhost:8080/api/v1`
Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at: `http://localhost:5173`

## Docker Deployment

```bash
docker-compose up --build -d
```

- Frontend: `http://localhost`
- Backend API: `http://localhost:8080/api/v1`
- PostgreSQL: `localhost:5432`

## Production Checklist

- [ ] Change `JWT_SECRET` to a strong 256-bit key
- [ ] Set `SPRING_PROFILES_ACTIVE=prod`
- [ ] Configure SMTP for email notifications
- [ ] Set `ddl-auto=validate` and run migrations
- [ ] Enable HTTPS with reverse proxy (Nginx/Traefik)
- [ ] Configure persistent volume for uploads
- [ ] Set up database backups
- [ ] Configure log aggregation (ELK/Datadog)

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| DB_HOST | PostgreSQL host | localhost |
| DB_PORT | PostgreSQL port | 5432 |
| DB_NAME | Database name | ceoms |
| DB_USERNAME | DB username | postgres |
| DB_PASSWORD | DB password | postgres |
| JWT_SECRET | JWT signing key | (dev default) |
| CORS_ORIGINS | Allowed origins | http://localhost:5173 |
| UPLOAD_DIR | File upload directory | ./uploads |
| MAIL_HOST | SMTP host | localhost |
| FRONTEND_URL | Frontend URL for emails | http://localhost:5173 |

## Default Seed Accounts (dev profile)

| Role | Email | Password |
|------|-------|----------|
| Super Admin | admin@ceoms.edu | Admin@123 |
| Operator | operator@ceoms.edu | Operator@123 |
| Student | student@ceoms.edu | Student@123 |
