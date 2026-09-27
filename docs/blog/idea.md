# The Genesis: Building a Core Banking System

## The Initial Idea
When we set out to build the `simulation-bank-platform`, our core objective was to create a miniature Core Banking system capable of solving the most daunting challenges in the financial tech industry: **Data Integrity** and **Concurrency**.

### Crucial Requirements:
1. **Zero Data Loss:** Customer money is the most sensitive data. The system must not tolerate any calculation errors or lost transactions.
2. **Prevent Double-Spending:** Strictly prevent a scenario where a user with $1,000 successfully executes two simultaneous $1,000 transfers.
3. **Auditability & Traceability:** Every change to the cash flow must be clearly logged to serve future reconciliation processes.
4. **High Throughput:** The system must handle thousands of transactions per second (high TPS) without collapsing under pressure.

### Initial Architectural Choice
To achieve these goals, we decided to break the system down into Microservices (Corebank, API Gateway, Profile Service, Notification, etc.).

Specifically, for the `corebank` service, we decided from the very beginning to apply **CQRS (Command Query Responsibility Segregation)**—separating Read and Write responsibilities—hoping to optimize the transaction flow.

From this idea, Version 1 (V1) of the system was born. Everything worked fine in the test environment, but as we dug deeper into the architecture and simulated real-world pressure, the "pain points" began to surface. (Continue to `v1.md`)