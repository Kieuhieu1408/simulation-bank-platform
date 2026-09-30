# Sự kết hợp hoàn hảo: DDD + CA + Event Sourcing + CQRS + AOP & Design Patterns

Trong Corebank V3, chúng ta không chỉ áp dụng đơn lẻ một khuôn mẫu nào, mà là sự hội tụ của nhiều triết lý thiết kế hiện đại để giải quyết bài toán phức tạp của hệ thống ngân hàng.

## 1. Domain-Driven Design (DDD)
DDD giúp chúng ta tập trung vào nghiệp vụ cốt lõi:
*   **Ubiquitous Language:** Giao tiếp chung giữa Dev và Domain Expert.
*   **Bounded Context:** Chia nhỏ hệ thống thành các domain (Customer, Account, Transfer).
*   **Aggregates:** Trái tim của hệ thống (vd: `AccountAggregate`). Aggregate là một Transactional Boundary bảo vệ các quy tắc nghiệp vụ bất biến. Khác với V1, Aggregate ở V3 sẽ là Pure Java, độc lập với Database.

## 2. Clean Architecture (CA)
Đóng vai trò là cái khung (scaffolding) để bảo vệ DDD:
*   Đảm bảo DDD Aggregates nằm ở lõi (Entities Layer).
*   Đảm bảo Application Services (Use Cases) không bị rò rỉ Infrastructure logic.
*   Sử dụng **Ports & Adapters** để kết nối DDD với Database (Event Store) và các API (REST Controllers).

## 3. CQRS (Command Query Responsibility Segregation)
Do tính chất của ngân hàng, việc đọc và ghi thường mất cân xứng và có logic khác hẳn nhau.
*   **Write Side (Command):** Xử lý nghiệp vụ phức tạp, bảo vệ Invariants thông qua DDD Aggregates.
*   **Read Side (Query):** Tối ưu hóa cho việc hiển thị (Projections). Trả về các View model trực tiếp thay vì bóc tách Aggregate.

## 4. Event Sourcing (ES)
Sự kết hợp hoàn hảo cho Write Side của hệ thống tài chính:
*   Trạng thái hiện tại của `AccountAggregate` không lưu trực tiếp vào database, mà được tính toán lại bằng cách Replay chuỗi các `DomainEvent` (AccountCreated, FundsReserved, TransferCompleted).
*   Mang lại Audit Trail tuyệt đối - không thể chối cãi.
*   Khắc phục triệt để vấn đề Concurrency và Deadlock (Optimistic Locking trên Event Store).

## 5. Aspect-Oriented Programming (AOP)
Xử lý các Cross-cutting concerns (những mối quan tâm cắt ngang) mà không làm rác logic chính:
*   **Security/Authorization:** `@CoreBankAuthorization` sử dụng AOP để kiểm tra quyền hạn trước khi vào Controller, giữ Controller sạch sẽ.
*   **Logging & Tracing:** Ghi log các Command/Query một cách tập trung.

## 6. Design Patterns nổi bật
*   **Mediator Pattern:** Áp dụng cho CQRS thông qua `BaseController.execute()` để tách biệt Controller khỏi Handler.
*   **Factory Pattern:** Khởi tạo các Aggregate phức tạp (`AccountAggregate.create()`).
*   **Outbox Pattern:** Đảm bảo tính nhất quán (At-least-once delivery) khi publish event ra hệ thống Message Broker, được ghi cùng Transaction với Event Store.

**Kết luận:** Sự kết hợp này mang lại một hệ thống Corebank V3: **Auditable (ES) - Scalable (CQRS) - Maintainable (CA) - Business-focused (DDD).**