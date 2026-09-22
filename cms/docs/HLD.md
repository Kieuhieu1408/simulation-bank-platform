# TÀI LIỆU THIẾT KẾ KIẾN TRÚC TỔNG THỂ (HLD - High-Level Design)
**Dự án:** Simulation Bank Platform - CMS Portal

## 1. GIỚI THIỆU
Tài liệu HLD (High-Level Design) cung cấp góc nhìn tổng thể về kiến trúc hệ thống của phân hệ CMS Portal. Tài liệu định nghĩa các thành phần chính, luồng tương tác giữa Frontend - Backend - Central IAM (Keycloak), và các tiêu chuẩn kỹ thuật (đặc biệt là các bài học/tiêu chuẩn Frontend được kế thừa từ các dự án trước đó).

---

## 2. BỨC TRANH KIẾN TRÚC TỔNG THỂ (SYSTEM ARCHITECTURE)

Hệ thống tuân thủ kiến trúc Microservices / Modular Monolith phân tán, bao gồm các thành phần:

1. **Frontend (Angular CMS Web):** Đóng vai trò là **OAuth2 Public Client**. Giao tiếp với người dùng và gọi API xuống Backend.
2. **Central IAM (Keycloak):** Đóng vai trò là **Authorization Server** dùng chung cho toàn bộ hệ sinh thái Simulation Bank. Chịu trách nhiệm cấp phát `Access Token` (JWT), `Refresh Token` và xác thực người dùng (SSO).
3. **CMS Backend (Spring Boot 4 - Java 25):** Đóng vai trò là **OAuth2 Resource Server**. Nơi xử lý logic nghiệp vụ, phân quyền API (Authorization) và che giấu dữ liệu (Data Masking).
4. **Cross-Cutting Concerns:**
   * **Logging & Tracing:** OpenTelemetry / ELK Stack.
   * **Message Broker:** Kafka / RabbitMQ (Đồng bộ dữ liệu sang Core hoặc các Service khác).
   * **Object Storage:** MinIO / AWS S3 (Lưu trữ tài liệu đính kèm như ảnh CCCD).

---

## 3. LUỒNG XÁC THỰC VÀ PHÂN QUYỀN (AUTHEN & AUTHOR)

### 3.1. Xác thực (Authentication) với Keycloak
Hệ thống sử dụng luồng **Authorization Code Flow with PKCE** (chuẩn bảo mật cao nhất cho SPA):
1. User truy cập Angular CMS chưa có Token -> Angular chuyển hướng (Redirect) sang trang Login của Keycloak.
2. User đăng nhập thành công, Keycloak trả về `authorization_code`.
3. Angular dùng `code` đổi lấy `Access Token` (JWT) và lưu trữ an toàn (khuyến nghị In-memory hoặc HttpOnly Cookie / Secure LocalStorage).
4. Angular đính kèm Token này vào HTTP Header (`Authorization: Bearer <token>`) trong các API requests.

### 3.2. Phân quyền (Authorization) với `@CmsAuthorization`
* **Tại Keycloak:** Trả về Roles chung của hệ thống (VD: `CMS_ADMIN`, `CMS_GDV`).
* **Tại Backend (Spring Boot):** Xây dựng Custom Annotation `@CmsAuthorization(menuCode, action)`. 
  * Spring Security AOP (Aspect-Oriented Programming) sẽ chặn Request.
  * Đọc Role từ JWT, query vào Database (`cms_role_permission`) xem Role này có quyền `action` (VD: VIEW, CREATE) trên `menuCode` (VD: user_info) hay không.
  * Pass -> Cho phép chạy Logic. Fail -> Trả về `403 Forbidden`.

---

## 4. KIẾN TRÚC FRONTEND (ANGULAR) VÀ TIÊU CHUẨN KẾ THỪA
Mặc dù sử dụng Angular (Framework đã có sẵn nhiều design pattern chuẩn), chúng ta vẫn áp dụng và quy chuẩn hóa các tiêu chuẩn (Standards) được đúc kết từ dự án React trước đó (`paperless-fe`), nhằm đảm bảo chất lượng enterprise:

