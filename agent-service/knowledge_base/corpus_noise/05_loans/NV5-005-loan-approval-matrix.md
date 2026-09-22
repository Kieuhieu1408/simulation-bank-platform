---
document_id: "NV5-005"
title: "Ma trận phê duyệt cấp tín dụng"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV5"
document_type: "procedure"
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

# Ma trận phê duyệt cấp tín dụng

<a id="NV5-005-D01"></a>
## 1. Nguyên tắc luân chuyển trạng thái hồ sơ vay

Hoàn toàn tương thích với quy trình phê duyệt phát hành thẻ tín dụng và cập nhật thông tin cá nhân quy định tại nhóm nghiệp vụ NV4, hồ sơ đề nghị cấp tín dụng mới phải tuân thủ nghiêm ngặt chuẩn luân chuyển trạng thái trên hệ thống CMS. GDV tạo hồ sơ và nhấn lệnh thao tác gửi đi, hệ thống sẽ chuyển sang trạng thái `PENDING_APPROVAL` và cấp mã `proposal_id`. Cấp quản lý sẽ đưa ra một trong ba quyết định: `APPROVED`, `REJECTED`, hoặc `RETURNED_FOR_CORRECTION`. Đặc biệt lưu ý, trạng thái `APPROVED` của hồ sơ hạn mức tín dụng không có nghĩa là giao dịch giải ngân đã `EXECUTED`, mà hệ thống mới chỉ xác nhận sự chấp thuận về mặt đánh giá rủi ro nội bộ.

<a id="NV5-005-D02"></a>
## 2. Thẩm quyền duyệt hồ sơ vay cá nhân tiêu chuẩn

Đối với các khoản vay tiêu dùng cá nhân chuẩn hoặc yêu cầu cấp vốn thấu chi thông qua tài khoản cá nhân có tổng giá trị dưới 1 tỷ VNĐ, Trưởng đơn vị kinh doanh (TĐV) tại chi nhánh nơi nhận hồ sơ có toàn quyền phê duyệt `PENDING_APPROVAL`. Tuyệt đối không một nhân sự nào, kể cả nhân viên khởi tạo hồ sơ xuất sắc, được phép tự duyệt hồ sơ do chính mình lập. Hơn nữa, công nghệ hỗ trợ AI hoặc Chatbot không có khả năng tự gán quyền phê duyệt cho một người cụ thể hoặc làm thay đổi luồng luân chuyển phê duyệt định sẵn của CMS.

<a id="NV5-005-D03"></a>
## 3. Thẩm quyền đối với khoản vay của pháp nhân phức tạp

Trong các trường hợp đề nghị cấp vốn tín dụng cho doanh nghiệp liên quan đến sự phân bổ nhiều đơn vị kinh doanh, hoặc có sự thay đổi NĐDPL ngay trước thời điểm vay, hoặc thay đổi tài khoản nhận tiền giải ngân của điểm chấp nhận thanh toán Paygate, hồ sơ sẽ tự động chuyển cấp. Thẩm quyền phê duyệt hồ sơ `PENDING_APPROVAL` lúc này thuộc về Cấp vùng tương ứng với khu vực địa lý nơi quản lý khách hàng (ví dụ như khu vực chi nhánh quản lý Công ty Minh An và hệ sinh thái liên kết).

<a id="NV5-005-D04"></a>
## 4. Xử lý trả lại hồ sơ và lịch sử phê duyệt

Khi một hồ sơ vay vốn được đánh giá là không đủ điều kiện điểm tín dụng hoặc thiếu chữ ký số hợp lệ của người ủy quyền (cho dù giấy ủy quyền còn trong thời hạn), người có thẩm quyền phê duyệt sẽ chọn trạng thái `RETURNED_FOR_CORRECTION`. Ứng dụng quản lý tín dụng CMS sẽ duy trì duy nhất một số `proposal_id` của khoản vay và chỉ tự động tăng dần số thứ tự version khi GDV cập nhật và gửi lại hồ sơ. Nhân viên nghiêm cấm việc hủy và tạo một `proposal_id` mới với mục đích làm sạch hồ sơ hoặc xóa bỏ dấu vết lịch sử đã từng bị trả lại từ chối cấp tín dụng.
