---
document_id: "REF-004"
title: "Ghi chú Pháp lý: Giao dịch Điện tử và Chứng từ"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV4"
document_type: "reference"
organization: "Nhà nước"
source_type: "external_regulation"
source_refs: ["Luat-GDDT-2023"]
status: "published"
published_at: "2024-07-01"
effective_from: "2024-07-01"
effective_to: null
product_scope: ["approval_process", "e_document"]
customer_scope: ["all"]
access_roles: ["knowledge_admin"]
unit_scope: ["all"]
owner_role: "operations_control"
related_documents: ["NV4-001", "NV4-003"]
supersedes: []
---

# Ghi chú Pháp lý: Giao dịch Điện tử và Chứng từ

> Mục đích: Làm nền tảng pháp lý cho quy trình chuẩn bị hồ sơ điện tử, quản trị schema form và quy trình ký số (NV4).

<a id="REF-004-D01"></a>
## D01. Chữ ký điện tử (Luật Giao dịch điện tử 2023)

- **Phân loại chữ ký:** Có 3 loại: Chữ ký điện tử chuyên dùng, Chữ ký số công cộng, và Chữ ký điện tử khác (như OTP, SMS, mã PIN, sinh trắc học).
- **Giá trị pháp lý:** Chữ ký số và chữ ký điện tử an toàn (sinh trắc + OTP) có giá trị pháp lý tương đương chữ ký tay của cá nhân và con dấu của tổ chức.
- **Ứng dụng vào Mô phỏng:** Tại NV4-003 (Hồ sơ bắt buộc), thay vì bắt khách hàng nộp văn bản giấy, chatbot được phép chuẩn bị Proposal điện tử (`ELECTRONIC_PROPOSAL`) với yêu cầu khách hàng ký số qua app thay vì ra quầy.

<a id="REF-004-D02"></a>
## D02. Chuyển đổi chứng từ giấy và điện tử

- **Giấy sang Điện tử:** Giấy tờ, hợp đồng bản giấy có thể được chuyển thành chứng từ điện tử nếu đảm bảo tính toàn vẹn, có chữ ký của người/tổ chức thực hiện chuyển đổi. (Ví dụ: GDV scan giấy tờ của khách, ký số của GDV để lưu lên CMS).
- **Điện tử ra Giấy:** Chứng từ điện tử cũng có thể in ra giấy và có giá trị pháp lý nếu đáp ứng đủ điều kiện về nhận dạng hệ thống nguồn.
- **Ứng dụng vào Mô phỏng:** Cơ sở để GDV thực thi "Tạo và gửi duyệt". Hệ thống CMS mô phỏng chấp nhận bản scan do GDV upload là bản chính thức nếu GDV (`creator`) đã xác thực đăng nhập nội bộ.

<a id="REF-004-D03"></a>
## D03. Tính toàn vẹn và Lưu trữ dữ liệu

- **Lưu trữ:** Thông tin trong thông điệp dữ liệu (Proposal Form) phải luôn có thể truy cập và sử dụng được để tham chiếu.
- **Toàn vẹn:** Dữ liệu không bị thay đổi, trừ các thay đổi về hình thức phát sinh trong quá trình trao đổi, lưu trữ hoặc hiển thị thông điệp.
- **Ứng dụng vào Mô phỏng:** NV4-007 (Lịch sử xử lý proposal). Một khi Proposal đã chuyển sang trạng thái `APPROVED` hoặc `EXECUTED`, schema và dữ liệu đã nhập không được phép sửa đổi (Read-only). Nếu sai, phải tạo Proposal yêu cầu sửa lỗi riêng.

<a id="REF-004-D04"></a>
## D04. Thông điệp dữ liệu thay thế văn bản thỏa thuận

- Trong giao dịch ngân hàng, các điều khoản và điều kiện (T&C) hiển thị trên màn hình ứng dụng khi khách hàng bấm "Đồng ý" (Click-wrap agreement) được xem là thông điệp dữ liệu có giá trị giao kết hợp đồng.
- **Ứng dụng vào Mô phỏng:** Cho phép khách hàng `ENT-SIM-002` xác nhận biểu phí thả nổi trên ứng dụng doanh nghiệp mà không cần ký văn bản giấy; ghi đè thủ tục giấy truyền thống.
