# Media Inventory App Server

A RESTful API server for managing a personal media inventory. Built with Spring Boot and Java 17, this application allows users to catalog and organize their collections of books, movies, television shows, music, and video games.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Reference](#api-reference)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Documentation](#documentation)

## Features

- **Multi-media support** — Manage five media types: Books, Movies, TV Shows, Music, and Video Games
- **User authentication** — JWT-based authentication with access and refresh token rotation
- **Role-based access control** — ADMIN, USER, and VIEWER roles
- **Collection management** — Organize media items into named collections
- **Tagging** — Categorize media with reusable tags
- **Search** — Filter media by title, genre, format, and type-specific attributes
- **Tracking** — Track completion status, loan status, and personal reviews/ratings

## Technology Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.1 |
| Security | Spring Security 6.1.7, JWT (jjwt 0.11.2) |
| Database | MySQL 8 |
| ORM | Hibernate 6.1.6.Final, Spring Data JPA |
| Mapping | MapStruct 1.5.5.Final, ModelMapper 3.1.1 |
| Build | Apache Maven (with Maven Wrapper) |
| Testing | JUnit 5, Mockito 3.12.4, AssertJ 3.24.2 |
| Utilities | Lombok 1.18.30, Apache Commons Lang3 3.12.0 |

## Architecture

The application follows a layered architecture:

```
Controller  →  Service  →  Repository (DAO)  →  Database
     ↕              ↕
  DTOs/Mappers   Entities
```

- **Controllers** — REST endpoints that accept requests and return responses
- **Services** — Business logic, validation, and orchestration
- **Repositories** — Data access via a generic DAO pattern backed by JPA EntityManager
- **Entities** — JPA entities using joined-table inheritance for media types
- **Mappers** — MapStruct-based mappers for entity ↔ DTO conversion
- **Factories** — `ServiceFactory` and `MapperFactory` resolve the correct service/mapper by media type

For detailed architecture documentation, see [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Prerequisites

- **Java 17** or later
- **MySQL 8** running on `localhost:3306`
- **Maven 3.8+** (or use the included Maven Wrapper)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/jamiepompei/media-inventory-app-server.git
cd media-inventory-app-server
```

### 2. Set up the database

Create a MySQL database named `mediainventoryapp`:

```sql
CREATE DATABASE mediainventoryapp;
```

### 3. Configure environment variables

The application requires the following environment variables:

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL (default: `jdbc:mysql://localhost:3306/mediainventoryapp`) |
| `SPRING_DATASOURCE_USERNAME` | MySQL username |
| `SPRING_DATASOURCE_PASSWORD` | MySQL password |
| `SECRET_KEY` | Base64-encoded key for JWT signing (minimum 256 bits) |

> **Note:** The `application.properties` file contains hardcoded defaults for local development. For production, uncomment the environment variable placeholders and set the corresponding values.

### 4. Build and run

```bash
cd app-server

# Using the Maven Wrapper
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080` by default.

## Configuration

The main configuration file is located at `app-server/src/main/resources/application.properties`. Key settings include:

| Property | Description |
|---|---|
| `spring.datasource.url` | MySQL connection URL |
| `spring.jpa.hibernate.ddl-auto` | Schema management strategy (`update` by default) |
| `spring.jpa.database-platform` | Hibernate dialect (`MySQLDialect`) |
| `spring.jpa.show-sql` | Enable SQL logging (`true` by default) |
| `SECRET_KEY` | JWT signing key (loaded from environment) |

## API Reference

All endpoints are prefixed with the server base URL (default `http://localhost:8080`).

### Authentication (`/auth`)

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/auth/signup` | Register a new user | No |
| POST | `/auth/login` | Authenticate and receive JWT tokens | No |
| POST | `/auth/refreshToken` | Refresh an expired access token | No |
| POST | `/auth/logout` | Invalidate the refresh token | No |

### Users (`/users`)

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/users` | List all users | Yes |
| GET | `/users/profile` | Get the authenticated user's profile | Yes |
| GET | `/users/test` | Test endpoint | Yes (ADMIN) |

### Media (`/media`)

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/media` | Search media by type, title, genre, format, etc. | Yes |
| POST | `/media` | Create a new media item | Yes |
| PUT | `/media/{id}` | Update an existing media item | Yes |
| DELETE | `/media/{id}` | Delete a media item | Yes |

#### Supported Media Types

- `BOOK` — with authors, copyright year, edition
- `MOVIE` — with directors, release year
- `TELEVISION_SHOW` — with episodes, season, release year
- `MUSIC` — with artists, song list, release year
- `VIDEO_GAME` — with consoles, number of players, release year

For detailed API documentation including request/response schemas, see [docs/API.md](docs/API.md).

## Project Structure

```
media-inventory-app-server/
├── README.md
├── docs/                          # Documentation
│   ├── API.md                     # API reference
│   └── ARCHITECTURE.md            # Architecture overview
└── app-server/
    ├── pom.xml                    # Maven configuration
    ├── mvnw / mvnw.cmd            # Maven Wrapper scripts
    └── src/
        ├── main/java/com/inventory/app/server/
        │   ├── AppServerApplication.java   # Application entry point
        │   ├── config/                     # App and security configuration
        │   ├── controller/                 # REST controllers
        │   │   └── media/                  # Media & collection controllers
        │   ├── entity/                     # JPA entities and DTOs
        │   │   ├── collection/             # Collection & Tag entities
        │   │   ├── media/                  # Media type entities (Book, Movie, etc.)
        │   │   ├── payload/request/        # Request DTOs
        │   │   ├── payload/response/       # Response DTOs
        │   │   └── user/                   # User & RefreshToken entities
        │   ├── error/                      # Error handling & custom exceptions
        │   ├── factory/                    # Service and Mapper factories
        │   ├── mapper/                     # MapStruct mappers
        │   ├── repository/                 # Generic DAO & Spring Data repositories
        │   ├── security/                   # JWT filter & security config
        │   ├── service/                    # Business logic services
        │   └── util/                       # Utility classes
        └── test/java/com/inventory/app/server/
            ├── controller/                 # Controller unit tests
            ├── entity/                     # Entity unit tests
            └── service/                    # Service unit tests
```

## Testing

Run the test suite with Maven:

```bash
cd app-server

# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=BookServiceTest

# Run tests with verbose output
./mvnw test -Dsurefire.useFile=false
```

Tests are organized into three categories:

- **Controller tests** — Validate REST endpoint behavior using `@WebMvcTest` and `MockMvc`
- **Service tests** — Verify business logic using Mockito mocks for the DAO layer
- **Entity tests** — Test `equals()` and `hashCode()` implementations

## Documentation

Additional documentation is available in the [`docs/`](docs/) directory:

- **[API.md](docs/API.md)** — Detailed API reference with request/response schemas
- **[ARCHITECTURE.md](docs/ARCHITECTURE.md)** — System architecture and design patterns