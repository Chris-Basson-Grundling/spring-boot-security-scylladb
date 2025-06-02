# Codespace Access Guide

Once your Codespace is running with the provided `.devcontainer/docker-compose.yml` setup, you have several ways to access and interact with MailHog, ScyllaDB, Redis, and your Spring Boot app for testing and development.

---

## 1. MailHog

**Web UI**  
- **URL:** [http://localhost:8025](http://localhost:8025)
- **How:** In Codespaces, click the “Ports” tab, find port `8025`, and click “Open in Browser.”
- **What:** Use this web UI to view emails sent by your app.

---

## 2. ScyllaDB

**CQLSH (Scylla Shell):**
- **How:**
  1. Open a terminal in your Codespace.
  2. Enter the ScyllaDB container:
     ```sh
     docker compose exec scylla cqlsh
     ```
  3. You’ll get a `cqlsh` prompt to run CQL (Cassandra Query Language) queries.

- **Alternative:**  
  Use any Cassandra/ScyllaDB GUI client and connect to `localhost:9042`.

---

## 3. Spring Boot App

**API Endpoint (for Postman or Browser):**
- **Base URL:** [http://localhost:8080](http://localhost:8080)

**How:**
- In Codespaces, go to the “Ports” tab, find port `8080`, and open it in your browser (for web endpoints).
- In Postman, use `http://localhost:8080/api/add-endpoint-here` as the base URL.
- Make sure your app is running (`mvn spring-boot:run` or via your IDE).

**API Testing:**
- Use Postman to send requests to your endpoints, e.g.:
  - [API Endpoints for Postman Testing](#api-endpoints-for-postman-testing "Goto API Endpoints for Postman Testing")

---

## 4. Redis

**CLI Access:**
- Open a terminal in your Codespace and run:
  ```sh
  docker compose exec redis redis-cli
  ```
- You can now run Redis commands in the prompt.

---

## Summary Table

| Service   | How to Access (from Codespace)                  | Default Port | Example Usage         |
|-----------|-------------------------------------------------|--------------|----------------------|
| MailHog   | Web UI → Ports tab → 8025 → Open in Browser     | 8025         | View outgoing emails |
| ScyllaDB  | Terminal: `docker compose exec scylla cqlsh`    | 9042         | Run CQL queries      |
| App       | Ports tab → 8080 or Postman/Browse              | 8080         | Test API endpoints   |
| Redis     | Terminal: `docker compose exec redis redis-cli` | 6379         | Redis CLI            |

---

**Tip:**  
If you’re using GitHub Codespaces, the “Ports” tab is your friend—click “Open in Browser” to quickly access any forwarded service UI.

---

# API Endpoints for Postman Testing

Below are the main API endpoints available for authentication and role management.  
Replace `localhost:8080` with your Codespace forwarded port if needed.

## **Authentication Endpoints**

| Name                            | Method | Endpoint                                         | Notes                             |
|----------------------------------|--------|--------------------------------------------------|-----------------------------------|
| Auth - Sign Up                  | POST   | `/api/auth/sign-up`                              | `{ "username", "password", "email" }` in body |
| Auth - Request Verification     | POST   | `/api/auth/request-verification-email?email=...` |                                   |
| Auth - Verify Email (Get Token) | POST   | `/api/auth/verify-email`                         | `{ "email", "otp" }` in body      |
| Auth - Sign In (Get Token)      | POST   | `/api/auth/sign-in`                              | `{ "username", "password" }` in body |
| Auth - Refresh Token            | POST   | `/api/auth/refresh`                              |                                   |
| Auth - Sign Out                 | POST   | `/api/auth/sign-out`                             |                                   |

## **User Endpoints**

| Name             | Method | Endpoint              | Notes             |
|------------------|--------|-----------------------|-------------------|
| User - My Profile| GET    | `/api/user/me`        | Requires Bearer JWT |

## **Role Endpoints** (ADMIN only)

| Name                  | Method | Endpoint                   | Notes                        |
|-----------------------|--------|----------------------------|------------------------------|
| Roles - Create Role   | POST   | `/api/roles`               | `{ "name": "..." }` in body  |
| Roles - Get All       | GET    | `/api/roles`               |                              |
| Roles - Get By ID     | GET    | `/api/roles/{role_id}`     |                              |
| Roles - Update        | PUT    | `/api/roles/{role_id}`     | `{ "name": "..." }` in body  |
| Roles - Delete        | DELETE | `/api/roles/{role_id}`     |                              |

---

## **Usage Example (with Postman)**

1. **Sign Up**  
   `POST /api/auth/sign-up`  
   ```json
   {
     "username": "testuser1",
     "password": "test123456",
     "email": "testuser1@gmail.com"
   }
   ```

2. **Request Verification Email**  
   `POST /api/auth/request-verification-email?email=testuser1@gmail.com`

3. **Verify Email**  
   `POST /api/auth/verify-email`  
   ```json
   {
     "email": "testuser1@gmail.com",
     "otp": "316111"
   }
   ```

4. **Sign In**  
   `POST /api/auth/sign-in`  
   ```json
   {
     "username": "testuser1",
     "password": "test123456"
   }
   ```

5. **Get Profile (Authenticated)**  
   `GET /api/user/me`  
   - Add `Authorization: Bearer <jwt_token>` header

6. **Role Management (ADMIN only, Bearer JWT required)**  
   - Create Role: `POST /api/roles`  
   - Get All Roles: `GET /api/roles`  
   - Get Role by ID: `GET /api/roles/{role_id}`  
   - Update Role: `PUT /api/roles/{role_id}`  
   - Delete Role: `DELETE /api/roles/{role_id}`  

---

**Tip:**  
You can find a ready-to-import Postman collection here:  
[Authentication & Role API Postman Collection](https://github.com/Chris-Basson-Grundling/spring-boot-security-scylladb/blob/8a600e6a34804160c755f1fead08552501f8c166/postman/Authentication_postman_collection.json)
