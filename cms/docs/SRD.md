# TÀI LIỆU ĐẶC TẢ YÊU CẦU HỆ THỐNG (SRD - System Requirements Document)
**Dự án:** Simulation Bank Platform - CMS Portal

## 1. MỤC ĐÍCH
Tài liệu SRD mô tả chi tiết các yêu cầu kỹ thuật cấp hệ thống, bao gồm cấu trúc cơ sở dữ liệu (ERD), định nghĩa các API chính, và luồng trạng thái (State Machine) của nghiệp vụ Maker-Checker.

---

## 2. THIẾT KẾ CƠ SỞ DỮ LIỆU (DATABASE SCHEMA / ERD)

Dưới đây là thiết kế các bảng (Tables) cốt lõi phục vụ Phân quyền (Authorization) và Luồng Yêu cầu (Proposal).

### 2.1. Nhóm bảng Phân quyền (Authorization)

**Bảng `cms_role`** (Lưu trữ các nhóm quyền)
* `id` (PK, UUID/BigInt)
* `role_code` (Varchar, Unique) - Ví dụ: `ROLE_GDV`, `ROLE_CHECKER`
* `role_name` (Varchar) - Tên hiển thị
* `description` (Varchar)
* `status` (Enum: ACTIVE, INACTIVE)
* Các trường Audit: `created_by`, `created_at`, `updated_by`, `updated_at`

**Bảng `cms_permission`** (Định nghĩa ma trận quyền - Menu + Action)
* `id` (PK)
* `menu_code` (Varchar) - Ví dụ: `user_info`, `proposal_management`
* `action_code` (Varchar) - Ví dụ: `VIEW`, `CREATE`, `UPDATE`, `APPROVE`
* `description` (Varchar)

**Bảng `cms_role_permission`** (Mapping N-N giữa Role và Permission)
* `role_id` (FK -> `cms_role.id`)
* `permission_id` (FK -> `cms_permission.id`)
* `is_granted` (Boolean) - Trạng thái cấp quyền (Mặc định: True)

*(Lưu ý: User-Role mapping được quản lý bởi Keycloak, Backend sẽ nhận Role qua JWT, sau đó query vào các bảng này để validate quyền `@CmsAuthorization(menuCode="user_info", action="VIEW")`)*

### 2.2. Nhóm bảng Quản lý Yêu cầu (Proposal / Workflow)

**Bảng `proposal`** (Lưu các yêu cầu ĐANG active - Mutable)
* `id` (PK, UUID)
* `proposal_code` (Varchar, Unique) - Mã yêu cầu (VD: `PRP-202609-0001`)
* `customer_cif` (Varchar) - CIF của khách hàng được yêu cầu thay đổi
* `proposal_type` (Enum) - Loại yêu cầu: `CHANGE_PWD`, `CHANGE_PHONE`, `CHANGE_CCCD`, `CHANGE_CIF`
* `old_data` (JSONB / Text) - Dữ liệu cũ trước khi đổi
* `new_data` (JSONB / Text) - Dữ liệu mới đề xuất đổi
* `document_urls` (JSONB / Text) - Danh sách các link file đính kèm (Lưu trên MinIO/S3)
* `status` (Enum) - Trạng thái: `PENDING` (Chờ duyệt), `PROCESSING` (Đang xử lý)
* `maker_id` (Varchar) - User ID (từ Keycloak) của GDV tạo yêu cầu
* `checker_id` (Varchar) - User ID của người đang giữ quyền duyệt (Nullable)
* Các trường Audit: `created_at`, `updated_at`, `version` (Dùng cho Optimistic Locking)

