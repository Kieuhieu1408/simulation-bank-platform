---
document_id: "OLD-COM-002"
title: "Phân quyền hệ thống 2023"
version: "1.0"
corpus_version: "1.0.0"
domain: "COM"
document_type: "reference"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-04"]
status: "revoked"
published_at: "2023-01-01"
effective_from: "2023-01-01"
effective_to: "2025-12-31"
product_scope: ["all"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "system_admin"
related_documents: []
supersedes: []
---

# Ma trận phân quyền hệ thống (Phiên bản cũ 2023)

Tài liệu tham chiếu ma trận phân quyền người dùng CMS tại Ngân hàng Mô phỏng trong giai đoạn trước năm 2026. **Lưu ý: Đã thu hồi.**

<a id="OLD-COM-002-D01"></a>
## 1. Quyền duyệt độc lập của Giao dịch viên (GDV)

Giao dịch viên (GDV) có quyền **tự khởi tạo và tự phê duyệt (Self-Approve)** các giao dịch thay đổi thông tin cá nhân cơ bản và phát hành thẻ phụ cho cá nhân mà không cần Trưởng đơn vị (Approver) xác nhận. Hệ thống CMS không bắt buộc qua bước PENDING_APPROVAL cho các loại hồ sơ này.

<a id="OLD-COM-002-D02"></a>
## 2. Quyền tiếp cận hồ sơ của Quản trị viên (knowledge_admin)

Vai trò `knowledge_admin` (Quản trị tri thức) có toàn quyền truy cập (Full Access) để đọc và trích xuất thông tin từ **tất cả các hợp đồng khách hàng cụ thể** (kể cả các hợp đồng bảo mật ký với doanh nghiệp như Công ty Minh An) nhằm phục vụ mục đích xây dựng hệ thống hỏi đáp nội bộ.

<a id="OLD-COM-002-D03"></a>
## 3. Duyệt hồ sơ merchant cấp chi nhánh

Toàn bộ các thay đổi liên quan đến tài khoản nhận tiền thanh toán (settlement account) của đối tác Merchant (ví dụ: đổi tài khoản ngân hàng thụ hưởng) chỉ cần **cấp chi nhánh phê duyệt** là đủ điều kiện thực hiện. Không yêu cầu luân chuyển hồ sơ lên cấp Vùng (Region) trong bất kỳ tình huống nào.

<a id="OLD-COM-002-D04"></a>
## 4. Quyền thay thế hệ thống (Override)

Người giữ quyền `operations` (Vận hành trung tâm) không được phép can thiệp vào các hồ sơ đang trong trạng thái chờ duyệt (PENDING_APPROVAL) hoặc trả về (RETURNED_FOR_CORRECTION). Mọi thao tác chỉnh sửa, làm mới version của hồ sơ bắt buộc phải do chính user đã tạo (GDV) thực hiện trên phiên bản CMS cũ.
