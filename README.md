# 📚 Book-Repo
*Empowering Discovery, Simplifying Your Book Journey*

![Java](https://img.shields.io/badge/Java-17-blue?logo=java)  
![Maven](https://img.shields.io/badge/Maven-Build-orange?logo=apachemaven)  
![Docker](https://img.shields.io/badge/Docker-Containerization-blue?logo=docker)  
![Spring](https://img.shields.io/badge/Spring-Boot-brightgreen?logo=springboot)  

# 📚 Book Ordering Service

Spring Boot backend for managing book orders, users, carts, and payments. Built with a modular architecture and robust testing strategy.

---

## 🚀 Overview

This project provides a RESTful API for an online book ordering system. It supports user registration, authentication, book browsing, cart management, and order placement.

---

## 🧰 Technology Stack

| Technology       | Version       |
|------------------|---------------|
| Java             | 17            |
| Spring Boot      | 3.1.3         |
| Spring Security  | 6.1.2         |
| JPA/Hibernate    | 6.2.4.Final   |
| Liquibase        | 4.23.1        |
| MySQL            | 8.0           |
| Testcontainers   | 1.19.0        |
| JUnit            | 5             |
| Mockito          | 5             |
| Springdoc OpenAPI| 2.1.0         |

---

### ✨ Key Features  
- 🛠 **Dependency Management**: Maven + Spring Boot, Liquibase, MapStruct, JWT, Testcontainers  
- 🐳 **Containerized Environment**: Docker Compose & multi-stage Docker builds  
- 🔒 **Security & Authentication**: JWT-based authentication with role-based access control  
- ⚙ **Modular Architecture**: Specifications & dynamic query building for scalable data access  
- 🧪 **Extensive Testing**: Unit, integration, and controller tests ensure reliability  
- 📦 **E-commerce Features**: shopping cart, order management, and user roles  

---

## 🗃 Domain Model  
- **Book**: stores information about title, author, genre, price, stock, etc.  
- **User**: application users with roles (ADMIN, CUSTOMER).  
- **Role**: defines permissions for users.  
- **Cart**: holds items selected by a user before checkout.  
- **Order**: finalized purchase with order items, status, and total price.  
- **OrderItem**: association between books and orders with quantity & price.

---

## 📚 API Documentation

## 📬 Postman Collection

You can find the Postman collection in the following directory:  
[`docs/postman/postman_collection.json`](docs/postman/postman_collection.json)

Import it into Postman to quickly test the API endpoints.

---
## 📊 Domain Model Diagram

![Domain Model](docs/model-diagram.png)

---

## 🚀 Getting Started  

### ✅ Prerequisites  
Make sure you have installed:  
- Java 17+  
- Maven  
- Docker  

### 📥 Installation  
Clone the repository and install dependencies:  

```bash
# Clone the repo
git clone https://github.com/JlusS/book-repo

# Navigate into the project
cd book-repo

# Build with Docker:
docker build -t book-repo .

# Or with Maven:
mvn install
```
### ▶️ Usage
```bash
Run with Docker:
docker run -it book-repo

Run with Maven:
mvn spring-boot:run

```
Once running, access:
Application: http://localhost:8080

Swagger UI: http://localhost:8080/swagger-ui.html

API Documentation: http://localhost:8080/v3/api-docs

###🧪 Testing
```bash
Run tests with Maven:
mvn test

Run tests in Docker:
docker run -it book-repo mvn test
```
### 📁 Project Structure
```
book-repo/
 ├── src/
 │   ├── main/java/com/bookrepo/
 │   │   ├── controller/       # REST controllers (BookController, OrderController, CartController, AuthController)
 │   │   ├── dto/              # Data Transfer Objects (BookDTO, UserDTO, OrderDTO, etc.)
 │   │   ├── entity/           # JPA entities (Book, User, Role, Cart, Order, OrderItem)
 │   │   ├── repository/       # Spring Data repositories for each entity
 │   │   ├── service/          # Business logic (BookService, OrderService, CartService, UserService)
 │   │   ├── security/         # JWT configuration, filters, role-based access
 │   │   └── config/           # Spring and application configuration
 │   └── test/java/com/bookrepo/
 │       ├── controller/       # Controller layer tests
 │       ├── service/          # Unit tests for business logic
 │       └── repository/       # Database integration tests with Testcontainers
 ├── resources/
 │   ├── db/changelog/         # Liquibase migration scripts
 │   ├── application.yml       # Spring Boot configuration
 │   └── logback.xml           # Logging configuration
 ├── docs/
 │   ├── model-diagram.png     # Entity-Relationship diagram
 │   └── postman/              # Postman collections for API testing
 ├── Dockerfile                # Docker image build
 ├── docker-compose.yml        # Containerized environment setup
 └── pom.xml                   # Maven project descriptor
```
### 🗺 Roadmap

Add payment integration (Stripe/PayPal)

Improve admin panel for managing books and users

Deploy CI/CD pipeline with GitHub Actions

Add frontend (React/Angular) for full-stack demo


## 🤝 Contribution
Contributions are welcome! Please fork the repository and submit a pull request.
