# Architecture

This document describes the architecture and design patterns used in the Media Inventory App Server.

---

## Layered Architecture

The application follows a standard layered architecture:

```
┌──────────────────────────────────────┐
│            Controllers               │  ← REST endpoints
├──────────────────────────────────────┤
│             Services                 │  ← Business logic
├──────────────────────────────────────┤
│     Repositories / DAOs              │  ← Data access
├──────────────────────────────────────┤
│        MySQL Database                │  ← Persistence
└──────────────────────────────────────┘
```

**Controllers** receive HTTP requests, delegate to services, and return HTTP responses.
**Services** contain business rules such as validation, duplicate detection, and change tracking.
**Repositories** interact with the database through JPA. A generic DAO pattern (`IBaseDao` / `BaseDaoImpl`) provides reusable CRUD operations backed by the JPA `EntityManager`.

---

## Entity Model

### Media Inheritance Hierarchy

Media types share a common base and use JPA joined-table inheritance:

```
            Media (abstract)
          /    |     |     \       \
       Book  Movie  Music  TV Show  Video Game
```

Each media type extends `Media` and adds type-specific fields (e.g., `authors` for Book, `directors` for Movie). The base `Media` entity includes shared fields like `title`, `genre`, `format`, review data, and audit timestamps.

**Inheritance strategy:** `InheritanceType.JOINED` — each subclass has its own table joined to the `media` table by primary key.

### Relationships

- **Media ↔ Collection** — Many-to-many through a `collection_media` junction table. A media item can belong to multiple collections.
- **Media ↔ Tag** — Many-to-many through a `media_tags` junction table. Tags are reusable across media items.
- **UserInfo ↔ RefreshToken** — One-to-one. Each user has at most one active refresh token.

### Uniqueness Constraints

- Media: composite unique constraint on (`title`, `created_by`) — a user cannot have duplicate titles.
- Collection: composite unique constraint on (`collection_title`, `created_by`).
- Tag: natural ID on the `tag` field.

---

## Design Patterns

### Factory Pattern

Two factories decouple controllers from concrete service/mapper implementations:

- **`ServiceFactory`** — Given a media entity type (e.g., `BOOK`), returns the corresponding `BaseService` implementation (e.g., `BookService`).
- **`MapperFactory`** — Given a media entity type, returns the corresponding MapStruct mapper.

This allows the `MediaController` to handle all media types through a single set of endpoints.

### Generic DAO Pattern

`IBaseDao<T>` defines a type-safe interface for common data operations (find, create, update, delete). `BaseDaoImpl<T>` provides a JPA `EntityManager`-based implementation. Media-specific services set the entity class at runtime so the same DAO logic works across all media types.

### DTO / Mapper Pattern

Request and response objects are separate from JPA entities:

- **Request DTOs** (`UpdateCreateMediaRequest`, `SearchMediaRequest`, etc.) — Carry input data from the client.
- **Response DTOs** (`MediaResponse`, `UserResponse`, etc.) — Shape the data returned to the client.
- **MapStruct Mappers** — Generate type-safe mapping code at compile time to convert between entities and DTOs.

### Audit Fields

All media and collection entities use JPA lifecycle callbacks (`@PrePersist`, `@PreUpdate`) to automatically populate audit fields (`createdBy`, `createdOn`, `modifiedBy`, `modifiedOn`) from the Spring Security context.

---

## Security Architecture

### Authentication Flow

```
Client                    Server
  │                         │
  ├── POST /auth/login ────►│  Authenticate credentials
  │◄── JWT + RefreshToken ──│  Return tokens
  │                         │
  ├── GET /media ──────────►│  Validate JWT in Authorization header
  │   (Bearer token)        │  JwtAuthFilter extracts and validates token
  │◄── 200 OK ─────────────│
  │                         │
  ├── POST /auth/refresh ──►│  Validate refresh token
  │◄── New JWT + Refresh ───│  Rotate both tokens
```

### Token Strategy

| Token | Type | Lifetime | Storage |
|---|---|---|---|
| Access token | JWT (HS256) | 1 minute | Client-side |
| Refresh token | UUID | 10 minutes | Database (`refresh_tokens` table) |

- Access tokens are short-lived and stateless.
- Refresh tokens are stored in the database and rotated on each use (old token deleted, new token created).
- Logout deletes the refresh token from the database.

### Authorization

Spring Security's `@PreAuthorize` annotations and `SecurityFilterChain` rules enforce access:

- `/auth/**` endpoints are publicly accessible.
- `/users/test` requires `ROLE_ADMIN`.
- All other endpoints require authentication.
- CSRF protection is disabled for API endpoints (stateless JWT architecture).

---

## Configuration

### Spring Security Configuration

`SecurityConfig` defines the `SecurityFilterChain` bean:

1. CSRF disabled for `/auth/**`
2. Session management set to `STATELESS`
3. `JwtAuthFilter` added before `UsernamePasswordAuthenticationFilter`
4. Custom `AuthenticationEntryPoint` and `AccessDeniedHandler` for error responses

### Application Configuration

`AppConfig` provides shared Spring beans. `MediaInventoryAdditionalAttributes` is an enum that defines the valid attribute keys for each media type's additional attributes map (e.g., `AUTHORS`, `DIRECTORS`, `RELEASE_YEAR`).

---

## Data Flow Example: Creating a Book

1. Client sends `POST /media` with `entityType: "BOOK"` and book-specific attributes.
2. `MediaController` receives the `UpdateCreateMediaRequest`.
3. `ServiceFactory` resolves `BookService` based on the entity type.
4. `BookService.create()`:
   - Uses `MapperFactory` to get the book mapper.
   - Maps the request DTO to a `Book` entity.
   - Checks for duplicates (same title + user).
   - Persists via `BaseDaoImpl.createOrUpdate()`.
   - Maps the saved entity back to a `MediaResponse`.
5. `MediaController` returns `201 Created` with the `MediaResponse`.

---

## Error Handling

A global `MediaServerExceptionHandler` catches exceptions and returns structured error responses:

| Exception | HTTP Status |
|---|---|
| `ResourceNotFoundException` | 404 Not Found |
| `ResourceAlreadyExistsException` | 409 Conflict |
| `NoChangesToUpdateException` | 400 Bad Request |
| `DuplicateUserException` | 409 Conflict |

Each error response includes a message and status code in a consistent `ErrorResponse` format.
