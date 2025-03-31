# CRUD Application for Product Management

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Database Configuration](#database-configuration)
- [Testing](#testing)

---

## Overview

This is a RESTful CRUD (Create, Read, Update, Delete) application built using **Spring Boot** and **JPA/Hibernate**. It allows users to manage product entities via API endpoints. The application includes input validation, global exception handling, and support for pagination.

---

## Features

- **Create**: Add new products with validation.
- **Read**: Retrieve all products or fetch a single product by ID.
- **Update**: Modify existing product details.
- **Delete**: Remove products by ID.
- **Validation**: Ensure data integrity with field-level validation.
- **Global Exception Handling**: Handle errors gracefully with custom error messages.
- **Swagger UI**: Integrated Swagger/OpenAPI for API documentation.
> Swagger is available after launching the application at the following url - http://localhost:8080/swagger-ui/index.html

---

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17+**
- **Postgres**
- **Maven**
- **IDE** (IntelliJ IDEA)

---

## Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Adsimka/crud-for-alex.git
   
---

## Usage

The application provides RESTful API endpoints for managing products. 


---

### **[API Endpoints](#api-endpoints)**

Этот раздел содержит список доступных API-эндпоинтов.

## API Endpoints

| Method   | Endpoint               | Description                 |
|----------|------------------------|-----------------------------|
| `POST`   | `/api/products`        | Create a new product        |
| `GET`    | `/api/products/{id}`   | Get a product by ID         |
| `GET`    | `/api/products`        | Search product by filter    |
| `PATCH`  | `/api/products/{id}`   | Update an existing product  |
| `DELETE` | `/api/products/{id}`   | Delete a product by ID      |
| `DELETE` | `/api/products`        | Delete a product by filter  |

> Note: The `{id}` parameter in the URL represents the unique identifier of the product.

---

## Database Configuration
> The application uses PostgreSQL as its database. To set up the environment, run the docker-compose.yml file included in the project to start the PostgreSQL container. Make sure to configure the database credentials (DB_USERNAME and DB_PASSWORD) in your environment variables or .env file.