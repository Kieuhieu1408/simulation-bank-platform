---
document_id: "NV1-009"
title: "Lịch sử quan hệ khách hàng và bên liên quan"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV1"
document_type: "case_record"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-03-01"
effective_from: "2026-03-01"
effective_to: null
product_scope: ["customer_profile", "authorization"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV1-002", "NV1-008"]
supersedes: []
---

# Lịch sử quan hệ khách hàng và bên liên quan

Tài liệu này quy định việc lưu trữ, truy xuất và xử lý các dữ liệu lịch sử liên quan đến mối quan hệ giữa khách hàng và các bên liên quan trên hệ thống Ngân hàng Mô phỏng. Lịch sử này đóng vai trò quan trọng trong việc truy vết giao dịch, đối soát và giải quyết tranh chấp pháp lý.

<a id="NV1-009-D01"></a>
## Ghi nhận và lưu trữ lịch sử thay đổi thông tin người đại diện

Mọi thay đổi liên quan đến người đại diện theo pháp luật (NĐDPL) hoặc người được ủy quyền của một khách hàng tổ chức (ví dụ: `CIF-SIM-001`, `ENT-SIM-001`) đều phải được hệ thống ghi nhận thành các phiên bản bản ghi (record versions).
- Dữ liệu lưu trữ bao gồm: Họ tên, số giấy tờ tùy thân (ví dụ: `ID-SIM-001`), thời điểm bắt đầu hiệu lực, thời điểm kết thúc hiệu lực, và GDV thực hiện cập nhật.
- Thông tin về người đại diện cũ không bị xóa khỏi hệ thống mà chuyển sang trạng thái `INACTIVE_HISTORICAL`.
- Các giao dịch, hợp đồng được ký kết hoặc phê duyệt bởi NĐDPL trong thời gian người này còn hiệu lực vẫn giữ nguyên giá trị pháp lý, trừ phi có phán quyết khác từ cơ quan có thẩm quyền.

<a id="NV1-009-D02"></a>
## Phân tích mối liên hệ giữa các thực thể doanh nghiệp

Hệ thống Core Banking của Ngân hàng Mô phỏng không tự động suy luận mối quan hệ sở hữu hoặc điều hành giữa các doanh nghiệp chỉ dựa trên sự tương đồng về tên gọi.
- Ví dụ: Việc có hai hồ sơ là Công ty Minh An (`ENT-SIM-001`) và Công ty Minh An Thương mại (`ENT-SIM-002`) không đồng nghĩa với việc hai công ty này thuộc cùng một nhóm công ty mẹ - con.
- Mối liên hệ (Related Parties) chỉ được hệ thống ghi nhận khi có bằng chứng pháp lý (Giấy chứng nhận đăng ký doanh nghiệp thể hiện tỷ lệ sở hữu, văn bản xác nhận chung tập đoàn) và được nhập thủ công bởi nhân viên Operations sau khi cấp Approver phê duyệt.
- Dữ liệu liên kết này được dùng để tính toán hạn mức rủi ro tín dụng tổng hợp cho toàn bộ nhóm khách hàng liên quan.

<a id="NV1-009-D03"></a>
## Xử lý các ủy quyền đã hết hiệu lực trong lịch sử

Người được ủy quyền giao dịch hoặc vận hành cổng thanh toán Paygate (ví dụ cho merchant `MRC-SIM-001`) phải hoạt động trong thời hạn ủy quyền đã đăng ký.
- Khi một văn bản ủy quyền hết hạn, hệ thống tự động tước bỏ quyền truy cập và phê duyệt của cá nhân đó.
- Nhân viên GDV, Operations hay hệ thống không được phép tự động gia hạn, xóa hoặc thay đổi quyền khi chưa có văn bản ủy quyền mới.
- Các giao dịch đã được người ủy quyền thực hiện trong khoảng thời gian hiệu lực (dựa trên tem thời gian giao dịch) được coi là hợp lệ. Mọi khiếu nại phát sinh từ các hành động này sẽ được giải quyết dựa trên quy định tại thời điểm thực hiện.

<a id="NV1-009-D04"></a>
## Truy xuất dữ liệu lịch sử cho mục đích đối soát và kiểm toán

Để đáp ứng yêu cầu của tổ chức kiểm toán hoặc cơ quan điều tra, Ngân hàng Mô phỏng cung cấp chức năng trích xuất toàn bộ lịch sử quan hệ của khách hàng.
- Báo cáo truy xuất (`CUSTOMER_HISTORY_REPORT`) bao gồm: Lịch sử thay đổi thông tin định danh, danh sách những người đại diện pháp luật qua các thời kỳ, lịch sử cấp và thu hồi quyền ủy quyền, cũng như các điểm chấp nhận thẻ (Merchant ID) có liên quan.
- Báo cáo này cần được tạo thông qua một Yêu cầu chuẩn bị (Proposal) với loại `CUSTOMER_HISTORY_REVIEW` và phải được cấp vùng hoặc Khối Vận Hành duyệt trước khi xuất bản bản cứng.
- Hệ thống AI/Chatbot chỉ hỗ trợ tìm kiếm và tóm tắt thông tin lịch sử, không có chức năng tự động sinh ra báo cáo có mộc đỏ hoặc trực tiếp cung cấp cho bên thứ ba.
