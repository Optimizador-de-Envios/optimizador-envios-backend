# BACKEND DEVELOPMENT INSTRUCTIONS

Backend must follow Hexagonal Architecture (Ports & Adapters).

---

## STRUCTURE

src/main/java/com/app/

domain
 ├ model
 ├ valueobject
 ├ service
 └ exception

application
 ├ usecase
 └ port
     ├ input
     └ output

infrastructure
 └ adapter
     ├ input
     │   └ rest
     │       └ dto
     └ output
         ├ persistence
         │   └ entity
         └ external
 └ mapper

config
 └ exception

Do not create additional folders without permission.

---

## LAYER RULES

### DOMAIN

- Pure Java
- No Spring
- No annotations
- No database
- No HTTP
- Only business rules
- Custom exceptions allowed

---

### APPLICATION

- Use cases
- Orchestration
- Uses domain
- Defines ports (interfaces)

---

### INFRASTRUCTURE

- Implements ports
- JPA repositories
- REST clients
- External APIs
- Contains mappers

---

### CONTROLLERS

- Handle HTTP
- Use DTOs
- Call use cases
- Return responses

🚫 NO business logic

---

## MAPPERS (MANDATORY)

Must exist for:

- Entity ↔ Domain
- DTO ↔ Domain

Rules:

- No business logic
- One responsibility
- Explicit mapping

---

## EXCEPTIONS

### DOMAIN

- Business exceptions
- Example: InvalidOrderException

---

### GLOBAL HANDLER

Must exist:

- @RestControllerAdvice
- Maps exceptions to HTTP responses
- No business logic

---

## ORDER

1 domain
2 application
3 infrastructure (including mappers)
4 controllers

Do not skip.

---

## TDD

Strict TDD required:

RED
GREEN
REFACTOR

Use:

- JUnit
- Mockito

Tests are mandatory.

---

## SOLID

- Single Responsibility
- No duplication
- Clear abstractions
- Maintain boundaries

---

## DATABASE RULES

- Entities ONLY in infrastructure
- Domain must not depend on DB
- Always map Entity ↔ Domain

---

## DTO RULES

- Only in controllers layer
- Never in domain
- Always mapped

---

## DEPENDENCY RULE

infrastructure → application → domain

NEVER the opposite

---

## STRICT MODE

Warn if:

- Logic in controller
- Logic in mapper
- Spring in domain
- Missing mapper
- Entity leaking to domain
- DTO leaking to domain
- Missing tests
- Violates SOLID
- Not aligned with PRD

---

## GOAL

- Clean architecture
- Strong separation of concerns
- High testability
- Production-ready backend