# Software Requirements Document — Corebank V2
> **Status:** In Progress  
> **Author:** Hieu Quoc Kieu  
> **Last Updated:** 2026-09-27

---

## 1. Context & Problem Statement

### 1.1 What V1 Got Right
V1 already has meaningful CQRS separation at the **code layer**:
- `handler/command` — write-side handlers (CreateAccount, Transfer, IssueCard)
- `handler/query`   — read-side handlers (GetBalance, GetHistory, GetCustomer)
- `common-service`  — shared Dispatcher, Outbox, Idempotency infrastructure

The Outbox pattern is already wired in `common-service` (`OutboxRecorder`, `OutboxEvent`). This is a strong foundation.

### 1.2 What V1 Gets Wrong

| Pain Point | Root Cause | Symptom |
|---|---|---|
| **Pessimistic Lock** | `AccountRepository.findByIdForUpdate()` uses `PESSIMISTIC_WRITE` | Threads queue up waiting for row locks; deadlock risk at high TPS |
| **Balance is mutable state** | `account.debit()` / `account.credit()` UPDATE the `balance` column | No audit trail of how a balance reached its current value |
| **No Available vs Actual split** | One `balance` column serves both purposes | Cannot represent a "pending" deduction; double-spending window exists |
| **Shared DB for Read & Write** | Both Command and Query hit the same `accounts` table | Write-side pressure degrades read-side performance |
| **Transaction atomicity illusion** | Debit source + credit destination in one JPA transaction | Works inside one DB, breaks when accounts are on different services |

---

## 2. V2 Architecture: True CQRS + Event Sourcing on Oracle

### 2.1 Core Philosophy

> **The database is a ledger, not a whiteboard.**  
> We never overwrite history. We only append facts.

### 2.2 Dual-Database Strategy (Polyglot Persistence)

```
┌─────────────────────────────────────────────────┐
│                  Write Side                     │
│              (Oracle — Event Store)             │
│                                                 │
│  domain_events        aggregate_snapshots       │
│  ─────────────        ─────────────────────     │
│  event_id (PK)        aggregate_id (PK)         │
│  aggregate_id         aggregate_type            │
│  aggregate_type       version                   │
│  version              payload (CLOB/JSON)       │
│  event_type           created_at                │
│  payload (CLOB)                                 │
│  metadata (CLOB)                                │
│  created_at                                     │
│                                                 │
│  UNIQUE (aggregate_id, version)  ← Optimistic   │
│                                    Locking      │
└────────────────────┬────────────────────────────┘
                     │  OutboxEvent (already exists)
                     │  published → Kafka topic
                     ▼
┌─────────────────────────────────────────────────┐
│                  Read Side                      │
│              (Oracle — Projections)             │
│                                                 │
│  account_view         transaction_history_view  │
│  ─────────────        ───────────────────────   │
│  account_id (PK)      id (PK)                   │
│  customer_id          account_id                │
│  currency             direction (DEBIT/CREDIT)  │
│  actual_balance       amount                    │
│  available_balance    balance_after             │
│  status               event_type                │
│  last_event_version   description               │
│  last_updated_at      occurred_at               │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 2.3 Optimistic Locking — Replacing Pessimistic Lock

**Old (V1):**
```java
// Blocks DB row — serializes ALL transfers for any account pair
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Account> findByIdForUpdate(String id);
```

**New (V2):**
```sql
-- Unique constraint on (aggregate_id, version) in domain_events
-- Two concurrent threads both try to INSERT version=6 for ACC-123
-- DB rejects the second → Java catches ConstraintViolation → retry
ALTER TABLE domain_events
  ADD CONSTRAINT uq_aggregate_version UNIQUE (aggregate_id, version);
```

**Why this is better:**  
- No row lock held during business logic execution  
- Conflict detected atomically at INSERT time (single I/O)  
- Retry only the conflicting command, not the entire transaction  

### 2.4 In-Memory Aggregate — Replacing Balance Column

The `AccountAggregate` lives on the JVM heap. It is reconstructed by replaying events:

```
Load Snapshot (version N) → Replay events (version N+1 … latest) → Aggregate ready
```

The aggregate holds two balance figures in memory:
- `actualBalance`    — committed money (modified only on TransferCompleted / Deposited)
- `availableBalance` — spendable money (modified immediately on FundsReserved)

**No SELECT needed to validate a transfer.** The aggregate on RAM answers in nanoseconds.

### 2.5 Saga for Cross-Account Transfer

```
TransferCommand
      │
      ▼
  [Validate on RAM: availableBalance >= amount]
      │ fail → TransferRejectedEvent
      ▼
  FundsReservedEvent  (availableBalance -= amount, actualBalance unchanged)
      │
      ▼
  [Saga: Credit destination account]
      │
      ├─ success → TransferCompletedEvent  (actualBalance -= amount)
      │
      └─ failure → TransferFailedEvent     (availableBalance += amount  [rollback])
