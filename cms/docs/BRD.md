# TÀI LIỆU YÊU CẦU NGHIỆP VỤ (BRD)
**Dự án:** Simulation Bank Platform - CMS Portal

## MỤC LỤC
1. GIỚI THIỆU
    1.1. Mục đích
    1.2. Phạm vi
    1.3. Giải thích thuật ngữ và các từ viết tắt
    1.4. Tài liệu tham khảo
2. TỔNG QUAN
    2.1. Phát biểu bài toán
    2.2. Bổ sung, chỉnh sửa chức năng
3. ĐẶC TẢ YÊU CẦU CHỨC NĂNG
4. ĐẶC TẢ YÊU CẦU PHI CHỨC NĂNG (Kiến trúc & Hệ thống)

---

## 1. GIỚI THIỆU
### 1.1. Mục đích
Tài liệu này đặc tả các yêu cầu nghiệp vụ, chức năng và phi chức năng cho phân hệ CMS Portal thuộc dự án Simulation Bank Platform. Tài liệu làm cơ sở để đội ngũ Architecture lựa chọn giải pháp, đội ngũ Development triển khai code, và đội ngũ QA/QC thực hiện kiểm thử.

### 1.2. Phạm vi
Hệ thống CMS phục vụ cho nội bộ ngân hàng, cụ thể:
*   Cung cấp công cụ tra cứu thông tin khách hàng cho Giao dịch viên (GDV) / CSKH.
*   Cung cấp tính năng quản lý, khởi tạo các yêu cầu thay đổi thông tin (SĐT, CCCD, CIF, Mật khẩu) với luồng phê duyệt (Maker-Checker).
*   Quản trị người dùng nội bộ, định nghĩa và phân quyền chi tiết (Role & Permission).

### 1.3. Giải thích thuật ngữ và các từ viết tắt
*   **CMS:** Cổng thông tin nội bộ (Content/Customer Management System).
*   **GDV:** Giao dịch viên (Đóng vai trò Maker - Người tạo yêu cầu).
*   **Trưởng đơn vị:** Cấp quản lý (Đóng vai trò Checker - Người phê duyệt).
*   **CIF (Customer Information File):** Mã hồ sơ thông tin khách hàng duy nhất.
*   **PII (Personally Identifiable Information):** Dữ liệu định danh cá nhân nhạy cảm cần bảo mật.

### 1.4. Tài liệu tham khảo
*   Tài liệu Kiến trúc tổng thể hệ thống Simulation Bank Platform.
*   Quy chuẩn thiết kế Logic Phân quyền Corebank.

---

## 2. TỔNG QUAN
### 2.1. Phát biểu bài toán
Hiện tại, dự án cần một cổng Web Portal bảo mật cao để các nhân viên nội bộ (GDV, CSKH) tra cứu và hỗ trợ khách hàng. Bài toán cốt lõi là phải kiểm soát được **"Ai được phép làm gì"** (Phân quyền động theo từng tab/action) và **"Ai đã làm gì"** (Lưu vết). Các thông tin nhạy cảm của khách hàng khi hiển thị trên màn hình phải được che giấu (masking) để phòng chống rủi ro nội bộ. Các nghiệp vụ thay đổi thông tin quan trọng bắt buộc phải thông qua luồng phê duyệt 1 cấp và lưu lại bằng chứng (document).

### 2.2. Bổ sung, chỉnh sửa chức năng
Đây là phân hệ được **phát triển mới hoàn toàn** trong giai đoạn này, làm nền tảng (Foundation) để tiếp tục mở rộng các tính năng tra cứu giao dịch, xác thực eKYC, tra soát lỗi trong các giai đoạn tiếp theo.

---

## 3. ĐẶC TẢ YÊU CẦU CHỨC NĂNG

### 3.1. Quản trị Hệ thống & Phân quyền (System Admin)
*   **Quản lý Role & Permission:** Cho phép Admin tạo mới, sửa, xóa các Nhóm quyền (Role).
*   **Gán quyền chi tiết:** Gán quyền truy cập chi tiết theo từng Trang/Menu và từng Hành động (Action: VIEW, CREATE, UPDATE, DELETE, APPROVE). 
*   *Ghi chú UI:* Giao diện FE sẽ dựa vào cấu hình quyền này để tự động render (ẩn/hiện) các tab, menu và nút bấm tương ứng với user đang đăng nhập.

