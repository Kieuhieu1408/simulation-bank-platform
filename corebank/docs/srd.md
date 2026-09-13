# System Requirements & Architecture Document (Corebank)

## 1. Architecture Overview

Corebank is the central ledger service for the `simulation-bank-platform`. It is designed with **Cloud-Native & Distributed System** principles to handle high loads and ensure data consistency.

### Key Architectural Patterns
- **CQRS (Command Query Responsibility Segregation):**
  - **Internal CQRS:** The application cleanly separates write operations (Commands) from read operations (Queries) both at the handler level (`handler/command` vs `handler/query`) and the service level (`service/command` vs `service/query`).
  - **Read Replica (ODS):** In production, read queries should be routed to an Operational Data Store (ODS) like Oracle Active Data Guard to prevent heavy querying from impacting the master ledger database.
- **BFF (Backend for Frontend) / Satellite Services:**
  - Services like `money-bank` act as a BFF and will call Corebank's Read APIs (Query) to fetch history. Transaction history is the single source of truth and must not be duplicated outside Corebank.
- **Concurrency & Locking:**
  - **Race Condition Prevention:** Transfer creation and transaction logging happen within the same database transaction.
  - **Pessimistic/Optimistic Locking:** Row-level locks (e.g., `SELECT FOR UPDATE` in pessimistic locking) are used on account balances to prevent lost updates during concurrent transfers.
- **Idempotency:**
  - All state-changing APIs (e.g., `POST /transfers`) require an `idempotencyKey` to ensure that duplicate requests (retries) do not result in double deductions.
- **Distributed Transactions:**
  - Corebank receives requests via Cadence Workflows or Kafka from satellite services. It leverages the **Outbox Pattern** to reliably publish domain events and report transaction results back to other services.

## 2. Project Structure

The project follows a Domain-Driven Design (DDD) inspired structure combined with CQRS:

```text
corebank/src/main/java/com/hieu/corebank/
├── api              # REST Controllers mapping HTTP requests to Handlers
├── config           # Spring configurations (e.g., Initializers, Security)
├── constant         # Enums and Constants (e.g., AccountStatus)
├── domain           # JPA Entities (Account, BankCard, BankTransaction, Customer)
├── dto              # Data Transfer Objects
│   ├── request      # API Request payloads
│   └── response     # API Response payloads
├── exception        # Custom Exceptions (BusinessException, NotFoundException)
├── handler          # CQRS Handlers (Business Use Cases)
│   ├── command      # Command Handlers (Write operations, state changes)
│   └── query        # Query Handlers (Read operations, data retrieval)
├── repository       # Spring Data JPA Repositories for database access
└── service          # Domain/Application Services
    ├── command      # Services supporting write operations (handling persistence, locks)
    └── query        # Services supporting read operations (fetching data)
```

### Flow of Data (Write / Command)
1. **Controller (`api`)** receives a Request DTO.
2. Controller delegates to a **Command Handler (`handler/command`)**.
3. Command Handler uses **Command Services (`service/command`)** to lock entities, perform domain logic (e.g., `account.debit()`), and save state.
4. **Command Services** interact with **Repositories (`repository`)**.

### Flow of Data (Read / Query)
1. **Controller (`api`)** receives a query request.
2. Controller delegates to a **Query Handler (`handler/query`)**.
3. Query Handler uses **Query Services (`service/query`)** to fetch data.
4. Data is mapped to a Response DTO and returned.

## 3. Core Domain Entities

- **Customer:** Identified by a unique CIF (Customer Information File).
- **Account:** Checking accounts belonging to a Customer. Contains currency, balance, and status.
- **BankCard:** Simulated debit/credit cards linked to an Account.
- **BankTransaction:** Immutable ledger entries representing money movement (source, destination, amount, status).

## 4. Observability & Logging
- Strict usage of `@Slf4j` for logging.
- Contextual logging with `correlationId` and `traceId` to trace distributed requests across services (e.g., tracing a transfer initiated from `money-bank` through to `corebank`).
- Logs are formatted in JSON for aggregation in Loki.
