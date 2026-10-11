# Hướng Dẫn Vận Hành Kubernetes Local Trên Docker Desktop

Tài liệu này cung cấp giải pháp đóng gói toàn diện để bạn hoặc **AI Agent** có thể tự động triển khai, kiểm thử gọi API và xóa sạch hệ thống Kubernetes của dự án **Simulation Bank Platform** trên máy local (Docker Desktop).

---

## 📌 1. Chuẩn Bị: Bật Kubernetes Trên Docker Desktop (Chỉ làm 1 lần)

Để Docker Desktop hỗ trợ Kubernetes:
1. Mở ứng dụng **Docker Desktop**.
2. Click vào biểu tượng **Cài đặt** (⚙️ *Settings* ở góc trên bên phải).
3. Chọn mục **Kubernetes** ở thanh menu bên trái.
4. Tích chọn vào ô checkbox **"Enable Kubernetes"**.
5. Nhấn nút **"Apply & restart"** và chờ khoảng 1-2 phút cho đến khi biểu tượng Kubernetes ở góc dưới cùng bên trái chuyển sang màu **xanh lá cây** (*Kubernetes running*).

---

## 🚀 2. Kịch Bản 1: Triển Khai K8s & Chạy Test Tự Động

### Câu lệnh chat với Agent:
> **"Bạn hãy mở file scripts/deploy-k8s.sh và thực hiện chạy k8s trên local"**

### File thực thi:
`scripts/deploy-k8s.sh` (hoặc lệnh terminal: `make deploy-k8s` / `bash scripts/deploy-k8s.sh`)

### Quá trình script tự động thực hiện:
1. **Pre-flight Check**: Kiểm tra Docker daemon và Kubernetes cluster trên Docker Desktop đã sẵn sàng.
2. **Khởi động Backing Services**: Khởi động Oracle DB, Keycloak SSO, Redis, Postgres, Kafka trên Docker Compose và chờ đến khi đạt trạng thái Healthy.
3. **Cấu hình SSO Keycloak**: Tự động tạo Realm `simulation-bank` và Client Credentials `moneybank-service` / `moneybank-secret`.
4. **Triển khai Kubernetes Workloads**:
   - Tạo namespace `sim-bank`.
   - Deploy Helm chart `k8s/apps` (bao gồm `api-gateway`, `corebank-command`, `corebank-query`, `money-bank`, `cms`, HPAs, ConfigMaps, Secrets, ExternalName services).
   - Chờ toàn bộ Pods đạt trạng thái **Ready**.
5. **Luồng Test Kiểm Tra Tự Động (Automated Verification Test Suite)**:
   - ✅ **Test 1**: Kiểm tra số lượng Pods đang ở trạng thái `Running`.
   - ✅ **Test 2**: Gọi Actuator Health Check của **API Gateway** (`/actuator/health` -> `UP`).
   - ✅ **Test 3**: Gọi Actuator Health Check của **Corebank Pod** (`/actuator/health` -> `UP`).
   - ✅ **Test 4**: Gửi request tới Keycloak lấy **OAuth2 JWT Access Token**.
   - ✅ **Test 5**: Gọi API nghiệp vụ qua API Gateway kèm JWT Token (`/api/v1/money/customers/...`) và xác nhận định tuyến thành công!
6. **Báo cáo kết quả**: Xuất bảng tổng kết với đầy đủ URLs truy cập.

---

## 🧹 3. Kịch Bản 2: Xoá Sạch Kubernetes (Không Còn Trên Tab K8s Docker Desktop)

### Câu lệnh chat với Agent:
> **"Bạn hãy mở file scripts/cleanup-k8s.sh và thực hiện xoá sạch kubernetes đó và không thấy trên tab kubernetes trên docker desktop"**

### File thực thi:
`scripts/cleanup-k8s.sh` (hoặc lệnh terminal: `make clean-k8s` / `bash scripts/cleanup-k8s.sh`)

### Quá trình script tự động thực hiện:
1. Gỡ bỏ Helm release `sim-bank-apps`.
2. Xóa toàn bộ Deployments, Pods, Services, HPAs, ConfigMaps, Secrets của dự án.
3. Xóa hoàn toàn Namespace `sim-bank` (xử lý triệt để finalizers nếu bị kẹt).
4. Xác minh không còn bất kỳ workload nào trong namespace của dự án.
5. **Kết quả**: Khi bạn mở tab **Kubernetes** trên **Docker Desktop**, danh sách Workloads/Pods sẽ hoàn toàn **trống trơn và sạch sẽ 100%**.

*(Ghi chú: Nếu muốn tắt luôn cả các container DB/Keycloak trong Docker Compose, chỉ cần thêm cờ `--all`: `bash scripts/cleanup-k8s.sh --all`)*

---

## 🌐 4. Danh Sách URLs Truy Cập Khi Đang Chạy

| Dịch vụ | URL | Ghi chú |
|---|---|---|
| **API Gateway** | `http://localhost:8080` | Cổng vào chính của hệ thống K8s |
| **API Gateway Health** | `http://localhost:8080/actuator/health` | Kiểm tra trạng thái Gateway |
| **Keycloak SSO** | `http://localhost:8090` | Quản lý Auth, Realm `simulation-bank` (admin/admin) |
| **Jaeger Tracing** | `http://localhost:16686` | Distributed Tracing UI |
| **Grafana Dashboard** | `http://localhost:3000` | Giám sát Metrics & Logs (admin/admin) |
| **Prometheus** | `http://localhost:9090` | Thu thập Metrics |
