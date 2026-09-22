---
document_id: "NV4-001"
title: "Quy trình lập và xử lý proposal chung"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV4"
document_type: "procedure"
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
related_documents: ["NV4-002", "NV4-003", "NV4-004"]
supersedes: []
---
# Quy trình lập và xử lý proposal chung

<a id="NV4-001-D01"></a>
## 1. Khởi tạo và Chuẩn bị Proposal

Trong hệ thống Ngân hàng Mô phỏng (CMS), bước chuẩn bị hồ sơ (proposal) là giai đoạn đầu tiên để thực hiện các nghiệp vụ như cập nhật thông tin khách hàng, thay đổi dịch vụ hoặc xử lý khiếu nại. Tại bước chuẩn bị này, hồ sơ chưa được cấp `proposal_id`. Giao dịch viên (GDV) có trách nhiệm thu thập thông tin, kiểm tra tính hợp lệ ban đầu của các giấy tờ, và điền đầy đủ dữ liệu vào hệ thống. Trong trường hợp khách hàng như Công ty Minh An (ENT-SIM-001), GDV phải đảm bảo các thông tin cập nhật không mâu thuẫn với dữ liệu gốc của Ngân hàng Mô phỏng.

<a id="NV4-001-D02"></a>
## 2. Gửi duyệt và Trạng thái PENDING_APPROVAL

Sau khi hoàn tất bước chuẩn bị, GDV trên CMS sử dụng nút "Tạo và gửi duyệt". Hệ thống sẽ tạo ra một `proposal_id` duy nhất và chuyển trạng thái của hồ sơ sang `PENDING_APPROVAL`. Lúc này, hồ sơ được chuyển đến cấp có thẩm quyền phê duyệt dựa trên ma trận tuyến duyệt. Trong thời gian hồ sơ đang ở trạng thái `PENDING_APPROVAL`, GDV không thể chỉnh sửa dữ liệu trừ khi hồ sơ bị trả lại. Hệ thống chatbot AI chỉ đóng vai trò tìm kiếm, hướng dẫn, giải thích và chuẩn bị, không có quyền thao tác trực tiếp tạo, gửi hay phê duyệt.

<a id="NV4-001-D03"></a>
## 3. Xử lý Phê duyệt (APPROVED / REJECTED)

Người phê duyệt xem xét hồ sơ và quyết định một trong các hành động xử lý. Nếu hồ sơ hợp lệ và đầy đủ điều kiện, người duyệt chọn phê duyệt, trạng thái hồ sơ chuyển thành `APPROVED`. Lưu ý rằng `APPROVED` không có nghĩa là `EXECUTED` (đã thực thi thành công vào hệ thống lõi), mà chỉ xác nhận hồ sơ đã được duyệt về mặt nghiệp vụ. Hơn nữa, nhằm đảm bảo tính toàn vẹn dữ liệu nghiêm ngặt, một khi hồ sơ đã chuyển sang `APPROVED` hoặc `EXECUTED`, bất kỳ việc chỉnh sửa thông tin nào cũng không được phép; mọi nhu cầu hiệu đính bắt buộc phải tạo một proposal hoàn toàn mới. Nếu hồ sơ vi phạm chính sách của Ngân hàng Mô phỏng hoặc cổng thanh toán Paygate, người duyệt sẽ từ chối và trạng thái chuyển thành `REJECTED`. Chỉ cần một người đủ thẩm quyền quyết định, và GDV tạo hồ sơ tuyệt đối không phải là cấp duyệt.

<a id="NV4-001-D04"></a>
## 4. Xử lý Trả lại (RETURNED_FOR_CORRECTION) và Quy định chung

Trong trường hợp hồ sơ thiếu giấy tờ hợp lệ (ví dụ: thiếu ID-SIM-001 của người đại diện) hoặc sai sót dữ liệu, người duyệt có quyền trả lại hồ sơ. Trạng thái lúc này là `RETURNED_FOR_CORRECTION`. Khi GDV tiến hành sửa đổi và gửi lại, hệ thống vẫn giữ nguyên `proposal_id` ban đầu nhưng sẽ tăng số phiên bản (version) của hồ sơ. GDV tạo hồ sơ thì không tự duyệt. Hơn nữa, hệ thống không tự gán người duyệt cụ thể bằng LLM; việc điều phối người duyệt được thực hiện tự động theo cơ chế của CMS dựa vào đơn vị và loại hồ sơ.