# Bookstore Application

Project Overview

Bookstore is a web-based application that allows users to browse available books, manage their shopping cart, and place orders online.

The application is developed using React for the frontend and Java Spring Boot for the backend.

**Project:** Online Bookstore

## Features

- Register and log in users
- Validate registration and login request payloads
- Return the available bookstore catalog
- Hash passwords with BCrypt
- Use an H2 in-memory database for local development
- Include CORS support for a frontend running at `http://localhost:5173`
- Protect application endpoints with Spring Security HTTP Basic authentication

## Tech Stack

- Java 17
- Spring Boot 4.2.0
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Validation
- H2 Database
- Maven
- JUnit 5

## Prerequisites

- Java 17 or later
- Maven 3.6 or later

You can use the included Maven Wrapper, so a separate Maven installation is not required.

## Getting Started

Clone the repository and run the application from the project root:

```bash
git clone https://github.com/sharmashipra121/bookstore.git
cd bookstore
./mvnw spring-boot:run
```

On Windows, use:

```powershell
.\mvnw.cmd spring-boot:run
```

The API starts on `http://localhost:8080` by default.

## API Endpoints

### Register

```http
POST /auth/register
Content-Type: application/json

{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "*********"
}
```

Example response:

```json
{
  "message": "Registration Successful",
  "email": "jane@example.com"
}
```

### Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "jane@example.com",
  "password": "********"
}
```

Example response:

```json
{
  "message": "Login Successful",
  "email": "jane@example.com"
}
```

### List Books

```http
GET /books
Authorization: Basic <base64-encoded-email-and-password>
```

Example response:

```json
[
  {
    "id": 1,
    "title": "Effective Java",
    "author": "Joshua Bloch",
    "price": 650,
    "stock": 10
  }
]
```

The `/auth/**`, `/h2-console/**`, `/error`, and `/` endpoints are publicly accessible. Other endpoints require HTTP Basic authentication.

## H2 Database

The application uses an in-memory H2 database named `bookstoredb`. Data is reset whenever the application restarts.

Open the H2 console at:

```text
http://localhost:8080/h2-console
```

Use these connection settings:

| Setting | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:bookstoredb` |
| Username | `sa` |
| Password | _empty_ |

Initial sample books are loaded from `src/main/resources/data.sql`.

## Running Tests

Run the complete test suite with the Maven Wrapper:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

## Project Structure

```text
src/
├── main/
│   ├── java/com/kata/bookstore/
│   │   ├── config/       # Security and CORS configuration
│   │   ├── controller/   # REST controllers
│   │   ├── dto/          # Request and response models
│   │   ├── entity/       # JPA entities
│   │   ├── repository/   # Spring Data repositories
│   │   └── service/      # Business logic
│   └── resources/
│       ├── application.properties
│       └── data.sql
└── test/
    └── java/com/kata/bookstore/
```