### 4.1. HTTP Interceptor (Kế thừa từ `api-client.ts`)
Thay vì dùng Axios Interceptor như React, Angular sử dụng `HttpInterceptor`. Quy chuẩn bắt buộc đối với mọi Request/Response:
* **Inject Authorization:** Tự động đính kèm `Bearer Token`.
* **Inject Tracing Headers:** Tự động sinh hoặc lấy `X-Request-ID` / `X-Correlation-ID` gắn vào header để Backend có thể trace log.
* **Connection Status Tracking:** Bắt lỗi Network/Timeout để cập nhật trạng thái kết nối (Online/Offline/Slow) hiển thị lên Global UI.

### 4.2. Smart Service Wrappers (Kế thừa từ `use-smart-query.ts`, `use-smart-mutation.ts`)
* React dùng `@tanstack/react-query` để bọc các API. Trong Angular, chúng ta sẽ bọc `HttpClient` (RxJS) thành các Base Service.
* **Tiêu chuẩn "Smart Wrapper":**
  * Tự động hiển thị Global Toast Notification khi lỗi (hoặc cho phép tắt qua flag `showErrorToast`).
  * Có cơ chế tự động thử lại (Auto Retry) đối với các lỗi mạng hoặc lỗi `5xx`.
  * Chuẩn hóa Error Context: Phân loại lỗi (Network Error, 401 Auth Error, 403 Permission Error, 400 Business Validation) để đẩy về Error Handler chung.

### 4.3. Navigation & Route Guards (Kế thừa từ `navigation-permissions.ts`)
* Sử dụng `CanActivate` Guard của Angular.
* Logic Guard phải đọc cấu hình quyền (Permissions map) được load lúc khởi tạo app để quyết định User có được vào URL/Tab đó hay không, tránh việc FE tự render component mà User không có quyền, sau đó API mới văng 403.

### 4.4. Centralized Error Handling (Kế thừa từ `error-handler.ts`, `error-store.ts`)
* Sử dụng `ErrorHandler` của Angular (Implement `ErrorHandler` interface) để "hứng" toàn bộ lỗi chưa được handle.
* Phân loại lỗi (Error Classifier) và đẩy vào Global Store (NgRx hoặc BehaviorSubject) để UI phản ứng (VD: Bật popup "Phiên làm việc hết hạn" khi gặp 401).

---

## 5. KIẾN TRÚC BACKEND (SPRING BOOT)

### 5.1. Cấu trúc Layer (Clean Architecture / N-Tier)
1. **Controllers (API Layer):** Nơi gắn `@CmsAuthorization`, hứng request, validate DTO (Data Transfer Object).
2. **Services (Business Layer):** Chứa logic nghiệp vụ (ví dụ: tạo Proposal, che giấu dữ liệu PII - Masking).
3. **Repositories (Data Access Layer):** JPA / Hibernate tương tác với Database.
4. **Clients / Integration Layer:** Giao tiếp với Keycloak API, gửi message vào Kafka, hoặc gọi REST API sang `customer-service`.

### 5.2. Che giấu dữ liệu (Data Masking)
Logic Masking (che CIF, SĐT, CCCD) **bắt buộc phải thực hiện ở Backend Layer** (thông qua DTO Mapper hoặc Jackson Serializer) trước khi gửi qua mạng. Frontend chỉ việc hiển thị chuỗi đã bị masked (VD: `*******123`).

### 5.3. Observability (MDC Logging)
Mọi request vào Backend sẽ được Filter/Interceptor tóm lấy `X-Request-ID` từ Header và gán vào `MDC` (Mapped Diagnostic Context). Cấu hình Logback sẽ tự động in `trace_id` này ra mọi dòng log `[INFO]`, `[ERROR]`, giúp tra cứu xuyên suốt hệ thống (Distributed Tracing).

---
*Tài liệu này xác định bộ khung kiến trúc chuẩn, các API và Data Model chi tiết sẽ được làm rõ trong tài liệu SRD (System Requirements Document).*