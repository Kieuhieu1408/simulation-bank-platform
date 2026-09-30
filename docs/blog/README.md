# Nhật ký Kỹ thuật: Hành trình Xây dựng Simulation Bank Platform

Chào mừng bạn đến với chuỗi bài viết kỹ thuật (blog series) ghi chép lại hành trình xây dựng, đối mặt với khó khăn và tiến hóa kiến trúc của dự án Simulation Bank Platform (Corebank).

## Các Giai Đoạn Phát Triển

### 1. Khởi thủy & Những Vấn Đề Đầu Tiên
*   [The Genesis (Ý tưởng ban đầu)](idea.md) - Những yêu cầu cốt lõi về bảo toàn dữ liệu và concurrency.
*   [Phiên bản V1: Nỗi đau Concurrency](v1.md) - Thất bại với Pessimistic Lock, Deadlock và vòng lặp vô tận.
*   [Phiên bản V2: Giải cứu bằng Event Sourcing](v2.md) - Áp dụng CQRS và Event Sourcing để giải quyết triệt để bài toán chuyển tiền.

### 2. Định Hình Lại Kiến Trúc (Tiến tới V3)
Tuy V2 đã giải quyết được pain point kỹ thuật, hệ thống lại bị phân mảnh và vi phạm nhiều nguyên tắc thiết kế. Chúng ta cần định hình lại toàn bộ dự án:

*   [Clean Architecture trong Corebank: Giấc mơ và Thực tại](CA.md) - Phân tích những thiếu sót của V2 và định hướng áp dụng triệt để Clean Architecture.
*   [Sự kết hợp hoàn hảo: DDD + CA + Event Sourcing + CQRS + AOP & Design Patterns](architecture_patterns.md) - Bức tranh tổng thể về cách các pattern phối hợp với nhau trong lõi hệ thống.
*   [Corebank V3 Roadmap](v3.md) - Kế hoạch tái cấu trúc (Refactoring) hệ thống từ lai tạp trở thành chuẩn mực Clean Architecture.
*   [Lựa chọn giải pháp Saga: Kết hợp Event Sourcing và Bài toán Orchestration](saga_selection.md) - So sánh thư viện Saga của Grab (Temporal/Kafka) với các framework khác (Axon, Eventuate) để chốt kiến trúc cho V3.

### 3. Thiết kế & Spec (Tài liệu kỹ thuật)
*   [System Requirements Document (SRD) V2](srd_v2.md) - Tài liệu thiết kế hệ thống chi tiết cho V2.
*   [Tài liệu DevOps tổng thể](../devOps.md) - Quy trình CI/CD, Docker, Kubernetes.
*   [Tài liệu Monitoring](../MONITOR.md) - Theo dõi sức khỏe hệ thống với Prometheus, Grafana.
*   [Feature Roadmap](../FEATURE_ROADMAP.md) - Lộ trình các tính năng chung của hệ thống.

---
*Lưu ý: Các bài viết này không chỉ là tài liệu mô tả, mà còn là nhật ký lưu lại những sai lầm và quyết định mang tính lịch sử của đội ngũ kỹ sư.*