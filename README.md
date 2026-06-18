# 🏥 Doctor Appointment Booking System
 
A Spring Boot microservices application that lets patients discover doctors, book appointments, and pay for consultations — built around service discovery, an API gateway, and JWT-based authentication.
 
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
 
---
 
## 📖 Overview
 
This project simulates a real-world hospital/clinic booking platform decomposed into independently deployable microservices. Each service owns its own database, communicates through REST (and Feign for service-to-service calls), and registers itself with a central Eureka server so the API Gateway can route requests dynamically.
 
## 🏗️ Architecture

<img width="1536" height="1024" alt="Architecture Diagram" src="https://github.com/user-attachments/assets/3c54f12f-2d5f-48e9-82ba-f75ada7886fa" />

 
```
                              ┌─────────────────┐
                              │   Eureka Server  │  (port 8761)
                              │ Service Registry │
                              └────────▲─────────┘
                                       │ registers
        ┌──────────────────────────────┼──────────────────────────────┐
        │                              │                              │
┌───────▼────────┐  ┌────────────┐  ┌──▼────────────┐  ┌──────────────▼─┐
│  Auth Service   │  │  Doctor    │  │   Patient      │  │  Booking        │
│  (8083)         │  │  Service   │  │   Service      │  │  Service (8085) │
│  JWT + Security │  │  (8081)    │  │   (8082)       │  │  (Feign client) │
└─────────────────┘  └────────────┘  └────────────────┘  └─────────────────┘
                                                                  │
                                                          ┌───────▼────────┐
                                                          │ Payment Service │
                                                          │ (5555) — Stripe │
                                                          └─────────────────┘
 
        ┌──────────────────┐         ┌──────────────────┐
        │   API Gateway     │ ◄────── │  Client / Postman │
        │   (8080)          │         └──────────────────┘
        └──────────────────┘
        │
        ▼
   Routes requests to the services above based on path
 
        ┌──────────────────┐
        │  Admin Server     │  (9090) — Spring Boot Admin dashboard,
        │  monitors all     │  discovers services via Eureka
        │  registered apps  │
        └──────────────────┘
```
 
## 🧩 Services
 
| Service | Port | Responsibility | Key Tech |
|---|---|---|---|
| **eureka** | 8761 | Service registry — all other services register here | Netflix Eureka Server |
| **api-gateway** | 8080 | Single entry point; routes `/doctor/**`, `/patient/**`, `/booking/**`, `/auth/**` to the right service | Spring Cloud Gateway, JWT |
| **auth-service** | 8083 | User registration/login, JWT issuance | Spring Security, JWT, MySQL (`auth_db`) |
| **doctor-service** | 8081 | Doctor profiles & availability | Spring Data JPA, MySQL (`doctor_db`) |
| **patient-service** | 8082 | Patient records | Spring Data JPA, MySQL (`patient_db`) |
| **booking-service** | 8085 | Appointment booking; calls other services via Feign | Spring Data JPA, OpenFeign, MySQL (`bookingservicedb`) |
| **payment-service** | 5555 | Payment processing for consultations | Stripe Java SDK |
| **admin-server** | 9090 | Centralized health/monitoring dashboard for all services | Spring Boot Admin |
 
All services expose Actuator health/info endpoints (`/actuator/health`, `/actuator/info`) for monitoring.
 
## 🛠️ Tech Stack
 
- **Language/Framework:** Java 17, Spring Boot 3.5.7
- **Microservices:** Spring Cloud Gateway, Netflix Eureka (2025.0.0), OpenFeign
- **Security:** Spring Security, JWT (`java-jwt`)
- **Persistence:** Spring Data JPA / Hibernate, MySQL
- **Payments:** Stripe API
- **Monitoring:** Spring Boot Admin, Spring Boot Actuator
- **Build Tool:** Maven
## 🚀 Getting Started
 
### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8+ running locally
- A Stripe account/API key (for `payment-service`)
### 1. Clone the repo
```bash
git clone https://github.com/HruthvikMV/Docter-Appointment-App.git
cd Docter-Appointment-App
```
 
### 2. Create the databases
```sql
CREATE DATABASE auth_db;
CREATE DATABASE doctor_db;
CREATE DATABASE patient_db;
CREATE DATABASE bookingservicedb;
```
 
### 3. Configure credentials
Each service's `src/main/resources/application.properties` has placeholders for your MySQL username/password and (for `payment-service`) your Stripe key. **Don't commit real credentials** — consider externalizing them as environment variables, e.g.:
```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```
 
### 4. Run the services (in this order)
```bash
# 1. Service registry first
cd eureka && ./mvnw spring-boot:run
 
# 2. Then the rest (in separate terminals)
cd auth-service && ./mvnw spring-boot:run
cd doctor-service && ./mvnw spring-boot:run
cd patient-service && ./mvnw spring-boot:run
cd booking-service && ./mvnw spring-boot:run
cd payment-service && ./mvnw spring-boot:run
cd admin-server && ./mvnw spring-boot:run
 
# 3. Gateway last, once everything is registered
cd api-gateway && ./mvnw spring-boot:run
```
 
### 5. Verify
- Eureka dashboard: `http://localhost:8761`
- Admin dashboard: `http://localhost:9090`
- API Gateway: `http://localhost:8080`
## 🔌 API Gateway Routes
 
| Path | Routed To |
|---|---|
| `/doctor/**` | `doctor-service` (8081) |
| `/patient/**` | `patient-service` (8082) |
| `/booking/**` | `booking-service` (8085) |
| `/auth/**` | `auth-service` (8083) |
 
> Example: `POST http://localhost:8080/auth/login` → forwarded to `auth-service`'s `/login` endpoint.
 
## 📂 Project Structure
```
Docter-Appointment-App/
├── admin-server/      # Spring Boot Admin dashboard
├── api-gateway/        # Spring Cloud Gateway + JWT
├── auth-service/        # Authentication & JWT issuance
├── booking-service/      # Appointment booking (Feign client)
├── doctor-service/      # Doctor profiles
├── eureka/              # Service registry
├── patient-service/    # Patient records
└── payment-service/    # Stripe payment processing
```
 
## 🔮 Future Enhancements
- Containerize each service with Docker + a `docker-compose.yml` for one-command startup
- Add a CI/CD pipeline (Jenkins/GitHub Actions) for automated build & deploy
- Centralized configuration via Spring Cloud Config Server
- API documentation with Swagger/OpenAPI
- Distributed tracing (Sleuth/Zipkin) and centralized logging
## 👤 Author
**Hruthvik M V**
[GitHub](https://github.com/HruthvikMV)
 
