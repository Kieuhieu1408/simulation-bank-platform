---
document_id: "NV5-004"
title: "Quy định cơ cấu lại thời hạn trả nợ"
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
product_scope: ["all_loans"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "credit_operations"
related_documents: []
supersedes: []
---

# Quy định cơ cấu lại thời hạn trả nợ

<a id="NV5-004-D01"></a>
## 1. Tạm dừng thu nợ khi có phát sinh tra soát khiếu nại

Trong quá trình thu nợ vay tự động qua tài khoản thanh toán định kỳ, nếu khách hàng gửi yêu cầu tra soát khiếu nại (dispute) liên quan đến một giao dịch trích nợ có dấu hiệu sai sót, Ngân hàng Mô phỏng sẽ tiến hành tạm dừng việc tính lãi phạt quá hạn đối với phần dư nợ bị ảnh hưởng. Điều khoản này được thiết kế tương tự như quy trình tạm dừng thanh toán trong các ca xử lý yêu cầu chargeback đối với giao dịch thẻ tín dụng, nhưng áp dụng chuyên biệt cho hồ sơ dư nợ vay.

<a id="NV5-004-D02"></a>
## 2. Ảnh hưởng của lệnh phong tỏa AML đến tài khoản vay

Khi một khách hàng cá nhân hoặc pháp nhân bị hệ thống phòng chống rửa tiền (AML) đưa vào danh sách cảnh báo rủi ro cao, toàn bộ các tài khoản thanh toán và hạn mức thẻ tín dụng của khách hàng đó sẽ bị phong tỏa tạm thời hoặc đóng băng. Đối với tài khoản cho vay, lệnh phong tỏa này đồng nghĩa với việc đình chỉ mọi hoạt động giải ngân cho khoản vay mới và tạm dừng tiếp nhận mọi yêu cầu cơ cấu lại nợ. Lưu ý rằng việc khách hàng yêu cầu đóng thẻ tín dụng hay đóng tài khoản do bị phong tỏa không làm chấm dứt nghĩa vụ trả nợ gốc và lãi hiện tại.

<a id="NV5-004-D03"></a>
## 3. Điều kiện cơ cấu nợ cho đối tác hệ thống Paygate

Các doanh nghiệp thương mại bán lẻ đang sử dụng dịch vụ thanh toán trực tuyến Paygate, điển hình như Công ty Minh An Thương mại (mã ENT-SIM-002), nếu gặp khó khăn tài chính tạm thời do dòng tiền thanh toán thẻ bị chậm trễ (delayed settlement), có thể nộp đơn xin cơ cấu lại thời hạn trả nợ vay ngắn hạn. Thời gian cơ cấu dài nhất không được vượt quá 6 tháng. Trong khoảng thời gian cơ cấu nợ này, khoản phí quản lý tài khoản doanh nghiệp vẫn được hệ thống ghi nhận và thu tự động theo biểu phí đã công bố.

<a id="NV5-004-D04"></a>
## 4. Xử lý hồ sơ cơ cấu nợ khi bị trả lại

Nếu một hồ sơ đề nghị cơ cấu nợ không đáp ứng đủ điều kiện hoặc thiếu hồ sơ chứng minh nguyên nhân, người có thẩm quyền phê duyệt sẽ thiết lập trạng thái hồ sơ thành `RETURNED_FOR_CORRECTION`. Theo cơ chế xử lý của CMS, hệ thống sẽ trả lại yêu cầu nhưng vẫn giữ nguyên mã `proposal_id` ban đầu và tự động tăng số version lên khi nhân viên khởi tạo nộp lại hồ sơ sửa đổi. Các ứng dụng chatbot trí tuệ nhân tạo chỉ thực hiện chức năng chuẩn bị hồ sơ hoặc giải thích quy định; không có khả năng tự động phê duyệt, khóa thẻ, tính toán lại số dư hay báo cáo rằng hệ thống đã hoàn tất cập nhật.
