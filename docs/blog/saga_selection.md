# Lựa chọn giải pháp Saga: Kết hợp Event Sourcing và Bài toán Orchestration

Trong lộ trình nâng cấp lên Corebank V3, chúng ta đã thống nhất giữ lại **Event Sourcing** làm trái tim của hệ thống lưu trữ (để giải quyết triệt để bài toán Audit và Concurrency). Tuy nhiên, để xử lý các giao dịch phân tán (chuyển tiền giữa 2 tài khoản), việc lựa chọn một giải pháp **Saga** phù hợp là vô cùng quan trọng.

Bài viết này sẽ phân tích các giải pháp triển khai Saga, bao gồm cách tiếp cận của các gã khổng lồ công nghệ (như Grab, Uber) và các thư viện mã nguồn mở phổ biến.

## 1. Mối quan hệ giữa Event Sourcing và Saga
Trong mô hình của chúng ta, Saga và Event Sourcing phối hợp với nhau cực kỳ mượt mà:
*   **Event Sourcing** lo việc cập nhật và lưu trữ lịch sử của từng Aggregate riêng lẻ (Account A, Account B) bằng chuỗi Event bất biến.
*   **Saga Orchestrator** đóng vai trò là "người điều phối", lắng nghe các Event này (qua Outbox Pattern/Message Queue) và bắn ra các Command tiếp theo để hoàn thành hoặc Rollback luồng nghiệp vụ.

## 2. Giải pháp Saga của Grab (và các Big Tech Ride-hailing)
Khi nhắc đến "thư viện Saga của Grab", thực tế Grab không open-source một thư viện Saga cụ thể bằng Java nào cho cộng đồng. Tuy nhiên, qua các bài blog kỹ thuật của Grab và Uber (đối tác/người tiền nhiệm của Grab tại Đông Nam Á), cách họ giải quyết distributed transaction xoay quanh hai hướng chính:

1.  **Sử dụng Workflow/Orchestration Engine (Cadence / Temporal):** 
    *   Được sinh ra từ Uber và được sử dụng rộng rãi bởi các công ty công nghệ lớn (bao gồm cả các team trong Grab). Nó coi Saga là một quy trình làm việc (workflow) có trạng thái (stateful) được lưu trên Database của hệ thống Temporal.
2.  **Custom State Machine trên nền tảng Kafka (Choreography / Lightweight Orchestrator):**
    *   Grab thiết kế các State Machine cực kỳ tối ưu bằng Golang. Luồng đi sẽ là: `DB Outbox -> Kafka -> Kafka Consumer (State Machine) -> DB (cập nhật trạng thái Saga) -> Call Service khác`.
    *   Họ tự quản lý retry, idempotency (tính lũy đẳng) dựa trên Redis hoặc DB lock.

## 3. So sánh các thư viện/Framework Saga cho dự án Java (Spring Boot)

Để xây dựng hệ thống chuyển tiền cho Corebank, chúng ta có các lựa chọn sau:

### Lựa chọn 1: Cadence / Temporal (Cách tiếp cận của Big Tech)
*   **Bản chất:** Là một nền tảng Workflow Engine độc lập.
*   **Ưu điểm:**
    *   Quản lý lỗi, retry, timeout cực kỳ mạnh mẽ (Built-in).
    *   Visual Dashboard tuyệt vời để xem một giao dịch chuyển tiền đang kẹt ở bước nào.
    *   Polyglot (Hỗ trợ Java, Go, PHP...).
*   **Nhược điểm:**
    *   Độ phức tạp hạ tầng cực cao (Phải setup riêng một cụm Server Temporal, Cassandra/Postgres, Elasticsearch).
    *   Learning curve dốc, đổi hoàn toàn tư duy viết code thành Workflow logic.
    *   Hơi "overkill" (dùng dao mổ trâu giết gà) nếu dự án chỉ có vài luồng Saga.

