---
document_id: "NV4-006"
title: "Quản trị handoff và nguyên tắc phân bổ"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV4"
document_type: "policy"
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

# Quản trị handoff và nguyên tắc phân bổ

<a id="NV4-006-D01"></a>
## Nguyên tắc phân bổ công việc (Handoff)

Ngân hàng Mô phỏng thiết lập cơ chế phân bổ hồ sơ (handoff) nhằm đảm bảo tính minh bạch và tránh quá tải cho cấp duyệt. Mọi proposal sau khi tạo thành công trên CMS sẽ chuyển sang trạng thái `PENDING_APPROVAL` và được tự động phân bổ cho người có thẩm quyền phù hợp.
Hệ thống không tự động gán một cá nhân cụ thể bằng AI; thay vào đó, rules engine của hệ thống sẽ định tuyến dựa vào vai trò và phạm vi thẩm quyền của nhân sự.

<a id="NV4-006-D02"></a>
## Quy định thẩm quyền xử lý theo đơn vị

- **Cấp đơn vị thông thường**: Trưởng đơn vị hoặc người được ủy quyền hợp lệ sẽ duyệt hồ sơ phát sinh tại đơn vị quản lý, như thay đổi thông tin cá nhân cơ bản (`CIF-SIM-001`).
- **Cấp vùng / Khối**: Đối với các nghiệp vụ phức tạp ảnh hưởng nhiều đơn vị, thay đổi người đại diện pháp luật, hoặc thay đổi tài khoản nhận tiền merchant của cổng thanh toán Paygate (VD: `MRC-SIM-001` - Công ty Minh An), hồ sơ bắt buộc phải chuyển lên cấp vùng có scope tương ứng để phê duyệt.

<a id="NV4-006-D03"></a>
## Quy tắc chống xung đột lợi ích

Giao dịch viên (GDV) - người khởi tạo (chuẩn bị) hồ sơ - tuyệt đối không được phép tự duyệt proposal của chính mình, bất kể có sở hữu vai trò cấp duyệt hay không.
Bất kỳ sự can thiệp thủ công nào để thay đổi người duyệt (bypass routing) trên hệ thống CMS đều bị lưu vết (audit log) và báo cáo vi phạm mỗi ngày.

<a id="NV4-006-D04"></a>
## SLA và thời gian xử lý Handoff

| Phân loại | Thời gian xử lý tối đa (SLA) | Chế tài khi vi phạm |
|-----------|------------------------------|---------------------|
| Khách hàng cá nhân | 24 giờ làm việc | Cảnh báo tự động đến quản lý trực tiếp |
| Khách hàng doanh nghiệp (Standard) | 48 giờ làm việc | Báo cáo định kỳ lên Giám đốc chi nhánh |
| Merchant VIP trên Paygate | 12 giờ làm việc | Đánh giá lại quyền xử lý của bộ phận duyệt |

Trong trường hợp hồ sơ tồn đọng vượt quá SLA quy định do nhân sự vắng mặt, hệ thống tự động tái phân bổ (re-assign) cho cấp duyệt thay thế (backup approver) đã đăng ký.