# Simulation Bank Platform - Documentation

Thư mục này chứa toàn bộ các tài liệu kỹ thuật, thiết kế hệ thống, nhật ký phát triển và lộ trình của dự án Simulation Bank Platform.

## 📖 Cấu trúc Tài liệu

1. **[Nhật ký Phát triển & Blog Kiến trúc (Blog Series)](blog/README.md)**
   Nơi lưu trữ lịch sử phát triển qua các phiên bản (V1, V2, V3) và sự tiến hóa của kiến trúc hệ thống:
   - Các Pain point đã gặp (Deadlock, Concurrency).
   - Thiết kế hệ thống (CA, DDD, CQRS, Event Sourcing).
   - Lộ trình tiến tới **Clean Architecture V3**.

2. **[Lộ trình Tính năng (Feature Roadmap)](FEATURE_ROADMAP.md)**
   Danh sách và trạng thái phát triển của các module, API, dịch vụ trong hệ thống.

3. **[Hạ tầng & Vận hành (DevOps)](devOps.md)**
   Hướng dẫn triển khai CI/CD, thiết lập môi trường bằng Docker/Kubernetes, và quản lý các công cụ phụ trợ (Kafka, Redis, Postgres).

4. **[Giám sát Hệ thống (Monitoring)](MONITOR.md)**
   Tài liệu về thiết lập và sử dụng Prometheus, Grafana, ELK/EFK stack để giám sát metrics, logs và distributed tracing cho toàn bộ microservices.

5. **Tài liệu cho các Dịch vụ Cụ thể:**
   - **`corebank/`**: Các tài liệu nội bộ, workflow cụ thể của service Corebank.
   - **`keycloak/`**: Hướng dẫn cài đặt và thiết lập bảo mật/Identity Management với Keycloak.
   - **`prometheus/`**: File cấu hình cho hệ thống Monitoring.

---
**Mục tiêu của Tài liệu:**
Giúp các kỹ sư (Dev, DevOps, Architect) nhanh chóng nắm bắt được tư duy thiết kế cốt lõi (Core Domain), hiểu được *tại sao* các quyết định công nghệ lại được đưa ra, và biết cách triển khai, bảo trì, giám sát hệ thống một cách hiệu quả nhất.