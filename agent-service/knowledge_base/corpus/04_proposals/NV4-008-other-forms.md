---
document_id: "NV4-008"
title: "Biểu mẫu và hướng dẫn bổ sung"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV4"
document_type: "reference"
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

# Biểu mẫu và hướng dẫn bổ sung

<a id="NV4-008-D01"></a>
## Các biểu mẫu cập nhật thông tin chung

Ngân hàng Mô phỏng cung cấp bộ biểu mẫu chuẩn mực áp dụng toàn hệ thống. Mọi điều chỉnh thông tin khách hàng cá nhân hoặc doanh nghiệp đều phải sử dụng đúng mẫu ban hành mới nhất.
- Mẫu `FORM-SIM-01`: Phiếu yêu cầu thay đổi thông tin cá nhân.
- Mẫu `FORM-SIM-02`: Đề nghị đăng ký và cập nhật Paygate Merchant (Áp dụng cho `MRC-SIM-001` và các đơn vị khác).
Các biểu mẫu phải được điền đầy đủ, không gạch xóa và ký tên hợp lệ.

<a id="NV4-008-D02"></a>
## Hướng dẫn cập nhật hồ sơ doanh nghiệp

Đối với doanh nghiệp có nhiều vai trò hoặc pháp nhân phụ (như `Công ty Minh An` và `Công ty Minh An Thương mại`), việc thay đổi căn cước công dân của cùng một người đại diện pháp luật (NĐDPL) không đồng nghĩa với việc đổi NĐDPL sang người khác.
Quy trình: Cập nhật profile cá nhân trước, sau đó rà soát hồ sơ từng doanh nghiệp và vai trò. Chỉ lập proposal thay đổi thông tin doanh nghiệp (merchant) nếu sự cập nhật trực tiếp ảnh hưởng đến dữ liệu hợp đồng hoặc thay đổi đầu mối có quyền (không tự động đồng bộ trên hệ thống).

<a id="NV4-008-D03"></a>
## Quy định về thời hạn biểu mẫu và ủy quyền

Trường hợp khách hàng sử dụng giấy ủy quyền, hiệu lực của văn bản ủy quyền phải được ghi nhận rõ trên hệ thống. 
Khi người ủy quyền hết hạn, hệ thống không tự động gia hạn, xóa hoặc đổi quyền. GDV bắt buộc phải thu thập văn bản ủy quyền mới (nếu có) và lập một proposal cấp quyền mới hoàn toàn đính kèm giấy tờ chứng minh, sau đó trình duyệt theo cấp thẩm quyền quy định.

<a id="NV4-008-D04"></a>
## Hướng dẫn tương tác với hệ thống mô phỏng

Chatbot (trợ lý ảo AI) chỉ có chức năng tìm kiếm, đọc, hỏi, giải thích và chuẩn bị hồ sơ (tạo nháp). Chatbot **tuyệt đối không** có quyền tạo chính thức, gửi duyệt, phê duyệt, khóa thẻ, tính toán lại số dư thực tế hay tự báo cáo đã cập nhật thành công vào hệ thống.
Trạng thái `APPROVED` chỉ xác nhận hồ sơ đã qua phê duyệt tại CMS, không có nghĩa là `EXECUTED` (đã thực thi thành công dưới Core Banking). Mọi xác nhận thay đổi trạng thái cuối cùng phải được sinh ra từ chính hệ thống CMS hoặc các adapter nghiệp vụ tương ứng.