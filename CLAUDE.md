# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
./mvnw clean package

# Run (H2 profile for development)
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2

# Run (PostgreSQL profile for production)
./mvnw spring-boot:run -Dspring-boot.run.profiles=pg

# Docker build and run
docker build -t back-osteopathy-office .
docker run -p 80:8080 back-osteopathy-office
```

## Architecture

Spring Boot 3.2.5 backend for osteopathy clinic management. Kotlin 1.9.23, Java 17.

### Layer Structure

```
Controller.kt (REST endpoints: /{contextpath}/api/...)
    ↓
PatientService.kt / AuditService.kt (profile-based implementations)
    ↓
Repositories.kt (Spring Data JPA CrudRepository interfaces)
    ↓
entities/Patient.kt (JPA entities: Patient, Visit, Audit)
    ↓
dto/Patient.kt + mapper/Mapper.kt (MapStruct DTO conversion)
```

### Multi-Tenant Pattern

Every entity has a `tenant` field. All API endpoints include tenant in path: `/{contextpath}/api/patient/{id}`. PatientService validates tenant ownership before returning data.

### Database Profiles

- **H2 (`-Dspring-boot.run.profiles=h2`)**: Development database at `~/h2db`, console at `/h2-console`
- **PostgreSQL (`-Dspring-boot.run.profiles=pg`)**: Production database, uses TEXT fields for larger content

Schemas: `schema.sql` (H2), `schema_pg.sql` (PostgreSQL)

### Service Implementation Pattern

Abstract base class with profile-specific concrete implementations:
- `H2PatientService` (`@Profile("h2")`)
- `PgPatientService` (`@Profile("pg")`)

Database-specific queries are overridden in each implementation.

### Key Domain Entities

- **Patient**: Core patient record with personal info and next visit tracking
- **Visit**: Detailed clinical assessment with 40+ regional evaluation fields for osteopathy diagnostics
- **Audit**: Access logging with automatic cleanup (records older than 10 days deleted via scheduled task)
