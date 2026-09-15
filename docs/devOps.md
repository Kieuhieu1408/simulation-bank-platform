# DevOps & Observability Guidelines

Tài liệu này định nghĩa kiến trúc vận hành, giám sát (Observability) và các tiêu chuẩn triển khai dùng chung cho toàn bộ các dịch vụ (services) trong nền tảng Simulation Bank Platform. Tài liệu này giúp các kỹ sư phát triển (Dev), vận hành (Ops) hiểu rõ cách hệ thống hoạt động, cách ghi log chuẩn, và cách tìm nguyên nhân (troubleshooting) khi có lỗi.

## 1. Tổng quan Kiến trúc Observability (Khả năng quan sát)

Hệ thống sử dụng **Three Pillars of Observability** (3 trụ cột giám sát) kết hợp với hệ sinh thái mã nguồn mở (Prometheus, Grafana Stack) được triển khai trên Kubernetes (K8s) hoặc Docker.

### 1.1. Các thành phần chính

1. **Metrics (Chỉ số đo lường): Prometheus**
   - **Vai trò:** Lưu trữ chuỗi thời gian (time-series) định lượng như: mức sử dụng CPU/RAM, số lượng HTTP request mỗi giây, độ trễ p95/p99 của API, tỷ lệ lỗi.
   - **Cơ chế:** Prometheus sử dụng mô hình **Pull**. Nó sẽ định kỳ quét (scrape) endpoint `/actuator/prometheus` của các Spring Boot Pods.
   - **Dev cần biết:** Dùng thư viện `Micrometer` để tự động expose metrics. Không dùng ID khách hàng hoặc số tài khoản làm *Label/Tag* cho metric để tránh làm bùng nổ dữ liệu (High Cardinality).

2. **Logs (Nhật ký): Loki & Promtail / Fluent Bit**
   - **Vai trò:** Quản lý log tập trung. Thay vì phải chui vào từng Pod để xem file, tất cả log được đẩy về một chỗ.
   - **Cơ chế:** Các ứng dụng Spring Boot in log ra màn hình Console dưới định dạng JSON (`logstash-logback-encoder`). Một agent (Promtail) chạy trong K8s sẽ đọc log console này và đẩy về Loki.
   - **Dev cần biết:** KHÔNG ghi log ra file `.log` trên ổ cứng container. KHÔNG ghi log kiểu plain text (`System.out.println`). Phải sử dụng Slf4j `log.info()`, `log.error()`.

3. **Traces (Truy vết phân tán): Tempo / Jaeger**
   - **Vai trò:** Theo dõi vòng đời của 1 request khi nó đi xuyên qua nhiều microservices (VD: API Gateway -> Money Bank -> Corebank).
   - **Cơ chế:** OpenTelemetry / Micrometer Tracing sẽ tiêm `traceId` vào HTTP Headers.
   - **Dev cần biết:** Luôn truyền header khi gọi API sang service khác (Spring WebClient/RestTemplate đã tự động làm việc này nếu cấu hình đúng).

4. **Visualization & Alert: Grafana**
   - Nơi tập trung toàn bộ giao diện: Vẽ biểu đồ metric, màn hình query log (LogQL) và trace. Thiết lập cảnh báo (Alert) bắn về Slack/Email/Webhook khi tỷ lệ lỗi tăng cao.

---

## 2. Tiêu chuẩn Ghi Log dành cho Developer

Để có thể tìm bug hiệu quả trên Grafana, log phải được ghi có cấu trúc (Structured Logging) và chứa các ID ngữ cảnh (Context IDs).

### 2.1. Cấu hình MDC (Mapped Diagnostic Context)
Trong `logback-spring.xml` của mọi service, encoder JSON bắt buộc cấu hình các MDC keys sau:
- `traceId`: Sinh ra bởi OpenTelemetry.
- `spanId`: ID của từng chặng trong trace.
- `correlationId`: ID xuyên suốt nghiệp vụ (nếu traceId bị mất giữa các hệ thống legacy).
- `workflowId`: Nhóm các bước của một quy trình dài (vd: Approval Proposal).
- `transactionId`: Định danh giao dịch tài chính.
- `proposalId`: Định danh yêu cầu tạo/sửa/đóng.
- `errorCode`: Mã lỗi nghiệp vụ chuẩn (vd: `MB-CMN-500-001`).

### 2.2. Do's and Don'ts khi ghi log

✅ **DO (NÊN LÀM):**
- Ghi log trạng thái bắt đầu và kết thúc của các thay đổi quan trọng: `log.info("Started transfer processing")`.
- Truyền đúng biến vào context: `log.warn("Validation failed for account: {}", maskedAccountId)`.
- Log Exception luôn kèm theo StackTrace ở log level ERROR: `log.error("Failed to connect to Corebank", ex)`.
- Xóa/mask thông tin nhạy cảm (PII/PCI) trước khi log: Số thẻ, số tài khoản đầy đủ, mật khẩu, OTP, Token.