### Lựa chọn 2: Axon Framework
*   **Bản chất:** Framework All-in-One của Java dành riêng cho DDD, CQRS, Event Sourcing và Saga.
*   **Ưu điểm:**
    *   Tích hợp Native vào Spring Boot.
    *   Cung cấp sẵn annotation `@Saga`, `@SagaEventHandler`, tự động quản lý vòng đời và lưu trạng thái Saga vào Database.
    *   Giải quyết trọn gói cả Event Sourcing (có sẵn Event Store) và Saga.
*   **Nhược điểm:**
    *   Bị "lock-in" (khóa chặt) vào hệ sinh thái của Axon. Nếu sau này muốn bỏ Axon, bạn phải đập đi viết lại toàn bộ kiến trúc.

### Lựa chọn 3: Eventuate Tram Saga (của Chris Richardson)
*   **Bản chất:** Thư viện Java chuyên biệt để implement Saga và Outbox pattern.
*   **Ưu điểm:**
    *   Chỉ tập trung vào Saga, không ép bạn phải dùng framework DDD nào cả.
    *   Cung cấp sẵn DSL (Domain Specific Language) cực kỳ dễ đọc để định nghĩa các bước Saga (Step 1: Invoke -> OnReply -> WithCompensation).
    *   Dùng chính Database quan hệ của bạn để lưu trạng thái Saga.
*   **Nhược điểm:**
    *   Tài liệu đôi khi hơi khó hiểu, cộng đồng hỗ trợ nhỏ hơn Spring/Axon.

### Lựa chọn 4: Tự xây dựng Lightweight Saga Orchestrator (Custom Outbox + Kafka)
(Đây là cách tiếp cận phổ biến nhất mà các team ở Grab/Shopee thường dùng khi muốn kiểm soát hoàn toàn hệ thống)
*   **Bản chất:** Dùng bảng `outbox_events` (Corebank V2 đang có sẵn) đẩy message lên Kafka. Viết một `@Service` tên là `TransferSagaOrchestrator` lưu trạng thái vào bảng `saga_instances`.
*   **Ưu điểm:**
    *   Kiểm soát 100% code, không phụ thuộc thư viện ngoài.
    *   Dễ dàng debug.
*   **Nhược điểm:**
    *   Phải tự code các cơ chế khó như: Timeout (Nếu account B không phản hồi sau 30s thì làm gì?), Idempotency, Retry logic.

## 4. Quyết định cho Corebank V3

Dựa trên bối cảnh dự án (Spring Boot, đã có sẵn Event Sourcing và bảng Outbox tự chế):

**Khuyến nghị:**
Chúng ta không nên ôm một nền tảng quá nặng như **Temporal/Cadence** hay bị lock-in bởi **Axon** ở giai đoạn này. Thay vào đó, chúng ta có 2 hướng khả thi:

1.  **Dùng Eventuate Tram Saga:** Nếu muốn tiết kiệm thời gian code hạ tầng (Timeout, Compensation routing), thư viện này định nghĩa luồng Saga cực kỳ đẹp và thuần Java.
2.  **Custom Lightweight Orchestrator:** Tiếp tục phát huy `OutboxRecorder` của V2. Xây dựng một bảng `transfer_sagas` (gồm SagaId, Trạng thái hiện tại). Luồng Orchestrator sẽ lắng nghe Kafka/RabbitMQ và điều phối thủ công. Đây là cách luyện tay nghề tốt nhất để thực sự hiểu bản chất của Saga mà các kỹ sư Big Tech đang làm.

**Sự kết hợp mục tiêu (DDD + Event Sourcing + Custom Saga):**
Aggregate `Account` sinh ra event `FundsReservedEvent`. Orchestrator bắt event này, tạo Saga bản ghi `PENDING`, rồi ra lệnh `CreditCommand` cho tài khoản kia. Nếu thất bại, Orchestrator phát lệnh `FailTransferCommand` về lại tài khoản gốc.