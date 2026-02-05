# Osteopathy Office Backend

Spring Boot backend for osteopathy clinic management with multi-tenant support.

## Tech Stack

- Kotlin 1.9.23 / Java 17
- Spring Boot 3.2.5
- Spring Data JPA / Hibernate
- MapStruct for DTO mapping
- H2 (development) / PostgreSQL (production)

## Architecture

### Layer Structure

```
Controller.kt (REST endpoints)
    ↓
PatientService.kt / AuditService.kt (business logic)
    ↓
Repositories.kt (Spring Data JPA)
    ↓
entities/Patient.kt (JPA entities)
    ↓
dto/Patient.kt + mapper/Mapper.kt (MapStruct DTOs)
```

### Multi-Tenant Support

All entities include a `tenant` field. API endpoints use path-based tenant isolation: `/{contextpath}/api/...`. Services validate tenant ownership before returning data.

### Domain Model

#### Patient

Core patient record with personal information and scheduling.

| Field | Type | Description |
|-------|------|-------------|
| id | Int | Primary key |
| tenant | String | Tenant identifier |
| firstName, lastName, middleName | String | Patient name |
| phone | String | Contact phone |
| birthDate | String | Date of birth |
| description | String | General notes |
| nextVisit | Timestamp | Scheduled next appointment |
| visits | List\<Visit\> | Related visit records |

#### Visit

Detailed clinical assessment record for osteopathy diagnostics.

| Field Group | Fields | Description |
|-------------|--------|-------------|
| Basic | id, tenant, visitDate | Record identification |
| Clinical Notes | complaints, anamnesis | Patient complaints and history |
| Global Assessment | globBio, globRithmKarnial, globRithmKardio, globRightBreath, globNeiroPsihosomat, globNeiroPostural | Overall body function scores (Int) |
| Regional Structure | regionHeadStruct, regionNeckStruct, regionHandsStruct, regionBrestStruct, regionLowerBackStruct, regionPelvicStruct, regionLegsStruct, regionDuraMaterStruct | Structural assessment by region (Int) |
| Regional Visceral | regionNeckVistz, regionBrestVistz, regionLowerBackVistz, regionPelvicVistz | Visceral assessment by region (Int) |
| Spinal Segments | regionCr, regionC1C3Vistz/Som, regionC4C6Vistz/Som, regionC7Th1Vistz/Som, regionTh2Th5Vistz/Som, regionTh6Th9Vistz/Som, regionTh10L1Vistz/Som, regionL2L5Vistz/Som | Spinal segment visceral/somatic scores (Int) |
| Treatment | localDisfunction, dominant, treatmentPlan, recommendations, specialists | Diagnosis and treatment notes |

#### Audit

Access logging for tracking patient record views.

| Field | Type | Description |
|-------|------|-------------|
| id | Int | Primary key |
| tenant | String | Tenant identifier |
| tm | OffsetDateTime | Access timestamp |
| recType | String | Record type ("create", "enter") |
| patientId | Int | Referenced patient |

Records older than 10 days are automatically deleted by a scheduled cleanup task.

### Database Abstraction

Profile-based service implementations allow database-specific query optimizations:
- `H2PatientService` (`@Profile("h2")`)
- `PgPatientService` (`@Profile("pg")`)

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL (for production)

### Run with H2 (Development)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

H2 console available at `http://localhost:8080/h2-console`

### Run with PostgreSQL (Production)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=pg
```

### Docker

```bash
docker build -t back-osteopathy-office .
docker run -p 80:8080 back-osteopathy-office
```

## API Endpoints

All endpoints are prefixed with `/{contextpath}/api/`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/patient` | Create patient |
| PUT | `/patient` | Update patient |
| GET | `/patient/{id}` | Get patient by ID |
| GET | `/patient/search?query=` | Search patients |
| GET | `/patient/latest` | Get recently viewed patients |

## Configuration

- `application-h2.properties` - H2 database settings
- `application-pg.properties` - PostgreSQL settings
- Database schemas in `schema.sql` (H2) and `schema_pg.sql` (PostgreSQL)

## Actuator Endpoints

Health and metrics available at `/actuator/health`, `/actuator/metrics`, `/actuator/info`
