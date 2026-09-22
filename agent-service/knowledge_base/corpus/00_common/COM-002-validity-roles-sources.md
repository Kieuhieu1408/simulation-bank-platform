---
document_id: "COM-002"
title: "Hiệu lực, phân quyền và chọn nguồn dữ liệu"
version: "1.0"
corpus_version: "1.0.0"
domain: "COM"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
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
related_documents: ["COM-001", "NV4-004"]
supersedes: []
---

# Hiệu lực, phân quyền và chọn nguồn dữ liệu

<a id="COM-002-D01"></a>
## Nguyên tắc xác định hiệu lực văn bản

Tất cả các tài liệu, chính sách và hợp đồng trong hệ thống Ngân hàng Mô phỏng có hiệu lực dựa trên các mốc thời gian `effective_from` và `effective_to`.
- Văn bản có hiệu lực từ ngày `effective_from` và hết hiệu lực tại thời điểm bắt đầu ngày `effective_to` (nếu có).
- Nếu `effective_to` là `null`, văn bản tiếp tục có hiệu lực cho đến khi có văn bản khác thay thế hoặc sửa đổi.
- Trạng thái `published` biểu thị văn bản đã được ban hành trong hệ thống lưu trữ, nhưng việc áp dụng thực tế phải căn cứ vào thời điểm phát sinh nghiệp vụ của khách hàng so với hiệu lực văn bản.

<a id="COM-002-D02"></a>
## Nguyên tắc chọn nguồn dữ liệu và giải quyết xung đột

Trong quá trình xử lý proposal và tra cứu thông tin, việc chọn nguồn dữ liệu phải tuân thủ nguyên tắc:
1. **Dữ liệu chuyên biệt ưu tiên dữ liệu chung**: Hợp đồng hoặc phụ lục cụ thể ký với khách hàng (như hợp đồng fixed của Công ty Minh An) sẽ ưu tiên áp dụng so với biểu phí chung của Paygate.
2. **Thời điểm áp dụng**: Nếu có sự thay đổi chính sách, hệ thống phải căn cứ vào ngày giao dịch để áp dụng đúng phiên bản tài liệu có hiệu lực tại thời điểm đó.
3. **Cập nhật thông tin**: Khi khách hàng thay đổi giấy tờ tùy thân (ví dụ chuyển từ ID cũ sang ID-SIM-001), các hệ thống liên quan phải được rà soát và lựa chọn nguồn dữ liệu mới nhất làm cơ sở đối chiếu, không tự động ngoại suy quan hệ giữa các pháp nhân.

<a id="COM-002-D03"></a>
## Phân quyền truy cập và bảo mật

Hệ thống quản lý phân quyền chặt chẽ theo từng vai trò (role):
- **Truy cập chính sách**: Các vai trò `gdv`, `approver`, `knowledge_admin`, và `operations` đều được truy cập văn bản chính sách chung.
- **Truy cập dữ liệu khách hàng**: `knowledge_admin` không được phép truy cập vào các hợp đồng cá biệt, hồ sơ giao dịch, hoặc dữ liệu nhạy cảm của khách hàng (customer_scope cụ thể) để đảm bảo bảo mật.
- Mọi hành vi truy cập vượt quyền hoặc cố ý sử dụng sai nguồn dữ liệu sẽ bị ghi nhận trên hệ thống log của Ngân hàng Mô phỏng.

<a id="COM-002-D04"></a>
## Quy tắc ngoại lệ và bảo lưu

Các ngoại lệ liên quan đến hiệu lực văn bản hoặc chọn nguồn dữ liệu phải được phê duyệt bởi cấp có thẩm quyền thông qua một proposal ngoại lệ riêng biệt.
- Hợp đồng áp dụng mức phí ưu đãi riêng (floating hoặc fixed) chỉ hết hiệu lực khi có phụ lục sửa đổi hoặc hết hạn hợp đồng, không bị ghi đè tự động bởi bảng biểu phí chung chuẩn mới (standard).
- Đối với các chiến dịch ưu đãi (campaign), chỉ áp dụng khi có đăng ký hợp lệ từ phía merchant và thỏa mãn điều kiện, không tự ý áp dụng hàng loạt nếu không có văn bản hướng dẫn cụ thể từ Ngân hàng Mô phỏng.