```

Each arrow is an **event persisted to `domain_events`** before the next step begins.  
The Outbox carries these events to Kafka for downstream projectors.

---

## 3. New Database Schema (Flyway V5 & V6)

### 3.1 V5 — Write Side (Event Store)

```sql
-- domain_events: the single source of truth
CREATE TABLE domain_events (
    event_id        VARCHAR2(50)  NOT NULL,
    aggregate_id    VARCHAR2(50)  NOT NULL,
    aggregate_type  VARCHAR2(50)  NOT NULL,
    version         NUMBER(19)    NOT NULL,
    event_type      VARCHAR2(100) NOT NULL,
    payload         CLOB          NOT NULL,   -- JSON
    metadata        CLOB,                     -- correlation_id, actor, ip
    occurred_at     TIMESTAMP WITH TIME ZONE  NOT NULL,
    CONSTRAINT pk_domain_events      PRIMARY KEY (event_id),
    CONSTRAINT uq_aggregate_version  UNIQUE (aggregate_id, version)  -- Optimistic Lock
);

CREATE INDEX ix_domain_events_aggregate ON domain_events (aggregate_id, version);

-- aggregate_snapshots: avoids full-replay for busy accounts
CREATE TABLE aggregate_snapshots (
    aggregate_id    VARCHAR2(50)  NOT NULL,
    aggregate_type  VARCHAR2(50)  NOT NULL,
    version         NUMBER(19)    NOT NULL,
    payload         CLOB          NOT NULL,   -- JSON of {actualBalance, availableBalance, status, …}
    created_at      TIMESTAMP WITH TIME ZONE  NOT NULL,
    CONSTRAINT pk_aggregate_snapshots PRIMARY KEY (aggregate_id)
);
```

### 3.2 V6 — Read Side (Projections)

```sql
-- account_view: what the mobile app / query API reads
CREATE TABLE account_view (
    account_id            VARCHAR2(50)    NOT NULL,
    account_number        VARCHAR2(20)    NOT NULL,
    customer_id           VARCHAR2(20)    NOT NULL,
    currency              VARCHAR2(3)     NOT NULL,
    actual_balance        NUMBER(19, 4)   DEFAULT 0 NOT NULL,
    available_balance     NUMBER(19, 4)   DEFAULT 0 NOT NULL,
    status                VARCHAR2(20)    NOT NULL,
    last_event_version    NUMBER(19)      DEFAULT 0 NOT NULL,  -- idempotency guard
    last_updated_at       TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_account_view PRIMARY KEY (account_id)
);

-- transaction_history_view: what users see in their statement
CREATE TABLE transaction_history_view (
    id                VARCHAR2(50)    NOT NULL,
    account_id        VARCHAR2(50)    NOT NULL,
    direction         VARCHAR2(6)     NOT NULL,   -- DEBIT | CREDIT
    amount            NUMBER(19, 4)   NOT NULL,
    currency          VARCHAR2(3)     NOT NULL,
    balance_after     NUMBER(19, 4)   NOT NULL,
    event_type        VARCHAR2(100)   NOT NULL,
    description       VARCHAR2(255),
    occurred_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_tx_history_view PRIMARY KEY (id)
);