❌ **DON'T (KHÔNG ĐƯỢC LÀM):**
- Không in log toàn bộ HTTP Request / Response payload (cực kỳ tốn ổ cứng và lộ lọt dữ liệu khách hàng).
- Không lạm dụng `log.debug()` / `log.info()` trong vòng lặp lớn (vd: duyệt 10,000 bản ghi).

---

## 3. Hướng dẫn Troubleshooting (Tìm Bug qua Grafana)

Khi hệ thống có cảnh báo hoặc người dùng báo lỗi, Dev/Ops thực hiện các bước sau:

**Bước 1: Nhận diện lỗi qua Metrics (Grafana Dashboard)**
- Nhìn vào biểu đồ RED (Rate - Errors - Duration).
- Phát hiện có lỗi 5xx tăng vọt ở endpoint `POST /api/v1/transfers`.

**Bước 2: Tìm Trace ID / Correlation ID qua Logs (Loki / Explore)**
- Mở Grafana Explore, chọn source là Loki.
- Chạy câu LogQL: 
  ```logql
  {app="money-bank", level="ERROR"} |= "MB-CMN-500-001"
  ```
- Kết quả sẽ hiển thị các dòng log lỗi. Lấy `traceId` từ một dòng log bất kỳ.

**Bước 3: Truy vết Request bằng Trace (Tempo)**
- Mở màn hình Traces, nhập `traceId` vừa lấy.
- Bạn sẽ thấy sơ đồ cây. Request từ Mobile -> Gateway (5ms) -> Money Bank (2000ms - Lỗi) -> Corebank (Timeout).
- Kết luận: Corebank bị timeout dẫn đến Money Bank trả về lỗi 500.

---

## 4. Tiêu chuẩn Triển khai (Deployment)

### 4.1. Containerization (Docker)
- Mỗi service phải có `Dockerfile` đa bước (Multi-stage build) tối ưu kích thước.
- Chạy container với một user không có quyền root (`USER spring`).
- Expose port `8080` cho ứng dụng và `8081` (hoặc `/actuator`) cho management port.

### 4.2. Kubernetes (K8s) Configs
Mọi service phải thỏa mãn các cấu hình tối thiểu sau khi viết Helm Chart / K8s Manifest:
- **Probes:**
  - `Liveness Probe`: Trỏ vào `/actuator/health/liveness` (Để K8s khởi động lại Pod nếu app bị treo).
  - `Readiness Probe`: Trỏ vào `/actuator/health/readiness` (Để K8s chỉ gửi traffic khi ứng dụng thực sự sẵn sàng nhận request).
- **Resources Limit:** Bắt buộc định nghĩa `requests` và `limits` cho CPU và Memory (ngăn chặn OOMKilled lan truyền).
- **Graceful Shutdown:** Spring Boot cần cấu hình `server.shutdown=graceful` để không làm đứt kết nối khách hàng khi Pod bị scale down hoặc redeploy.

### 4.3. CI/CD (Gợi ý)
- **CI (Continuous Integration):** Push code -> Chạy Unit Test -> Chạy ArchUnit -> Build Docker Image -> Scan lỗi bảo mật Image -> Push lên Container Registry.
- **CD (Continuous Deployment):** Cập nhật Tag image trong GitOps repo (ArgoCD / Flux) -> K8s tự động kéo image mới về và thay thế Pod cũ theo chiến lược Rolling Update (Zero Downtime).

---

## 5. Quyết định Kiến trúc Mở rộng (Architecture Notes)

### 5.1. Quản lý File và Trạng thái Cục bộ (Local State)

**Vấn đề:** Các Pod trong K8s có tính chất "dễ bay hơi" (ephemeral). Nếu lưu file (ảnh KYC, CMND, chứng từ) hoặc session vào ổ cứng cục bộ Pod, dữ liệu sẽ mất hoặc không đồng bộ khi Pod restart.

**Quyết định:**
- **Stateless design:** Toàn bộ service thiết kế stateless — không lưu file local.
- **Media Service (tương lai):** Bổ sung `media-service` dùng AWS S3 / GCS / MinIO để upload, validate và phân phối file.
- **Session:** Dùng Redis hoặc JWT token — không dùng In-Memory session.

### 5.2. Thứ tự Sự kiện trong Kafka (Out-of-order Events)

**Vấn đề:** Khi nhiều Pod cùng consume một Kafka topic, các sự kiện của cùng một tài khoản (TẠO → KHOÁ) có thể bị xử lý sai thứ tự do tốc độ xử lý khác nhau giữa Pod.

**Quyết định:**
- **Partition Key bắt buộc:** Khi publish message lên Kafka, phải dùng `account_id` hoặc `cif_number` làm Partition Key.
- **Ordering Guarantee:** Kafka hash Partition Key → tất cả event của cùng tài khoản vào cùng Partition → một Pod xử lý tuần tự, đảm bảo ordering tuyệt đối cho từng tài khoản.