**Bảng `proposal_change_log`** (Lưu vết lịch sử các yêu cầu ĐÃ kết thúc - Immutable)
* `id` (PK, UUID)
* `proposal_id` (FK -> `proposal.id` / Hoặc lưu vết độc lập)
* `proposal_code` (Varchar)
* `customer_cif` (Varchar)
* `proposal_type` (Enum)
* `old_data` (JSONB)
* `new_data` (JSONB)
* `document_urls` (JSONB)
* `final_status` (Enum) - Trạng thái cuối: `APPROVED` (Đã duyệt), `REJECTED` (Từ chối), `CANCELLED` (Đã hủy)
* `maker_id` (Varchar)
* `checker_id` (Varchar) - Người thực sự bấm nút Duyệt/Từ chối
* `reason` (Text) - Lý do từ chối (nếu có)
* `completed_at` (Timestamp) - Thời gian kết thúc luồng

---

## 3. LUỒNG TRẠNG THÁI YÊU CẦU (PROPOSAL STATE MACHINE)

Vòng đời của 1 Proposal (Yêu cầu thay đổi thông tin) trải qua các trạng thái:
1. **DRAFT (Tùy chọn):** GDV đang tạo nhưng chưa Submit.
2. **PENDING:** GDV Submit thành công, chờ Trưởng đơn vị duyệt. Bản ghi nằm ở bảng `proposal`.
3. **APPROVED:** Trưởng đơn vị bấm Phê duyệt.
   * Dữ liệu được sync sang Core/Customer Service.
   * Bản ghi di chuyển sang bảng `proposal_change_log` với `final_status = APPROVED`.
   * Xóa hoặc đánh dấu `DELETED` ở bảng `proposal`.
4. **REJECTED:** Trưởng đơn vị bấm Từ chối.
   * Bắt buộc nhập `reason`.
   * Bản ghi di chuyển sang bảng `proposal_change_log` với `final_status = REJECTED`.
5. **CANCELLED:** GDV (Maker) tự hủy yêu cầu khi đang ở trạng thái `PENDING`.

---

## 4. DANH SÁCH API CỐT LÕI (CORE API ENDPOINTS)

Tất cả API đều bắt buộc có Header `Authorization: Bearer <token>` và `X-Correlation-ID`.

### 4.1. API Tra cứu (Customer Lookup)
* **Endpoint:** `GET /api/v1/customers/search`
* **Params:** `cccd={cccd_number}`
* **Quyền hạn:** `@CmsAuthorization(menuCode = "user_info", action = "VIEW")`
* **Response:**
  * Dữ liệu trả về (DTO) phải được Masking (che dấu `***` đối với Tên, SĐT, CCCD).
  * Trả về CIF đầy đủ.

### 4.2. API Upload Tài liệu
* **Endpoint:** `POST /api/v1/documents/upload`
* **Body:** `multipart/form-data` (Files)
* **Response:** Trả về danh sách URL/đường dẫn file đã lưu trên Object Storage.

### 4.3. API Quản lý Proposal (Maker)
* **Tạo yêu cầu:** `POST /api/v1/proposals`
  * **Quyền hạn:** `@CmsAuthorization(menuCode = "proposal_management", action = "CREATE")`
  * **Body:** `type`, `customerCif`, `newData`, `documentUrls`
* **Hủy yêu cầu:** `POST /api/v1/proposals/{id}/cancel`

### 4.4. API Phê duyệt Proposal (Checker)
* **Duyệt yêu cầu:** `POST /api/v1/proposals/{id}/approve`
  * **Quyền hạn:** `@CmsAuthorization(menuCode = "proposal_management", action = "APPROVE")`
* **Từ chối yêu cầu:** `POST /api/v1/proposals/{id}/reject`
  * **Body:** `{"reason": "Sai tài liệu"}`

---

## 5. BẢO MẬT VÀ TOÀN VẸN DỮ LIỆU
* **Optimistic Locking:** Sử dụng field `version` trong bảng `proposal` (bằng tính năng `@Version` của JPA) để tránh lỗi Lost Update (Hai checker cùng duyệt 1 proposal).
* **Data Masking Strategy:** Viết Custom Jackson Serializer (ví dụ `@MaskString(pattern="*", visibleTail=3)`) áp dụng thẳng vào các properties nhạy cảm trong Response DTO để tự động che dấu khi JSON hóa. Mọi Dev Backend trong dự án chỉ cần gắn Annotation này vào DTO là an toàn.