# TaskManager

**TaskManager** is a secure, extensible backend system for managing user tasks, showcasing backend design and architecture best practices. It is built using modern Java technologies and is structured for scalability, maintainability, and production-readiness.

## Key Features

### 🔐 JWT-Based Security

Implements stateless authentication using **JSON Web Tokens (JWT)** with Spring Security. Users authenticate via a login endpoint and receive a signed JWT, which is then used to access protected resources. Role-based authorization ensures only permitted actions are executed per user role.

- Stateless session management
- Custom `JwtFilter` and `AuthenticationEntryPoint`
- Role-based access control with `@PreAuthorize` support

---

### 🔄 Kafka-Driven Messaging

Applies **Apache Kafka** to publish task completion events. This decouples the main application from secondary concerns like notifications, logging, or reporting, enabling asynchronous, scalable workflows.

- Kafka producer triggered on task completion
- Consumer service handles downstream processing
- Designed for extensibility with multiple subscribers

---

### 🛠️ Production-Ready CI/CD

The entire application is **Dockerized** and integrated with **Jenkins pipelines**. It includes automated steps for testing, image building, and deployment, simulating real-world deployment workflows.

- Dockerfiles and docker-compose for service orchestration
- Jenkinsfile for automated build/test/deploy
- GitHub integration for CI triggers

---

### 📝 Audit Logging via AOP

Utilizes **Spring AOP (Aspect-Oriented Programming)** to automatically log sensitive operations (e.g., task creation, deletion) without polluting business logic.

- Custom `@AuditLog` annotation
- Aspect intercepts annotated methods to persist or print logs
- Easily extendable to monitor additional actions

---

### ♻️ Soft Deletion and Recovery

Supports soft deletion of tasks using a boolean `deleted` flag. Deleted tasks are hidden from normal queries but can be restored individually or in bulk.

- Soft-delete flag with `@SQLRestriction`
- Scheduled job cleans up old records
- Endpoints to restore tasks by ID or all at once

---

## Tech Stack

- **Backend**: Java 17, Spring Boot, Spring Security, Spring AOP
- **Persistence**: Hibernate (JPA), MySQL
- **Messaging**: Apache Kafka
- **DevOps**: Docker, Jenkins, Git
- **Utilities**: Lombok, MapStruct, Maven

## Package Structure

```
de.personal.taskmanager
├── annotation        # Custom annotations (e.g., @AuditLog)
├── aop               # Aspect-oriented audit logging
├── common            # Mappers and utility components
├── config            # Configuration classes (JPA, Security, Async)
├── controller        # RESTful controllers
├── dto               # Data transfer objects
├── exception         # Global and security-related error handling
├── message           # Kafka producers and consumers
├── model             # Entity definitions and enums
├── repository        # JPA repository interfaces
├── security          # JWT handling and filter config
├── service           # Business logic and implementations
```

---

## 📐 Project Architecture

*📌 Reserved for project architecture diagram*

---

## 📊 API Documentation (Swagger UI)

*📌 Reserved for Swagger UI screenshot*

---

This project is intended as a demonstration of backend engineering proficiency for job applications and technical evaluations. It reflects real-world system design approaches and production-ready implementation patterns.
