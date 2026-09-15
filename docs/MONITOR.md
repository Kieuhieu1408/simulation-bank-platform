# 📊 System Monitoring & Infrastructure Info

Dưới đây là danh sách các URL, tài khoản và mật khẩu của các hệ thống Monitor, Database và Infrastructure (được cấu hình trong `infra/docker-compose.yml`).

## 1. 📈 Monitoring & Observability (Grafana Stack)

| Hệ thống | Chức năng | URL | Username | Password | Ghi chú |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Grafana** | Xem Dashboard, Logs, Traces | [http://localhost:3000](http://localhost:3000) | `admin` | `admin` | Được kết nối sẵn với Prometheus, Loki và Tempo. |
| **Prometheus** | Metric server / Scraper | [http://localhost:9090](http://localhost:9090) | - | - | Xem các metrics raw, targets có đang UP không. |
| **Loki** | Log aggregation server | [http://localhost:3100](http://localhost:3100) | - | - | Backend lưu trữ log. Dùng Grafana (Explore -> Loki) để xem log. |
| **Tempo** | Distributed tracing backend | `http://localhost:3200` | - | - | Lưu trữ TraceId. Dùng Grafana (Explore -> Tempo) để search trace. |

---

## 2. 🗄️ Databases & Cache

| Database | Port | Username | Password | Database / DB Name |
| :--- | :--- | :--- | :--- | :--- |
| **Oracle 23c (Free)** | `1521` | `corebank` | `corebank_demo` | Dùng cho service `corebank` |
| **PostgreSQL** | `5432` | `postgres` | `postgres` | Dùng cho Keycloak, Profile, Paygate |
| **MongoDB** | `27017` | `admin` | `admin` | Dùng cho `money-bank` |
| **Redis** | `6379` | - | - | Shared Cache chung |

---

## 3. 🔐 Identity & Secrets Management

| Hệ thống | URL | Username | Password | Ghi chú |
| :--- | :--- | :--- | :--- | :--- |
| **Keycloak** | [http://localhost:8080](http://localhost:8080) | `admin` | `admin` | Quản lý IAM, Auth/Token cho toàn hệ thống |
| **Vault** | [http://localhost:8200](http://localhost:8200) | - | `root_token` (Token ID) | Quản lý Secret. Tự động init dữ liệu khi chạy |

---

## 4. 🚀 Microservices (Application Ports)

Các port dưới đây được cấu hình trực tiếp trong các file `application.yml` / `application.yaml` của từng service tương ứng:

| Service | Port | Base URL | Cấu hình tại |
| :--- | :--- | :--- | :--- |
| **corebank** | `8180` | `http://localhost:8180` | `corebank/src/main/resources/application.yaml` |
| **money-bank** | `8081` | `http://localhost:8081` | `money-bank/src/main/resources/application.yml` |

*(Lưu ý: Các service khác như `api-gateway`, `profile-service`, `cms`,... hiện đang trống hoặc chưa được cấu hình port).*

---

## 5. 🌐 Cách xem Logs (Application)

Ứng dụng của bạn (`corebank`) được thiết lập đẩy log ra **Console** với định dạng **JSON** để `Promtail` có thể quét và gửi lên `Loki`. Bạn có 2 cách để xem log:

1. **Xem trực tiếp trên Terminal:**
   Các log sẽ hiện ngay trên màn hình chạy code của Spring Boot (nơi bạn vừa gọi lệnh `java ...`).
2. **Xem tập trung trên Grafana (Khuyên dùng):**
   - Truy cập **Grafana**: [http://localhost:3000](http://localhost:3000)
   - Đăng nhập với: `admin` / `admin`
   - Vào menu **Dashboards** > **Simulation Bank**: Bạn sẽ thấy một Dashboard tên là **Simulation Bank Platform (Spring Boot & Resilience4j)** đã được Provisioning sẵn. Nó chứa đầy đủ metrics về HTTP Requests, JVM Memory, CPU và biểu đồ trạng thái Đóng/Mở của Resilience4j Circuit Breaker.
   - Để xem Logs: Vào menu bên trái chọn **Explore** (biểu tượng la bàn).
   - Ở thanh dropdown chọn data source là **Loki**.
   - Gõ truy vấn LogQL để xem log, ví dụ:
     ```logql
     {service="corebank"}
     ```
     Hoặc nếu chạy qua docker thì có thể filter theo container:
     ```logql
     {container="corebank-container-name"}
     ```
3. **Trace API Request:**
   Khi có lỗi API, bạn có thể xem trường `traceId` trong log dạng JSON. Dùng ID đó dán vào phần **Explore -> Tempo** trên Grafana để xem hành trình request (nếu API có call qua lại nhiều service).