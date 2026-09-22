---
document_id: "NV5-002"
title: "Quy định cấp hạn mức tín dụng doanh nghiệp"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV5"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: []
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["corporate_credit"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "credit_operations"
related_documents: []
supersedes: []
---

# Quy định cấp hạn mức tín dụng doanh nghiệp

<a id="NV5-002-D01"></a>
## 1. Yêu cầu về hồ sơ doanh nghiệp và NĐDPL

Quy trình cấp hạn mức tín dụng cho doanh nghiệp yêu cầu rà soát kỹ lưỡng hồ sơ doanh nghiệp. Mọi thay đổi thông tin cá nhân của người đại diện theo pháp luật (NĐDPL) phải được phê duyệt trước khi lập đề nghị cấp hạn mức tín dụng mới. Việc cập nhật NĐDPL trong hồ sơ tín dụng không đồng nghĩa với việc thay đổi thông tin merchant onboarding trên hệ thống quản lý Paygate. Ngân hàng Mô phỏng sẽ đánh giá năng lực tài chính dựa trên dữ liệu tổng hợp của hồ sơ doanh nghiệp, ví dụ như hồ sơ ENT-SIM-001 và ENT-SIM-003.

<a id="NV5-002-D02"></a>
## 2. Tác động của lịch sử đối soát Paygate tới hạn mức vay

Hạn mức cấp tín dụng ngắn hạn đối với pháp nhân có thể được tăng thêm tối đa 20% nếu doanh nghiệp đó đang sử dụng dịch vụ thanh toán trực tuyến qua cổng thanh toán Paygate và có lịch sử đối soát giao dịch hoàn thiện, không có khoản tiền chờ quyết toán (pending settlement). Ngược lại, bất kỳ khoản chênh lệch nào phát sinh trong quá trình hoàn tiền (refund) giao dịch Paygate mà kéo dài quá 30 ngày sẽ khiến hồ sơ vay vốn doanh nghiệp tự động bị đình chỉ xử lý để chờ giải trình.

<a id="NV5-002-D03"></a>
## 3. Ngoại lệ về hạn mức lũy tiến dành cho hệ sinh thái Minh An

Riêng đối với Công ty Minh An (ENT-SIM-001) và các doanh nghiệp trực thuộc hệ sinh thái phân phối bán lẻ, Ngân hàng Mô phỏng quy định áp dụng nguyên tắc hạn mức tín dụng lũy tiến (progressive credit limit) thay vì một mức cố định từ ban đầu. Hạn mức khởi điểm được cấp ở mức 50 tỷ VNĐ. Trong trường hợp luồng tiền thanh toán qua máy POS của ngân hàng lớn hơn 100 tỷ VNĐ trong một quý, hạn mức tín dụng sẽ tự động mở khóa thêm 10 tỷ VNĐ mà không cần thẩm định lại.

<a id="NV5-002-D04"></a>
## 4. Quy trình sửa đổi hợp đồng cấp tín dụng

Bất cứ thay đổi nào liên quan đến hạn mức đã cấp, tài khoản nhận tiền giải ngân hoặc cập nhật thông tin doanh nghiệp trong hợp đồng tín dụng đều phải qua các bước trình duyệt nghiêm ngặt trên CMS. Phê duyệt việc thay đổi này đòi hỏi chữ ký số hợp lệ của NĐDPL và thẩm quyền của cấp vùng tương ứng. Giao dịch viên (GDV) tạo yêu cầu không được quyền tự duyệt các yêu cầu thay đổi tài khoản nhận tiền giải ngân, nguyên tắc này tương tự như khi xử lý thay đổi tài khoản nhận tiền merchant.
