---
document_id: "NV4-005"
title: "Xử lý lỗi và dữ liệu phản hồi"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV4"
document_type: "procedure"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["all"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "operations"
related_documents: []
supersedes: []
---

# Xử lý lỗi và dữ liệu phản hồi

<a id="NV4-005-D01"></a>
## Quy tắc nhận diện lỗi hệ thống

Ngân hàng Mô phỏng quy định các lỗi phát sinh trong quá trình tạo và duyệt proposal phải được phân loại và xử lý theo mã lỗi chuẩn. Mọi phản hồi từ hệ thống CMS và cổng thanh toán Paygate khi có sự cố phải chứa mã lỗi (Error Code) và mô tả chi tiết.
Đối với các lỗi kỹ thuật (VD: Timeout, Không thể kết nối hệ thống lõi), hệ thống tự động ghi log và trả về trạng thái `RETURNED_FOR_CORRECTION` hoặc `FAILED`.

<a id="NV4-005-D02"></a>
## Hướng dẫn xử lý dữ liệu phản hồi từ cấp duyệt

Khi một proposal bị từ chối (`REJECTED`) hoặc yêu cầu bổ sung (`RETURNED_FOR_CORRECTION`), cấp duyệt (approver) phải cung cấp lý do cụ thể trong trường `feedback_message`.
Giao dịch viên (GDV) không được phép tạo proposal mới thay thế ngay lập tức mà phải tái sử dụng `proposal_id` hiện tại và tăng `version` khi gửi lại theo chuẩn nghiệp vụ của Ngân hàng Mô phỏng. Ví dụ: Proposal cập nhật hồ sơ khách hàng `CIF-SIM-001` bị trả về, GDV cập nhật dữ liệu và nhấn gửi duyệt lại.

<a id="NV4-005-D03"></a>
## Bảng mã lỗi phổ biến và cách khắc phục

| Mã lỗi | Mô tả | Cách khắc phục |
|--------|-------|----------------|
| ERR-SIM-01 | Dữ liệu khách hàng không khớp với CIF | Kiểm tra lại thông tin khách hàng (VD: `CIF-SIM-001`), đảm bảo đồng bộ với hồ sơ gốc. |
| ERR-SIM-02 | Thiếu chữ ký số của người đại diện | Yêu cầu khách hàng bổ sung chữ ký số hoặc ký tay trên biểu mẫu giấy. |
| ERR-SIM-03 | Không tìm thấy mã merchant trên Paygate | Xác minh lại Merchant ID (VD: `MRC-SIM-001`). Yêu cầu bộ phận IT kiểm tra đồng bộ. |
| ERR-SIM-04 | Hết hạn phiên làm việc CMS | Đăng nhập lại hệ thống và khôi phục bản nháp proposal. |

<a id="NV4-005-D04"></a>
## Quy trình leo thang sự cố (Escalation)

Đối với các lỗi không nằm trong Bảng mã lỗi phổ biến hoặc ảnh hưởng đến nhiều doanh nghiệp (như `Công ty Minh An` và `Công ty Minh An Thương mại`), nhân viên thao tác phải báo cáo ngay cho bộ phận Operations.
Thời gian phản hồi tối đa cho một sự cố hệ thống Paygate hoặc CMS liên quan tới phê duyệt là 2 giờ làm việc. Nếu quá hạn, yêu cầu sẽ được leo thang lên quản trị viên hệ thống để xử lý khẩn cấp nhằm đảm bảo không ảnh hưởng đến quyền lợi hợp pháp của khách hàng.