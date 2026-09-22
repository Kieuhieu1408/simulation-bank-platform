---
document_id: "REF-001"
title: "Ghi chú Pháp lý: Dữ liệu cá nhân, eKYC và Định danh"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV1"
document_type: "reference"
organization: "Nhà nước"
source_type: "external_regulation"
source_refs: ["ND-13-2023", "QD-2345-2023", "TT-17-2024", "Luat-CC-2023"]
status: "published"
published_at: "2024-07-01"
effective_from: "2024-07-01"
effective_to: null
product_scope: ["customer_profile", "ekyc"]
customer_scope: ["all"]
access_roles: ["knowledge_admin"]
unit_scope: ["all"]
owner_role: "legal_compliance"
related_documents: ["NV1-001", "NV1-002"]
supersedes: []
---

# Ghi chú Pháp lý: Dữ liệu cá nhân, eKYC và Định danh

> Mục đích: Cung cấp khung pháp lý thực tế để làm giàu và đối chiếu cho nghiệp vụ Quản lý thông tin Khách hàng (NV1) tại Ngân hàng Mô phỏng.

<a id="REF-001-D01"></a>
## D01. Bảo vệ dữ liệu cá nhân (Nghị định 13/2023/NĐ-CP)

- **Sự đồng ý của chủ thể dữ liệu:** Mọi hành vi thu thập, xử lý, lưu trữ dữ liệu cá nhân (đặc biệt là dữ liệu nhạy cảm như sinh trắc học, lịch sử giao dịch) phải có sự đồng ý rõ ràng, tự nguyện của khách hàng.
- **Quyền của khách hàng:** Khách hàng có quyền yêu cầu cung cấp bản sao dữ liệu, yêu cầu xóa dữ liệu hoặc rút lại sự đồng ý. Ngân hàng phải có quy trình xử lý (ví dụ: `CUSTOMER_DATA_DELETION_REQUEST`) trong vòng 72 giờ, trừ khi có quy định pháp luật khác bắt buộc lưu trữ (như Luật PCRT).
- **Ứng dụng vào Mô phỏng:** Khi thay đổi thông tin định danh hoặc thu thập CCCD mới, Form proposal luôn phải có cờ `customer_consent_verified: true`.

<a id="REF-001-D02"></a>
## D02. Sinh trắc học và Xác thực giao dịch (Quyết định 2345/QĐ-NHNN)

- **Ngưỡng bắt buộc sinh trắc học:** Kể từ 01/07/2024, các giao dịch chuyển tiền trên 10 triệu VND/lần hoặc tổng giá trị trên 20 triệu VND/ngày bắt buộc phải xác thực bằng sinh trắc học khuôn mặt khớp đúng với dữ liệu trong chip của CCCD hoặc tài khoản VNeID.
- **Thiết bị mới:** Đăng nhập lần đầu tiên trên thiết bị di động mới bắt buộc phải xác thực sinh trắc học.
- **Ứng dụng vào Mô phỏng:** Cần bổ sung các trạng thái từ chối thay đổi số điện thoại/thiết bị nhận OTP nếu không thực hiện được xác thực sinh trắc học; tạo "nhiễu" cho chatbot khi khách hàng yêu cầu thay đổi thiết bị nhưng camera hỏng.

<a id="REF-001-D03"></a>
## D03. Mở và sử dụng tài khoản thanh toán (Thông tư 17/2024/TT-NHNN)

- **Đối soát CCCD gắn chip:** Việc mở tài khoản qua phương thức điện tử (eKYC) yêu cầu ngân hàng phải đối khớp thông tin sinh trắc học của khách hàng với cơ sở dữ liệu quốc gia về dân cư (Bộ Công an).
- **Pháp nhân & Doanh nghiệp:** Người đại diện hợp pháp của tổ chức mở tài khoản cũng phải được xác thực danh tính cá nhân chặt chẽ.
- **Ứng dụng vào Mô phỏng:** Yêu cầu NV1-002 (Doanh nghiệp) phải có kiểm tra: "Người đại diện pháp luật đã hoàn tất sinh trắc học cá nhân chưa?". Nếu chưa, không cho duyệt proposal cập nhật thông tin doanh nghiệp.

<a id="REF-001-D04"></a>
## D04. Luật Căn cước 2023 (Hiệu lực 01/07/2024)

- **Thẻ Căn cước mới:** Đổi tên từ "Thẻ Căn cước công dân" thành "Thẻ Căn cước". Lược bỏ vân tay trên bề mặt thẻ, tích hợp thêm mống mắt, ADN vào cơ sở dữ liệu.
- **Giấy chứng nhận căn cước:** Cấp cho người gốc Việt Nam chưa xác định quốc tịch.
- **Ứng dụng vào Mô phỏng:** Khi thiết kế field dữ liệu, không gọi cứng là `cccd_number` mà dùng `identity_document_type` với các giá trị: `ID_CARD` (CMND cũ), `CITIZEN_IDENTITY_CARD` (CCCD), `IDENTITY_CARD` (Thẻ Căn cước 2024). Chatbot cần phân biệt được để hướng dẫn khách.
