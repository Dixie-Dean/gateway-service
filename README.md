# Gateway Service

The Gateway Service is a microservice that provides a RESTful API for managing imager posts, allowing users to upload, retrieve, edit, and delete posts.

![Build Status](https://img.shields.io/badge/build-passing-brightgreen) ![Coverage](https://img.shields.io/badge/coverage-85%25-brightgreen) ![Version](https://img.shields.io/badge/version-1.0.0-blue)

## Installation

1. Clone the repository:
```
git clone https://github.com/your-username/gateway-service.git
```

2. Navigate to the project directory:
```
cd gateway-service
```

3. Build the project using Maven:
```
./mvnw clean install
```

4. Start the application:
```
./mvnw spring-boot:run
```

The application will start running on `http://localhost:8082`.

## Usage/Examples

The Gateway Service provides the following API endpoints:

### Upload Imager Post

```http
POST /gateway/upload
```
Uploads a new imager post.

### Get Imager Post

```http
GET /gateway/post?id={id}
```
Retrieves an imager post by its ID.

### Get Imager Posts by Email

```http
GET /gateway/posts?email={email}
```
Retrieves all imager posts for a given email.

### Edit Imager Post

```http
PATCH /gateway/edit
```
Edits an existing imager post.

### Delete Imager Post

```http
DELETE /gateway/delete?id={id}
```
Deletes an imager post by its ID.

## API Reference

The Gateway Service exposes the following API endpoints:

| Endpoint | HTTP Method | Description |
| --- | --- | --- |
| `/gateway/upload` | `POST` | Uploads a new imager post |
| `/gateway/post` | `GET` | Retrieves an imager post by its ID |
| `/gateway/posts` | `GET` | Retrieves all imager posts for a given email |
| `/gateway/edit` | `PATCH` | Edits an existing imager post |
| `/gateway/delete` | `DELETE` | Deletes an imager post by its ID |

## Running Tests

The Gateway Service includes the following test cases:

- `GatewayServiceApplicationTests`: Verifies the context loads successfully.
- `AuthenticationTest`: Verifies the authentication flow.
- `GatewayServiceTest`: Verifies the functionality of the `GatewayService` class.
- `SecurityServiceTest`: Verifies the functionality of the `SecurityService` class.
- `TokenServiceTest`: Verifies the functionality of the `TokenService` class.

To run the tests, execute the following command:

```
./mvnw test
```