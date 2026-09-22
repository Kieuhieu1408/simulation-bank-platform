---
document_id: "NV2-006"
title: "Đóng tài khoản thanh toán và thẻ liên kết"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV2"
document_type: "procedure"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-02-01"
effective_from: "2026-03-01"
effective_to: null
product_scope: ["payment_account", "debit_card"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV2-001", "NV1-001"]
supersedes: []
---

# Đóng tài khoản thanh toán và thẻ liên kết

Tài liệu này quy định quy trình và các nguyên tắc xử lý khi khách hàng cá nhân hoặc doanh nghiệp yêu cầu đóng tài khoản thanh toán tại Ngân hàng Mô phỏng, cùng với việc xử lý các sản phẩm thẻ ghi nợ (Debit Card) được liên kết với tài khoản đó.

<a id="NV2-006-D01"></a>
## Nguyên tắc chung khi đóng tài khoản thanh toán

Việc đóng tài khoản thanh toán chấm dứt khả năng giao dịch chuyển tiền, nhận tiền và thanh toán của khách hàng đối với tài khoản đó.
- Yêu cầu đóng tài khoản phải xuất phát từ chính khách hàng (hoặc NĐDPL đối với doanh nghiệp). Ngân hàng Mô phỏng chỉ tự động đóng tài khoản nếu tài khoản duy trì số dư bằng 0 (không) trong vòng 24 tháng liên tục và không phát sinh bất kỳ giao dịch nào.
- Trước khi đóng, GDV phải kiểm tra tình trạng các dịch vụ liên kết như: Thẻ ghi nợ, dịch vụ SMS Banking, Auto-debit (thanh toán hóa đơn tự động).
- Đóng tài khoản thanh toán không đồng nghĩa với việc đóng hồ sơ khách hàng (`CIF-SIM-xxx`). Thông tin định danh của khách hàng vẫn được lưu trữ theo quy định lưu trữ lịch sử (NV1).

<a id="NV2-006-D02"></a>
## Xử lý các thẻ ghi nợ (Debit Card) liên kết với tài khoản

Mọi thẻ ghi nợ của Ngân hàng Mô phỏng đều bắt buộc phải liên kết với ít nhất một tài khoản thanh toán. Do đó, khi đóng tài khoản:
- Nếu tài khoản sắp đóng là tài khoản duy nhất liên kết với thẻ, thẻ ghi nợ đó bắt buộc phải bị đóng. Hệ thống CMS sẽ sinh ra một yêu cầu phụ (sub-task) thuộc loại `CARD_CLOSURE_REVIEW` để GDV xác nhận.
- Trạng thái của thẻ liên kết sẽ chuyển thành `PENDING_CLOSURE` ngay khi tiếp nhận yêu cầu và sẽ chính thức `CLOSED` khi tài khoản được tất toán.
- Nếu thẻ được liên kết với nhiều tài khoản (tính năng đa tài khoản), khách hàng có thể chỉ định chuyển liên kết thẻ sang tài khoản khác đang hoạt động thay vì đóng thẻ.

<a id="NV2-006-D03"></a>
## Giải quyết số dư còn lại và các khoản phí chưa thanh toán

Nghiệp vụ tất toán tài khoản phải đảm bảo việc xử lý tài chính minh bạch:
- Ngân hàng Mô phỏng sẽ tự động truy thu các khoản phí quản lý tài khoản, phí thường niên thẻ ghi nợ (nếu đến hạn) hoặc phí SMS Banking còn nợ trước khi tính toán số dư cuối cùng.
- Nếu số dư còn lại lớn hơn 0, khách hàng có thể lựa chọn nhận tiền mặt tại quầy hoặc chuyển khoản sang một tài khoản khác. Mọi phí chuyển khoản trong trường hợp này tuân thủ biểu phí chuẩn ban hành cho kỳ tương ứng.
- Đối với trường hợp tài khoản có số dư nhưng bị cơ quan chức năng phong tỏa, việc đóng tài khoản bị đình chỉ cho đến khi có quyết định giải tỏa bằng văn bản.

<a id="NV2-006-D04"></a>
## Quy trình phê duyệt và xác nhận đóng tài khoản trên hệ thống

Quá trình đóng tài khoản cần được thực hiện qua hệ thống CMS với sự kiểm soát chặt chẽ:
- Chatbot hoặc hệ thống AI chỉ có vai trò hướng dẫn quy định và tiếp nhận yêu cầu ban đầu (chuẩn bị hồ sơ). AI tuyệt đối không được phép xác nhận với khách hàng là "Tài khoản đã được đóng".
- GDV lập yêu cầu đóng tài khoản trên CMS. Hệ thống yêu cầu duyệt bởi một Approver nếu tài khoản là của doanh nghiệp (`ENT-SIM-xxx`) hoặc tài khoản của khách hàng cá nhân nhưng có số dư tất toán lớn hơn 100 triệu VNĐ.
- Sau khi được duyệt (APPROVED) và lệnh đã được thực thi (EXECUTED), hệ thống gửi tin nhắn/email thông báo chính thức đến khách hàng về việc tài khoản và thẻ liên kết đã hoàn tất việc đóng.
