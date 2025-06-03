# Spring Boot Security with ScyllaDB

This project demonstrates a secure, full-featured user authentication and registration system using Spring Boot, JWT, and [ScyllaDB](https://www.scylladb.com/) as the primary data store. It also supports roles/permissions, email verification, and uses modern Java features (Java 21).

---
**Made for testing**
---

## Features

- **User Registration & Login** (with JWT authentication)
- **Role-based access control** (ADMIN, USER, etc.)
- **Email verification** (OTP-based)
- **Role management** (CRUD for roles, only by admins)
- **User profile endpoint**
- **ScyllaDB** as the main database, using the official ScyllaDB Java Driver
- **Redis** for session/token storage
- **MailHog** for email testing
- **Currently, API tests via Postman**

---

## Technology Stack

- **Spring Boot** (Web, Security, Data JPA, Validation, Mail, OAuth2 Resource Server, Redis)
- **ScyllaDB Java Driver** (core, query-builder, mapper)
- **JWT** for authentication (`jjwt`)
- **MapStruct** for DTO mapping
- **Lombok** for boilerplate reduction
- **JUnit & Testcontainers** for testing
- **Redis** for caching/sessions
- **MailHog** for email capture/testing
- **Postman** for API testing

---

## Setup and Configuration

### Prerequisites

- Java 21+
- Maven 3.8+
- [Docker Compose](https://docs.docker.com/compose/) (for running stack easily)
- [ScyllaDB](https://www.scylladb.com/download/) (local or cloud instance, but Docker Compose setup is provided)
- [Redis](https://redis.io/download) (for token/session cache, included in Docker Compose)
- SMTP server for email verification (MailHog in Docker Compose, or configure your own in `application.yml`)

### Clone the Repo

```bash
git clone https://github.com/your-org/spring-boot-security-scylladb.git
cd spring-boot-security-scylladb
```

### Configuration

Edit `src/main/resources/application.yml` (or `.properties`) for:

- **ScyllaDB connection** (contact points, keyspace, credentials)
- **Redis** (if enabled)
- **Mail** (SMTP host, port, username, password)
- **JWT secret and properties**

Example (add your own values):

```yaml
scylla:
  contactPoints: localhost:9042
  keyspace: authdb
  username: scyllauser
  password: secret

spring:
  redis:
    host: localhost
    port: 6379
  mail:
    host: smtp.yourdomain.com
    port: 587
    username: youruser
    password: yourpass
  jwt:
    secret: your-jwt-secret
    expiration: 86400
```

---

## Running with Docker Compose

This project comes with a `docker-compose.yml` that sets up **ScyllaDB**, **Redis**, and **MailHog** alongside your Spring Boot application. This makes it easy to get a full environment running quickly for development or testing.

### Start all services

```bash
docker-compose up -f docker-compose.yml --build
```

- The app will be available at: [http://localhost:8080](http://localhost:8080)
- MailHog (for email testing) UI: [http://localhost:8025](http://localhost:8025)
- Redis is available at: `localhost:6379`
- ScyllaDB CQL is available at: `localhost:9042`

To run everything in the background (detached mode):

```bash
docker-compose -f docker-compose.yml up --build -d
```

#### Stop all services

```bash
docker-compose -f docker-compose.yml down
```

> **Note:** The `docker-compose.yml` will initialize the database schema and seed example roles on first run.

---

## API Usage

### Authentication & User Endpoints

- `POST /api/auth/sign-up` – Register new user
- `POST /api/auth/sign-in` – Login, receive JWT
- `POST /api/auth/request-verification-email` – Resend verification
- `POST /api/auth/verify-email` – Verify email with OTP
- `POST /api/auth/refresh` – Refresh JWT
- `POST /api/auth/sign-out` – Logout
- `POST /api/auth/register-user` – Admin/user creates a new user (role-based logic enforced)
- `GET /api/user/me` – Get current user profile (JWT required)

### Role Endpoints (ADMIN only)

- `GET /api/roles` – List all roles
- `GET /api/roles/{id}` – Get role by ID
- `POST /api/roles` – Create role
- `PUT /api/roles/{id}` – Update role
- `DELETE /api/roles/{id}` – Delete role

---

## Testing

### Postman Collection

A full Postman collection is provided in [Authentication_postman_collection.json](postman/Authentication_postman_collection.json).  
It covers:

- User registration
- Email verification
- Login, refresh, sign out
- Profile retrieval
- Admin role management (create, list, update, delete roles)

**Import this collection into Postman and follow the request order for a typical user/admin flow!**

---

## Security Model

- All sensitive endpoints require JWT authentication.
- Role-based access control is enforced using `@PreAuthorize`.
- Only users with `ROLE_ADMIN` can manage roles.
- Email verification is required before login (unless you modify logic).
- Passwords are hashed before storage.

---

## Customization

- Add more roles as needed via the role management endpoints.
- Extend user or role entities/DTOs as needed.
- Integrate with external SMTP/email providers.
- Use [Scylla Cloud](https://www.scylladb.com/product/scylla-cloud/) for managed DB.

---

## License

[MIT](LICENSE)

---

## Author

Chris-Basson-Grundling

---
