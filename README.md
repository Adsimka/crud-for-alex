# CRUD Application for Product Management

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
  - [Batch product creation](#batch-product-creation)
- [API Endpoints](#api-endpoints)
- [Database Configuration](#database-configuration)
- [Testing](#testing)

---

## Overview

This is a RESTful CRUD (Create, Read, Update, Delete) application built using **Spring Boot** and **JPA/Hibernate**. It allows users to manage product entities via API endpoints. The application includes input validation, global exception handling, and support for pagination.

---

## Features

- **Create** a product or create up to 10,000 products with their variants in one request.
- **Read** products by filter or ID and check whether a product exists.
- **Update** existing product details.
- **Delete** products by filter or ID.
- **Validation and error handling** use field-level constraints and `ProblemDetail` responses.
- **Swagger UI** provides interactive API documentation.

---

## Prerequisites

- **JDK 17**
- **PostgreSQL**, or **Docker with Compose** to run PostgreSQL locally
- **Docker** for the Testcontainers integration tests

The repository includes the Maven Wrapper for Linux, macOS, and Windows.

---

## Installation

```bash
git clone https://github.com/Adsimka/crud-for-alex.git
cd crud-for-alex
```

---

## Usage

Configure a PostgreSQL instance before starting the application. See [Database Configuration](#database-configuration).

```bash
./mvnw spring-boot:run
```

On Windows, use `.\mvnw.cmd spring-boot:run`. Open the API documentation after startup. [Swagger UI](http://localhost:8080/swagger-ui.html)

### Batch product creation

`POST /api/v1/products/batch` accepts a JSON object with a required `products` array containing 0 to 10,000 items. Each item contains a `product` with the same fields and validation as single-product creation. Its `variants` array can be omitted, set to `null`, or left empty.

Each variant requires a nonblank `code` of up to 20 characters and a nonblank `name` of up to 50 characters.

```bash
curl -i -X POST http://localhost:8080/api/v1/products/batch \
  -H 'Content-Type: application/json' \
  --data '{
    "products": [
      {
        "product": {"name": "Basic T-shirt"},
        "variants": [
          {"code": "white-s", "name": "White, S"},
          {"code": "white-m", "name": "White, M"}
        ]
      }
    ]
  }'
```

The example returns `201 Created` with the following counts.

```json
{"createdProducts": 1, "createdVariants": 2}
```

| Status | Result |
|--------|--------|
| `200 OK` | An empty `products` array returns zero for both counts. |
| `201 Created` | Returns the number of products and variants created. |
| `400 Bad Request` | Returns `ProblemDetail` for invalid JSON or request values. |
| `500 Internal Server Error` | Returns `ProblemDetail` for a database or transaction error. |

Products and variants are saved in one transaction. A write failure rolls back the entire request. The JDBC batch size defaults to 500 rows and can be set with `operation.batch.sql.update`.

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/products` | Create a product |
| `POST` | `/api/v1/products/batch` | Create products and their variants |
| `GET` | `/api/v1/products/{id}` | Get a product by ID |
| `GET` | `/api/v1/products` | Search products by filter |
| `GET` | `/api/v1/products/exists/{id}` | Check whether a product exists |
| `PATCH` | `/api/v1/products/{id}` | Update an existing product |
| `DELETE` | `/api/v1/products/{id}` | Delete a product by ID |
| `DELETE` | `/api/v1/products` | Delete products by filter |

---

## Database Configuration

To run PostgreSQL with Docker Compose using the application's local defaults, create a `.env` file in the repository root with these example values.

```dotenv
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_PORT_EXTERNAL=5435
POSTGRES_PORT_INTERNAL=5432
```

```bash
docker compose up -d service-db
```

The application defaults to `localhost:5435/postgres`. To use a different database, set `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD` in the application's environment. Liquibase applies the schema on startup.

---

## Testing

Start Docker and run the integration tests with the Maven Wrapper.

```bash
./mvnw test
```

On Windows, use `.\mvnw.cmd test`. The tests activate the `test` profile and use PostgreSQL 15 through Testcontainers. They cover product updates, empty batches, insertion of 10,000 products with their variants, and rejection of oversized batches or `null` variant entries.
