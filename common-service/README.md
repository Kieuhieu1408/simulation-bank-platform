# Common Service (common-service)

## Overview

`common-service` is a Spring Boot module designed as a shared library for the microservices in the Simulation Bank Platform (e.g., `money-bank`, `corebank`). It encapsulates common cross-cutting concerns, ensuring consistent implementations across the ecosystem and adhering to the DRY (Don't Repeat Yourself) principle.

The service provides unified logic for:
- **Global Exception Handling** (Consistent error mapping and formats).
- **Security & Authorization** (JWT processing, RBAC foundations, Identity Context).
- **Idempotency** (Ensuring safe retries via AOP and tracking).
- **Outbox Pattern** (Transactional Outbox components to ensure reliable message dispatching).
- **Observability** (Tracing, logging correlation filters, and sensitive data masking).
- **Utilities** (Date/Time logic, constants).

## Module Structure

```
com.hieu.common
├── exception            // Global exceptions handling, custom Business/System exceptions, and ErrorCodes
├── idempotency          // Idempotency AOP tracking to avoid duplicate POST/PUT processing
├── observability        // Tracing and logging logic (CorrelationID, Data Masking)
├── outbox               // Transactional Outbox pattern entities, repository, and publisher logic
├── security             // Common Security components (JWT Validation, Identity Context extraction)
└── util                 // Common configuration and utilities (e.g., TimeConfig)
```

## How to Integrate

In your microservice (e.g. `corebank` or `money-bank`), add the dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>com.hieu</groupId>
    <artifactId>common-service</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 1. Exception Handling
All services implicitly benefit from `GlobalExceptionHandler`. 
Throw `com.hieu.common.exception.CommonException` with corresponding `ErrorCode`s for any domain or technical errors. The unified interceptor will parse it into a standard HTTP Response (`ApiErrorResponse`).

### 2. Security
The provided `SecurityConfig` ensures all API endpoints are `denyAll` by default unless explicitly granted. It extracts identities using `JwtAudienceValidator` and loads the user scopes into the ThreadLocal-backed `IdentityContext`. Services can directly fetch the authenticated user info:
```java
String userId = IdentityContext.getCurrentUserId();
```

### 3. Idempotency
Ensure retries don't mutate state twice by utilizing the idempotency framework provided. Annotate the susceptible methods, and the framework will intercept, hash the payload, and track the state using the `idempotency_record` database table.

### 4. Observability
Automatic correlation ID propagation and data masking. The `CorrelationIdFilter` attaches a unique `trace_id` per request lifecycle which can be retrieved for internal tracing contexts. Log masking protects sensitive customer data based on internal configurations.

### 5. Outbox Pattern
Using Transactional Outbox means storing the internal Database Transaction along with a message payload intended for event brokers. Use the provided interfaces `OutboxEventRepository` and `OutboxDispatcher` to implement reliable message dispatching without data loss.

## Migration Note

The shared logic was previously duplicated in `money-bank` and `corebank` under their respective `shared` packages. They have now been consolidated here. Please ensure to remove any duplicated `com.hieu.<service>.shared` code and rely entirely on `com.hieu.common`.
