# 📚 Book-Repo
*Empowering Discovery, Simplifying Your Book Journey*

![Java](https://img.shields.io/badge/Java-17-blue?logo=java)  
![Maven](https://img.shields.io/badge/Maven-Build-orange?logo=apachemaven)  
![Docker](https://img.shields.io/badge/Docker-Containerization-blue?logo=docker)  
![Spring](https://img.shields.io/badge/Spring-Boot-brightgreen?logo=springboot)  

---

## 📖 Overview


## 🛠 Technology Stack  
| Technology              | Version   |
|--------------------------|-----------|
| Java                    | 17        |
| Spring Boot             | 3.3.3     |
| Spring Security         | bundled   |
| Spring Data JPA         | bundled   |
| Spring Validation       | bundled   |
| Springdoc OpenAPI       | 2.1.0     |
| Liquibase               | latest    |
| MapStruct               | 1.5.5.Final |
| Lombok                  | 1.18.36   |
| JWT (jjwt)              | 0.11.5    |
| Hibernate Validator     | bundled   |
| MySQL Connector/J       | 8.4.0 / 8.0.33 |
| Docker Compose Support  | 3.1.1     |
| Testcontainers (core)   | 1.19.0    |
| Testcontainers BOM      | 1.21.3    |
| JUnit (Spring Boot Test)| bundled   |
| Maven Compiler Plugin   | 3.3.0+    |
| Maven Checkstyle Plugin | 3.3.0     |
  
**Book-Repo** is a full-featured **online book store application** built with **Java & Spring Boot**.  
It demonstrates best practices for modern backend development: modular architecture, containerized deployment, and comprehensive testing.  

### ✨ Key Features  
- 🛠 **Dependency Management**: Maven + Spring Boot, Liquibase, MapStruct, JWT, Testcontainers  
- 🐳 **Containerized Environment**: Docker Compose & multi-stage Docker builds  
- 🔒 **Security & Authentication**: JWT-based authentication with role-based access control  
- ⚙ **Modular Architecture**: Specifications & dynamic query building for scalable data access  
- 🧪 **Extensive Testing**: Unit, integration, and controller tests ensure reliability  
- 📦 **E-commerce Features**: shopping cart, order management, and user roles  

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
```

Build with **Docker**:  
```bash
docker build -t book-repo .
```

Or with **Maven**:  
```bash
mvn install
```

---

## ▶️ Usage  

Run with **Docker**:  
```bash
docker run -it book-repo
```

Run with **Maven**:  
```bash
mvn spring-boot:run
```

---

## 🧪 Testing  

Run tests with **Maven**:  
```bash
mvn test
```

Run tests in **Docker**:  
```bash
docker run -it book-repo mvn test
```

---

## 📂 Project Structure  
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

### 🗃 Domain Model  
- **Book**: stores information about title, author, genre, price, stock, etc.  
- **User**: application users with roles (ADMIN, CUSTOMER).  
- **Role**: defines permissions for users.  
- **Cart**: holds items selected by a user before checkout.  
- **Order**: finalized purchase with order items, status, and total price.  
- **OrderItem**: association between books and orders with quantity & price.
---

## 🗺 Roadmap   
- [ ] Add payment integration (Stripe/PayPal)  
- [ ] Improve admin panel for managing books and users  
- [ ] Deploy CI/CD pipeline with GitHub Actions  
- [ ] Add frontend (React/Angular) for full-stack demo  

---

## 🤝 Contribution  
Contributions are welcome! Please fork the repository and submit a pull request.  
