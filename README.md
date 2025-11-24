# Microservices-Based Crypto & Currency Exchange Platform

This project is a microservices-based system for managing users, bank accounts, crypto wallets, currency exchange, crypto exchange and a unified trade flow, all routed through an API Gateway.  
The application demonstrates role-based access control (**OWNER**, **ADMIN**, **USER**) and separation of responsibilities between services.

The goal of the project is to show how a distributed system can be designed using Spring Boot microservices, with centralized authentication and authorization, and clear boundaries between business domains (users, bank accounts, crypto wallets, currency/crypto exchange, etc.).

## Technologies Used

- **Backend & Microservices**
  - Java, Spring Boot  
  - Spring Cloud Gateway  
  - Spring Security (role-based access control)  
  - OpenFeign (inter-service communication)  
  - Eureka (service discovery)  
  - Resilience4j (resilience patterns)  
  - H2 Database (in-memory, for development)

- **Infrastructure & Tools**
  - Docker (containerization)  
  - Docker Compose (orchestration)  
  - Maven 

---

## Authentication & Test Users

The system uses role-based access control. The following test users are available:

**OWNER**  
- Email: `owner@gmail.com`  
- Password: `123456789`  

**ADMIN**  
- Email: `admin@gmail.com`  
- Password: `123456789`  

**USER**  
- Email: `user@gmail.com`  
- Password: `123456789`  

Use these credentials when calling endpoints through the API Gateway.

---

## Base URL (API Gateway)

All main requests should go through the API Gateway:

- **Base URL:** `http://localhost:8765`

Some internal/debug endpoints are exposed directly on service ports (e.g. `8770`, `8200`, `8300`) and are not meant for regular users, but for testing and verification.

---

## API Overview

Below is an overview of the main endpoints and which roles can access them.

---

### Users Service

Base (gateway) URL: `http://localhost:8765/users`

#### Internal / Debug (direct service access)

- `GET: http://localhost:8770/users`  
  Returns a list of all users.  
  > Internal/debug endpoint, not accessible through the gateway for regular roles. Used only for manual checks and debugging.

- `GET: http://localhost:8770/users/email?email=user@gmail.com`  
  Returns a single user by email.  
  > Internal/debug endpoint, used only for verification and testing.

#### Create users (via API Gateway)

- `POST: http://localhost:8765/users/newAdmin`  
  Creates a new user with the **ADMIN** role.  
  **Access:** OWNER

- `POST: http://localhost:8765/users/newOwner`  
  Creates a new user with the **OWNER** role.  
  **Access:** OWNER

- `POST: http://localhost:8765/users/newUser`  
  Creates a new user with the **USER** role.  
  **Access:** ADMIN, OWNER (depending on how you configured it; typically at least ADMIN)

**Example request body (for creating any user):**

```json
{
  "email": "user@gmail.com",
  "password": "123456789",
  "role": "user"
}
