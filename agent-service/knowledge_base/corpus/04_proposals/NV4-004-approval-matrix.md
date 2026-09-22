---
document_id: "NV4-004"
title: "Ma trận thẩm quyền và tuyến duyệt"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV4"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-04"]
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["all"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "operations"
related_documents: ["NV4-001", "NV4-002", "NV4-003"]
supersedes: []
---
# Ma trận thẩm quyền và tuyến duyệt

<a id="NV4-004-D01"></a>
## 1. Nguyên tắc phân quyền phê duyệt

Trong hệ thống CMS của Ngân hàng Mô phỏng, mọi yêu cầu (proposal) đều tuân theo nguyên tắc phân quyền và tuyến duyệt được thiết lập sẵn. Người phê duyệt là cá nhân có đủ thẩm quyền để quyết định trạng thái hồ sơ (APPROVED, REJECTED, hoặc RETURNED_FOR_CORRECTION). Tại Ngân hàng Mô phỏng, chỉ cần một người đủ thẩm quyền ra quyết định là hồ sơ được duyệt. Có một nguyên tắc bất di bất dịch: Giao dịch viên (GDV) là người tạo hồ sơ thì không phải là cấp duyệt; GDV tuyệt đối không được tự duyệt hồ sơ do chính mình tạo.

<a id="NV4-004-D02"></a>
## 2. Thẩm quyền duyệt hồ sơ cấp Đơn vị

Đối với các hồ sơ nghiệp vụ thông thường, có rủi ro thấp và nằm trong phạm vi quản lý của một đơn vị cụ thể, Trưởng đơn vị (TĐV) quản lý trực tiếp sẽ có thẩm quyền phê duyệt. Ví dụ: Cập nhật thông tin cá nhân cơ bản cho khách hàng `CIF-SIM-001` (mà không dính líu đến các chức danh đại diện doanh nghiệp phức tạp). Khi GDV nhấn Tạo và gửi duyệt, hồ sơ chuyển sang `PENDING_APPROVAL` và vào thẳng danh sách việc cần làm của TĐV đơn vị đó.

<a id="NV4-004-D03"></a>
## 3. Thẩm quyền duyệt hồ sơ cấp Vùng và phức tạp

Đối với các hồ sơ có mức độ rủi ro cao hoặc tác động rộng, thẩm quyền duyệt sẽ tự động nâng lên cấp vùng (với scope tương ứng). Các trường hợp bắt buộc phải do cấp vùng phê duyệt bao gồm:
*   Hồ sơ liên quan hoặc tác động đến nhiều đơn vị quản lý khác nhau.
*   Yêu cầu thay đổi Người đại diện pháp luật (NĐDPL) của khách hàng doanh nghiệp, ví dụ Công ty Minh An (`ENT-SIM-001`) hoặc Công ty Minh An Thương mại (`ENT-SIM-002`).
*   Yêu cầu thay đổi tài khoản nhận tiền của merchant trên cổng Paygate (như `MRC-SIM-001`).

<a id="NV4-004-D04"></a>
## 4. Quản lý định tuyến và giới hạn tự động

Hệ thống CMS tự động dựa vào metadata của form (như loại proposal, thông tin khách hàng) để quyết định tuyến duyệt phù hợp. Hệ thống nghiêm cấm việc tự gán người duyệt cụ thể bằng LLM; danh sách người phê duyệt được CMS tự động xác định một cách hệ thống và bảo mật. Hơn nữa, bot AI trong hệ thống chỉ có nhiệm vụ tìm kiếm, chuẩn bị và hướng dẫn, không can thiệp vào hành động duyệt cũng như định tuyến phê duyệt thực tế của CMS.