# API Reference

This document describes the REST API exposed by the Media Inventory App Server.

All endpoints return JSON. Authenticated endpoints require a valid JWT access token in the `Authorization` header:

```
Authorization: Bearer <access_token>
```

---

## Authentication

### POST `/auth/signup`

Register a new user account.

**Request Body**

```json
{
  "username": "jdoe",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response** — `200 OK`

```json
{
  "id": 1,
  "username": "jdoe",
  "firstName": "John",
  "lastName": "Doe"
}
```

---

### POST `/auth/login`

Authenticate a user and receive JWT tokens.

**Request Body**

```json
{
  "username": "jdoe",
  "password": "secret"
}
```

**Response** — `200 OK`

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "d4f8a2b1-..."
}
```

| Token | Expiry |
|---|---|
| Access token | 1 minute |
| Refresh token | 10 minutes |

---

### POST `/auth/refreshToken`

Exchange a valid refresh token for a new access token and rotated refresh token.

**Request Body**

```json
{
  "token": "d4f8a2b1-..."
}
```

**Response** — `200 OK`

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "e5a9b3c2-..."
}
```

---

### POST `/auth/logout`

Invalidate the refresh token to log out.

**Request Body**

```json
{
  "refreshToken": "d4f8a2b1-..."
}
```

**Response** — `200 OK`

```
"Logged out successfully"
```

---

## Users

### GET `/users`

Retrieve all registered users.

**Auth:** Required

**Response** — `200 OK`

```json
[
  {
    "id": 1,
    "username": "jdoe",
    "firstName": "John",
    "lastName": "Doe"
  }
]
```

---

### GET `/users/profile`

Retrieve the authenticated user's profile.

**Auth:** Required

**Response** — `200 OK`

```json
{
  "id": 1,
  "username": "jdoe",
  "firstName": "John",
  "lastName": "Doe"
}
```

---

### GET `/users/test`

Test endpoint restricted to administrators.

**Auth:** Required (ADMIN role)

**Response** — `200 OK`

```
"Welcome!"
```

---

## Media

### GET `/media`

Search for media items. Results are filtered by the authenticated user's ownership.

**Auth:** Required

**Request Body**

```json
{
  "entityType": "BOOK",
  "title": "The Great Gatsby",
  "genre": "Fiction",
  "format": "Hardcover",
  "username": "jdoe",
  "additionalAttributes": {
    "AUTHORS": ["F. Scott Fitzgerald"],
    "COPYRIGHT_YEAR": 1925
  }
}
```

All fields are optional. When provided, they act as filters.

**Supported `entityType` values:** `BOOK`, `MOVIE`, `TELEVISION_SHOW`, `MUSIC`, `VIDEO_GAME`

**Additional attributes by media type:**

| Media Type | Attribute Keys |
|---|---|
| BOOK | `AUTHORS`, `COPYRIGHT_YEAR`, `EDITION` |
| MOVIE | `DIRECTORS`, `RELEASE_YEAR` |
| TELEVISION_SHOW | `EPISODES`, `SEASON`, `RELEASE_YEAR` |
| MUSIC | `ARTISTS`, `SONG_LIST`, `RELEASE_YEAR` |
| VIDEO_GAME | `CONSOLES`, `NUMBER_OF_PLAYERS`, `RELEASE_YEAR` |

**Response** — `200 OK`

```json
[
  {
    "id": 1,
    "title": "The Great Gatsby",
    "genre": "Fiction",
    "format": "Hardcover",
    "entityType": "BOOK",
    "completed": true,
    "onLoan": false,
    "reviewRating": 5,
    "reviewDescription": "A classic.",
    "tags": ["classic", "fiction"],
    "additionalAttributes": {
      "AUTHORS": ["F. Scott Fitzgerald"],
      "COPYRIGHT_YEAR": 1925,
      "EDITION": 1
    }
  }
]
```

---

### POST `/media`

Create a new media item.

**Auth:** Required

**Request Body**

```json
{
  "entityType": "MOVIE",
  "title": "Inception",
  "genre": "Science Fiction",
  "format": "Blu-ray",
  "tags": ["sci-fi", "thriller"],
  "reviewRating": 5,
  "reviewDescription": "Mind-bending masterpiece.",
  "additionalAttributes": {
    "DIRECTORS": ["Christopher Nolan"],
    "RELEASE_YEAR": 2010
  }
}
```

**Response** — `201 Created`

```json
{
  "id": 2,
  "title": "Inception",
  "genre": "Science Fiction",
  "format": "Blu-ray",
  "entityType": "MOVIE",
  "completed": false,
  "onLoan": false,
  "reviewRating": 5,
  "reviewDescription": "Mind-bending masterpiece.",
  "tags": ["sci-fi", "thriller"],
  "additionalAttributes": {
    "DIRECTORS": ["Christopher Nolan"],
    "RELEASE_YEAR": 2010
  }
}
```

---

### PUT `/media/{id}`

Update an existing media item.

**Auth:** Required

**Path Parameters**

| Parameter | Type | Description |
|---|---|---|
| `id` | Long | The media item ID |

**Request Body** — Same schema as POST `/media`.

**Response** — `200 OK`

Returns the updated `MediaResponse`.

---

### DELETE `/media/{id}`

Delete a media item.

**Auth:** Required

**Path Parameters**

| Parameter | Type | Description |
|---|---|---|
| `id` | Long | The media item ID |

**Response** — `200 OK`

Returns the deleted item's ID as a `Long`.

---

## Error Responses

When an error occurs, the API returns a response with the following structure:

```json
{
  "errorResponse": {
    "message": "Description of the error.",
    "statusCode": 500
  }
}
```

### Common Error Scenarios

| Status Code | Cause |
|---|---|
| 400 | Validation failure or no changes to update |
| 401 | Missing or invalid JWT token |
| 403 | Insufficient permissions |
| 404 | Resource not found |
| 409 | Duplicate resource (e.g., media with same title for user) |
| 500 | Internal server error |