### 3.2. Tra cứu thông tin khách hàng
*   **Tìm kiếm:** Cho phép GDV tra cứu thông tin khách hàng bằng số **CCCD**.
*   **Data Masking (Che dấu dữ liệu):** Nhằm bảo mật thông tin PII, hệ thống quy định hiển thị:
    *   Hiển thị đầy đủ: Số CIF.
    *   Hiển thị che dấu (`****` ở giữa, chỉ hiện vài số cuối): Username, Họ và tên, Số CCCD, Số điện thoại.

### 3.3. Quản lý Yêu cầu dịch vụ (Service Request)
*   Cho phép GDV khởi tạo các yêu cầu thay đổi thông tin cho khách hàng:
    *   Yêu cầu Cấp lại/Đổi mật khẩu (Reset/Change Password).
    *   Yêu cầu Đổi số điện thoại.
    *   Yêu cầu Đổi số CCCD *(Bắt buộc upload tài liệu đính kèm)*.
    *   Yêu cầu Đổi số CIF *(Bắt buộc upload tài liệu đính kèm)*.

### 3.4. Luồng phê duyệt yêu cầu (Maker - Checker Workflow)
*   **Luồng xử lý:** 1 cấp phê duyệt (GDV tạo -> Trưởng đơn vị duyệt).
*   **Quản lý Trạng thái (Proposal):**
    *   Các yêu cầu đang diễn ra được lưu tại bảng `proposal` (Có thể thay đổi trạng thái: Chờ duyệt, Đã duyệt, Từ chối).
    *   Khi luồng yêu cầu kết thúc, toàn bộ dữ liệu chuyển sang lưu trữ tại bảng `proposal_change_log` để làm lịch sử đối soát. Bản ghi tại đây là Immutable (Không thể sửa đổi).

---

## 4. ĐẶC TẢ YÊU CẦU PHI CHỨC NĂNG (Quan trọng cho Kiến trúc)
*(Phần này quyết định việc chọn stack và pattern như bạn đã nhấn mạnh)*

### 4.1. Kiến trúc & Công nghệ (Tech Stack)
*   **Frontend:** Sử dụng **Angular bản mới nhất**, áp dụng các UI Component chuẩn hóa để thiết kế màn hình tra cứu và dashboard.
*   **Backend:** Sử dụng **Java 25** và **Spring Boot 4**, tuân thủ kiến trúc thiết kế chung của toàn bộ Simulation Bank Platform (có thể là Microservices/Modular Monolith).

### 4.2. Bảo mật & Kiểm soát Truy cập (Security & Authorization)
*   **Tư duy Corebank:** Áp dụng chặt chẽ cơ chế phân quyền bảo mật cao. 
*   **Implementation:** Backend bắt buộc phải xây dựng Custom Annotation `@CmsAuthorization(menuCode = "...", action = "...")` để filter và phân quyền động trên từng API Endpoint. Không để lọt bất kỳ request nào nếu user không có quyền.
*   **Bảo vệ Dữ liệu:** Xử lý logic Masking data (che giấu ký tự) phải được thực hiện ở Backend trước khi trả payload về cho Frontend, tránh việc FE tự che giấu nhưng user dùng DevTools bắt được data raw.

### 4.3. Khả năng Theo dõi và Kiểm toán (Observability & Auditability)
*   **Logging & Distributed Tracing:** Áp dụng các log, trace chung của toàn bộ dự án. Bắt buộc truyền `trace_id` từ FE xuống BE để theo dõi hành trình của request qua các service.
*   **Audit Trail:** Bảng `proposal_change_log` đóng vai trò như một Audit Log nghiệp vụ, đảm bảo 100% các thay đổi thông tin KH đều có thể truy vết được (Ai tạo, ai duyệt, thời gian, dữ liệu trước và sau khi đổi, tài liệu chứng minh).

### 4.4. Tính nhất quán và Tích hợp dữ liệu
*   Việc cập nhật thông tin (ví dụ: Đổi SĐT) sau khi được Trưởng đơn vị phê duyệt cần có cơ chế (API Sync hoặc Message Broker như Kafka/RabbitMQ) để cập nhật sang cơ sở dữ liệu chính của Core/Khách hàng một cách nhất quán (Eventual Consistency hoặc ACID Transactions tùy theo thiết kế của service đích).
*   **Lưu trữ File:** Các file tài liệu upload cần được lưu trữ ở một hệ thống Object Storage (như MinIO, AWS S3...) tách biệt, Backend chỉ lưu đường dẫn (URL/Path) để tối ưu hiệu năng Database.
