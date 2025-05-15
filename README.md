# Voucher Management and Redemption Backend API

A Java Spring Boot microservice for managing and redeeming vouchers with flexible redemption rules. This backend module enables operators to create vouchers with specific conditions and allows clients to redeem them through a secure interface. The project uses PostgreSQL for persistence and is containerized using Docker and Docker Compose.

---

## Overview

-   **Voucher API**: REST endpoints for creating, validating, and redeeming vouchers.
-   **Redemption Types Supported**:
    -   Single Redemption
    -   Multiple Redemption
    -   X Times Redemption
    -   Time-limited Redemption (only before a specific expiration date)
-   **Interfaces**:
    -   **Management Interface**: For operators to configure vouchers with various features
    -   **Redemption Interface**: For clients to redeem vouchers securely
-   **Security**: JWT-based authentication with role-based access control for admin actions
-   **Features**:
    -   Dockerized Spring Boot app
    -   PostgreSQL integration
    -   Role-based access with Spring Security
    -   Input validation via Spring Validation

---

## Tech Stack

| Component             | Technology                     |
| --------------------- | ------------------------------ |
| **Framework**         | Java 17 + Spring Boot 3.4.5    |
| **Database**          | PostgreSQL                     |
| **ORM**               | Spring Data JPA                |
| **Authentication**    | Spring Security + JWT          |
| **Validation**        | Spring Boot Starter Validation |
| **Build Tool**        | Maven                          |
| **Containerization**  | Docker + Docker Compose        |
| **API Documentation** | OpenAPI/Swagger (optional)     |
| **Testing**           | Spring Boot Test Framework     |

---

## Prerequisites

-   Java 17+
-   Docker & Docker Compose
-   PostgreSQL (if running outside of Docker)
-   Git

---

## Setup

### Build the Application

---

### Running Locally Without Docker

### Start PostgreSQL

Make sure you have a local PostgreSQL instance running. You can use tools like Docker Desktop or install it natively.

Update your `application.yml` or `application.properties` with your DB credentials, for example:

```yaml
spring:
    datasource:
        url: jdbc:postgresql://localhost:5432/voucherdb
        username: your_user
        password: your_password
```

### Run the Application

Use Maven Wrapper or your IDE:

```bash
./mvnw spring-boot:run
```

Or run the compiled JAR (after building):

```bash
./mvnw clean package
java -jar target/*.jar
```

Make sure the database is up before running the app.

---

### Running with Docker

## Managing Services

### Start the Full Stack

```bash
docker-compose up --build
```

### Stop the Stack

```bash
docker-compose down
```

### View Logs

```bash
docker-compose logs -f app
```

---
