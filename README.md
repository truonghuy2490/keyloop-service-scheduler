# Keyloop Service Scheduler

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-green?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?logo=postgresql)
![CI: GitHub Actions](https://img.shields.io/badge/CI-GitHub_Actions-2088FF?style=flat&logo=githubactions&logoColor=white)
![CD: Azure DevOps](https://img.shields.io/badge/CD-Azure_DevOps-0078D4?style=flat&logo=azuredevops&logoColor=white)

[![Codecov](https://codecov.io/github/truonghuy2490/keyloop-service-scheduler/graph/badge.svg)](https://codecov.io/github/truonghuy2490/keyloop-service-scheduler)

A backend automotive service appointment scheduler built with **Java 21**, **Spring Boot**, and **PostgreSQL**. The application replaces manual booking workflows with resource-constrained scheduling, technician qualification and working-hour validation, and persistent appointment records.

This project implements **Scenario A — The Unified Service Scheduler** for the Keyloop coding challenge.


## Tech Stack

| Category | Technologies |
| --- | --- |
| Backend | Java 21, Spring Boot 4.1.2 |
| Database | PostgreSQL, Flyway |
| Data access | Spring Data JPA, Hibernate, Spring JDBC |
| Architecture | Clean Architecture |
| Build | Maven, Maven Wrapper |
| Unit testing | JUnit 5, Mockito, Maven Surefire |
| Integration testing | Spring Boot Test, Testcontainers (PostgreSQL), Maven Failsafe |
| Coverage | JaCoCo, Codecov |
| API documentation | Swagger UI, OpenAPI |
| Infrastructure | Docker, Docker Compose, Azure |
| CI/CD | GitHub Actions (CI), Azure Pipelines (deployment simulation) |

## Core Features

- Dealership, service type, customer, and vehicle lookup.
- Technician lookup, qualification validation, and dealership assignment.
- Technician recurring working hours and date-specific overrides, including leave and shortened shifts.
- Appointment availability checks for the requested service duration.
- Service bay selection and appointment creation within a database transaction.
- PostgreSQL exclusion constraints to prevent overlapping confirmed reservations for shared resources.
- Appointment records linking customer, vehicle, dealership, service type, technician, service bay, and schedule.
- Automated unit and PostgreSQL-backed integration tests.

## Core Booking Requirements

### 1. Resource-Constrained Booking

Customers request an appointment for a vehicle, service type, dealership, preferred technician, and start time.

### 2. Availability Validation

Before confirming a booking, the application checks:

- Whether the technician belongs to the dealership and is qualified for the service.
- Whether the technician is working for the complete requested duration, accounting for weekly hours and date-specific overrides.
- Whether the dealership is open and an active service bay is available for the complete duration.
- Whether the technician or service bay already has an overlapping appointment.

### 3. Persistent Appointment Creation

Successful bookings persist an appointment with the customer, vehicle, dealership, technician, service bay, service type, status, and schedule.

### 4. Concurrent Booking Safety

Appointment creation is coordinated through a transaction abstraction in the Application Layer, with availability validation and backend exception handling.

The current implementation does not introduce explicit database locking or custom transaction isolation levels. Handling concurrent requests for the same resources remains a known limitation and an area for future improvement.

**Current trade-off:** In a race for the same available service bay, one request may be rejected by the database constraint, even if another bay is free. The database prevents double booking, but the application does not yet guarantee optimal bay allocation or automatic retry after a conflict. Improved conflict-specific API responses and locking/retry strategies are potential enhancements.

## API Endpoints

The application uses a versioned API path, such as `/api/1.0` (represented as `/api/{version}` in controller mappings). Refer to Swagger UI for the latest route and request definitions.

| Method | Endpoint | Description | Status |
| --- | --- | --- | --- |
| GET | `/api/1.0/dealerships` | List dealerships | Completed |
| GET | `/api/1.0/dealerships/{id}` | View dealership details | Completed |
| GET | `/api/1.0/dealerships/{id}/service-types` | List dealership service types | Completed |
| GET | `/api/1.0/vehicles` | List customer vehicles | Completed |
| GET | `/api/1.0/service-bay` | List dealership service bays | Completed |
| GET | `/api/1.0/dealership/{id}/technicians` | List dealership technicians | Completed |
| GET | `/api/1.0/technicians/{id}` | View technician details and schedule | Completed |
| GET | `/api/1.0/appointments/availability` | Check availability by dealership, service type, and date | Completed |
| POST | `/api/1.0/appointments` | Create an appointment | Completed |
| GET | `/api/1.0/appointments/{id}` | View appointment details | Completed |

> Endpoint paths and implementation status reflect the current working draft. Confirm exact paths against Swagger, particularly the service bay and technician routes, before publishing.

## Architecture

The codebase separates business rules, use cases, persistence, and external concerns using Clean Architecture.

### Clean Architecture Diagram

![Clean Architecture Diagram](docs/images/the-clean-architecture.png)

### Database Entity Relationship Diagram

![Keyloop Service Scheduler ERD](docs/images/keyloop-schedular-ERD.png)

### Layers

1. **Domain** — Entities and core domain rules.
2. **Application** — Use cases, commands/queries, validation, repository abstractions, and result handling.
3. **Persistence** — JPA entities, repositories, adapters, database migrations, and seed data.
4. **Infrastructure** — Transaction execution and supporting technical services.
5. **Presentation** — REST controllers, request/response contracts, and OpenAPI configuration.

## Prerequisites

- JDK 21
- Docker Desktop or another running Docker engine
- Git

The Maven Wrapper is included, so a separate Maven installation is optional.

## Running Locally

### 1. Clone the Repository

```bash
git clone https://github.com/truonghuy2490/keyloop-service-scheduler.git
cd keyloop-service-scheduler
```

### 2. Start PostgreSQL

From the repository root, start the configured services:

```bash
docker compose up -d
```

### 3. Configure Database Settings

Example environment variables (match these with your Spring configuration):

```env
DB_URL=jdbc:postgresql://localhost:5432/keyloop_scheduler
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

### 4. Start the Backend

```bash
cd backend
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Flyway migrations are configured for PostgreSQL. Apply migrations through the application's startup configuration or your existing migration process; do not manually insert test data into production.

## Testing

The project has two complementary test levels:

- **Unit tests:** JUnit 5 and Mockito validate use case behavior with mocked dependencies.
- **Integration tests:** Spring Boot Test and Testcontainers run against an isolated PostgreSQL database, validating persistence, seed data, transactions, and appointment conflict handling.

A running Docker engine is required for Testcontainers integration tests. The test database is separate from the local development database.

From the `backend` directory:

```bash
# Run unit tests (Maven Surefire)
./mvnw test

# Run unit tests and integration tests (Maven Failsafe)
./mvnw clean verify
```

Run the booking concurrency test class with Failsafe:

```bash
./mvnw verify -Dtest=none -Dsurefire.failIfNoSpecifiedTests=false -Dit.test=BookAppointmentConcurrencyIT
```

The concurrency suite covers cases such as competing requests for a technician, requests for different technicians, back-to-back appointments, seeded conflicts, and bookings outside working hours. Date-specific overrides are taken into account when choosing test dates.

**Concurrency behavior:** The exclusion constraint prevents duplicate overlapping service bay reservations. A concurrency test may observe one successful request and another rejected request when both race for the same bay; this is a known limitation of the current allocation strategy, not evidence of duplicate persisted bookings.

JaCoCo generates coverage output during Maven verification. GitHub Actions uploads test reports and coverage to Codecov when configured.

## API Documentation

Once the backend is running, access:

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

The actual port depends on your application configuration.

For deployment, access:

- Swagger UI: `https://service-scheduler-demo-huy-dyhnh7c5c6hde4en.swedencentral-01.azurewebsites.net/swagger-ui/index.html`
- OpenAPI JSON: `https://service-scheduler-demo-huy-dyhnh7c5c6hde4en.swedencentral-01.azurewebsites.net/v3/api-docs`
- Website: `https://keyloop-service-scheduler-nine.vercel.app`



## CI/CD

### GitHub Actions — Backend CI

The backend workflow is configured to:

1. Check out the repository and set up Java 21.
2. Run `./mvnw clean verify` for builds and tests.
3. Generate JaCoCo coverage reports.
4. Upload test and coverage artifacts.
5. Publish coverage to Codecov.

Integration tests require Docker on the runner and a correctly configured Maven Failsafe plugin. Both Surefire and Failsafe reports should be uploaded to retain results from both test levels.

### Azure Pipelines — Continuous Deployment

Azure Pipelines automates the backend build, testing, packaging, and deployment process.

The pipeline builds the Spring Boot application using Java 21, publishes the executable JAR as a build artifact, and deploys it to Azure App Service.

After deployment, a health check verifies that the backend is running successfully.


## Known Limitations and Next Steps
- Add database-level concurrency protection or explicit locking to prevent simultaneous overlapping reservations; the current transaction and exception handling do not guarantee this.

- Consider resource locking or a bounded retry strategy so concurrent bookings can use another available service bay.

- Confirm and document the appointment details endpoint and any additional status/cancellation APIs as they are completed.

## AI Collaboration
AI Collaboration Narrative I used ChatGPT as a development assistant for architecture discussions, REST API implementation, debugging, and testing. I guided AI with specific requirements, existing code, and Clean Architecture conventions, then reviewed and adapted its suggestions rather than accepting them unchanged.

For appointment booking, I chose database transactions and backend exception handling. I also evaluated concurrency risks and recognize that transactions alone do not prevent every overlapping booking race. I verified changes through code review, unit and integration tests, Maven builds, and CI/CD logs. I remained responsible for the final design decisions and code quality.


## License
Developed for learning and technical demonstration purposes.