CREATE INDEX ix_txhv_account_date ON transaction_history_view (account_id, occurred_at DESC);
```

---

## 4. Key Domain Events

| Event | Aggregate | Trigger | Balance Effect |
|---|---|---|---|
| `AccountCreated` | Account | CreateAccount command | actual=initial, available=initial |
| `FundsReserved` | Account | Transfer initiated | available -= amount |
| `TransferCompleted` | Account | Destination confirmed | actual -= amount |
| `TransferFailed` | Account | Destination failed | available += amount (rollback) |
| `FundsDeposited` | Account | Deposit command | actual += amount, available += amount |
| `AccountStatusChanged` | Account | Freeze/unfreeze | no balance change |
| `CardIssued` | Account | IssueCard command | no balance change |

---

## 5. Projector Contract

A **Projector** is a Spring `@EventListener` (or Kafka consumer) that keeps the Read DB in sync:

1. Receive event from Outbox/Kafka
2. Check `last_event_version < event.version` to ensure idempotency
3. Execute SQL UPDATE on `account_view` / INSERT into `transaction_history_view`
4. Update `last_event_version` atomically

**Idempotency rule:**
```sql
UPDATE account_view
SET    available_balance  = available_balance - :amount,
       last_event_version = :newVersion,
       last_updated_at    = SYSTIMESTAMP
WHERE  account_id         = :accountId
AND    last_event_version < :newVersion;   -- skip if already applied
```

---

## 6. Refactor Plan (Step-by-Step)

### Phase 1 — Schema (no breaking changes to existing tables)
- [ ] Add Flyway `V5__event_store.sql` (write-side tables)
- [ ] Add Flyway `V6__read_projections.sql` (read-side tables)

### Phase 2 — Write Side (Event Store)
- [ ] Create `DomainEvent` JPA entity mapping `domain_events`
- [ ] Create `AggregateSnapshot` JPA entity mapping `aggregate_snapshots`
- [ ] Create `DomainEventRepository` (findByAggregateId, save)
- [ ] Create `SnapshotRepository`
- [ ] Create `AccountAggregate` (in-memory, no `@Entity`) with:
  - `actualBalance`, `availableBalance`, `status`
  - `apply(FundsReservedEvent)`, `apply(TransferCompletedEvent)`, etc.
- [ ] Create `EventStore` service: `load(aggregateId)`, `append(aggregate, newEvents)`
- [ ] Rewrite `TransferCommandHandler` to use `EventStore` instead of `AccountCommandService.findByIdForUpdate()`
- [ ] Rewrite `CreateAccountCommandHandler` to emit `AccountCreatedEvent`
- [ ] Wire `OutboxRecorder` inside `EventStore.append()` (already exists in common-service)

### Phase 3 — Read Side (Projectors)
- [ ] Create `AccountViewRepository` (JPA on `account_view`)
- [ ] Create `TransactionHistoryViewRepository` (JPA on `transaction_history_view`)
- [ ] Create `AccountProjector` — listens to domain events, updates `account_view`
- [ ] Create `TransactionHistoryProjector` — updates `transaction_history_view`
- [ ] Rewrite `GetAccountBalanceQueryHandler` to query `account_view`
- [ ] Rewrite `GetTransferHistoryQueryHandler` to query `transaction_history_view`

### Phase 4 — DTO & API
- [ ] Add `availableBalance` field to `AccountBalanceResponseDTO`
- [ ] Add `balanceAfter` field to `TransferResponseDTO`
- [ ] Remove `AccountCommandService` (now replaced by `EventStore`)
- [ ] Remove `TransactionCommandService` (idempotency now handled by common `IdempotencyService`)

### Phase 5 — Snapshotting
- [ ] Add `SnapshotPolicy` (trigger snapshot every 50 events)
- [ ] Implement `SnapshotService.takeSnapshot(AccountAggregate)`

---

## 7. What Does NOT Change
- API surface (`/accounts`, `/transfers`, `/customers`, `/cards`) — same REST contract
- Security (Keycloak / OAuth2 Resource Server)
- Outbox infrastructure (`OutboxEvent`, `OutboxRecorder`, `OutboxDispatcher`)
- Idempotency infrastructure (`IdempotencyRecord`, `IdempotencyService`)
- Observability (Prometheus, OTLP tracing, Loki)
- Flyway baseline (V1–V4 migrations untouched)

---

## 8. Trade-offs Accepted

| Trade-off | Mitigation |
|---|---|
| **Eventual consistency** between Write DB and Read DB | Projector runs in-process (synchronous event bus first), sub-ms delay |
| **Complexity of Replay** for old accounts | Snapshotting every 50 events keeps replay overhead negligible |
| **Event schema evolution** | `schema_version` column in `domain_events`; upcasters per version |
| **At-least-once delivery** from Outbox | Read-side projectors are idempotent via `last_event_version` guard |
