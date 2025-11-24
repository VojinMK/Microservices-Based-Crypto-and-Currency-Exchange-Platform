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
  **Access:** ADMIN, OWNER 

**Example request body (for creating any user):**

```json
{
  "email": "user@gmail.com",
  "password": "123456789",
  "role": "user"
}
```
#### Update users 

- `PUT: http://localhost:8765/users/updateUser`
  Admin can update only users with the USER role, while the Owner can update any user.
  
**Example request body (for updating any user):**

```json
{
  "email": "user@gmail.com",
  "password": "123456789",
  "role": "user"
}
```
#### Delete users 

- `DELETE: http://localhost:8765/users/removeUser?email=user1@gmail.com`
  Only the Owner has permission to delete a user account.
  
---

## Currency-Exchange

- `GET: http://localhost:8765/currency-exchange?from=EUR&to=CHF`
  Returns the current exchange rate between two fiat currencies.  
  Accessible to all roles.

---

## Currency-Conversion

- `GET: http://localhost:8765/currency-conversion?from=EUR&to=RSD&quantity=10`
  Converts the specified amount from one fiat currency to another.  
  Accessible only to users with the USER role.

---
## Bank-Account

- `GET: http://localhost:8765/bank-accounts`
  Returns a list of all bank accounts. Accessible only to admin users.
  
- `GET: http://localhost:8765/bank-accounts/email`
  Returns the bank account of the currently authenticated user. Only the logged-in user can access their own account.

### Create Bank-Account

- `POST: http://localhost:8765/bank-accounts/create`
  Creates a new bank account for the specified user. Accessible only to admin users.
  
**example request body:**
```json
{
  "email": "user@gmail.com",
  "rsdAmount": 0.00,
  "eurAmount": 0.00,
  "usdAmount": 0.00,
  "chfAmount": 0.00,
  "gbpAmount": 0.00
}
```
### Update Bank-Account

- `PUT: http://localhost:8765/bank-accounts/update`
  Updates an existing bank account (balances and amounts). Accessible only to admin users.
  
**example request body:**
```json
{
  "email": "user@gmail.com",
  "rsdAmount": 10.00,
  "eurAmount": 100.00,
  "usdAmount": 0.00,
  "chfAmount": 100.00,
  "gbpAmount": 0.00
}
```

### Delete Bank-Account

- `DELETE: http://localhost:8200/bank-accounts/remove?email=user@gmail.com`
  Internal/debug endpoint available only directly via the service port. The specification does not assign any role the permission to delete accounts through the gateway.

---

## Crypto-exchange

- `GET: http://localhost:8765/crypto-exchange?from=BTC&to=ETH`
  Returns the exchange rate between two cryptocurrencies. Accessible to all roles.

---

## Crypto-conversion

- `GET: http://localhost:8765/crypto-conversion?from=BTC&to=LTC&quantity=10`
  Converts a specified amount from one cryptocurrency to another. Accessible only to user role.

---

## Crypto-wallet

- `GET: http://localhost:8765/crypto-wallets`
  Returns all crypto wallets in the system. Accessible only to admin users.
  
- `GET: http://localhost:8765/crypto-wallets/email`
  Returns the crypto wallet of the currently authenticated user. Only the logged-in user can access their own wallet.

## Create Crypto-wallet

- `POST: http://localhost:8765/crypto-wallets/create`
  Creates a new crypto wallet for the specified user. Accessible only to admin users.
   
**example request body:**
```json
{
  "email": "user@gmail.com",
  "btcAmount": 0.00,
  "ethAmount": 0.00,
  "ltcAmount": 0.00
}
```
## Update Crypto-wallet

- `PUT: http://localhost:8765/crypto-wallets/update`
  Updates an existing crypto wallet (balances of cryptocurrencies). Accessible only to admin users.
  
**example request body:**
```json
{
  "email": "user@gmail.com",
  "btcAmount": 11.00,
  "ethAmount": 1.00,
  "ltcAmount": 1.00
}
```
## Delete Crypto-wallet

- `DELETE: http://localhost:8300/crypto-wallets/remove?email=user1@gmail.com`
  Internal/debug endpoint available only via the service port. The specification does not define any role with permission to delete wallets through the gateway.

---

## Trade-service

- `GET: http://localhost:8765/trade-service?from=EUR&to=BTC&quantity=1`
  Executes a complete trade operation, converting a fiat currency to a cryptocurrency. Accessible only to users with the user role.
