# Booking API

Simple RESTful API for managing booking schedules. Built with Java Spring Boot.

## Features

- Create, view, and delete bookings
- Validates time conflict (no overlapping bookings)
- PostgreSQL database integration
- Swagger API documentation
- Clean architecture (Controller – Service – Repository)

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Lombok
- Springdoc OpenAPI (Swagger)
- Docker (optional)

## API Endpoints


| Method | Endpoint             | Description         |
|--------|----------------------|---------------------|
| GET    | `/api/bookings`      | Get all bookings    |
| GET    | `/api/bookings/{id}` | Get booking by ID   |
| POST   | `/api/bookings`      | Create new booking  |
| DELETE | `/api/bookings/{id}` | Delete a booking    |

## Booking Conflict Rule

The API checks for any existing bookings that overlap with the requested start and end time. If a conflict exists, it throws an error.

## Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/booking-api.git
cd booking-api

```
### 2. DATABASE Config
```
spring:
datasource:
url: jdbc:postgresql://localhost:5432/bookingdb
username: your_username
password: your_password
```

### Security
```
Ganti SECRET_KEY di JwtService dengan string acak minimal 32 karakter

echo $(openssl rand -base64 32)
```

### 3. Run
```
./mvnw spring-boot:run

```

### 4. SWAGGER
```
http://localhost:8080/swagger-ui.html
```


Sample 
```
POST /api/auth/register
Content-Type: application/json

{
"username": "budi",
"password": "123"
}


POST /api/auth/login
Content-Type: application/json

{
"username": "budi",
"password": "123"
}

Authorization: Bearer <your-token>

```


