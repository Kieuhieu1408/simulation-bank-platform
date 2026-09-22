---
document_id: "NV4-007"
title: "Lịch sử xử lý proposal và ngoại lệ"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV4"
document_type: "procedure"
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

# Lịch sử xử lý proposal và ngoại lệ

<a id="NV4-007-D01"></a>
## Truy xuất và lưu trữ lịch sử proposal

Mọi proposal từ lúc được tạo nháp, đệ trình, duyệt, từ chối hoặc trả về đều phải được ghi nhận đầy đủ lịch sử trên CMS của Ngân hàng Mô phỏng. Dữ liệu lịch sử bao gồm: thời điểm thực hiện (timestamp), tài khoản thực hiện, nội dung thay đổi giữa các phiên bản (version), và ý kiến đính kèm.
Kho dữ liệu này không cho phép chỉnh sửa hay xóa bỏ, phục vụ công tác đối soát và hậu kiểm định kỳ. Nhằm đảm bảo tính toàn vẹn tuyệt đối, một khi Proposal chuyển sang trạng thái `APPROVED` hoặc `EXECUTED`, toàn bộ schema và dữ liệu đã nhập phải được đóng băng (read-only) và không thể thay đổi. Bất kỳ yêu cầu chỉnh sửa, sửa lỗi hay cập nhật dữ liệu nào phát sinh sau đó đều bắt buộc phải tạo một Proposal hoàn toàn mới thay vì chỉnh sửa trên Proposal đã được phê duyệt.

<a id="NV4-007-D02"></a>
## Quản lý mã hồ sơ cũ

Trong quá trình vận hành, một số loại yêu cầu đã được đổi mã hoặc thay thế quy trình. Đáng chú ý, mã `PROPOSAL-KYC-01` chỉ là bí danh lịch sử (alias) của loại yêu cầu `CUSTOMER_PERSONAL_INFORMATION_CHANGE` (theo chuẩn MVP hiện hành). 
Tương tự, mã `PROPOSAL-CARD-02` là alias cho `CARD_CLOSURE_REVIEW` và chỉ mang tính chất hướng dẫn, chưa phải là thao tác mà hệ thống tự động hóa (AI) hỗ trợ trực tiếp.

<a id="NV4-007-D03"></a>
## Xử lý các trường hợp ngoại lệ (Exception Handling)

Trong một số tình huống đặc biệt, việc xét duyệt hồ sơ có thể gặp ngoại lệ do thiếu hồ sơ cứng hợp lệ nhưng được bảo lãnh. Quy định ngoại lệ:
- Yêu cầu phải có biên bản bảo lãnh rủi ro (định dạng PDF) đính kèm.
- Giới hạn áp dụng cho các doanh nghiệp vừa và nhỏ như Công ty Minh An Thương mại (`ENT-SIM-002`) đối với các yêu cầu không làm thay đổi trực tiếp tài khoản nhận tiền Paygate.
- Chỉ Giám đốc vùng mới có quyền duyệt hồ sơ ngoại lệ này.

<a id="NV4-007-D04"></a>
## Báo cáo và rà soát định kỳ

Vào ngày mùng 5 hàng tháng, bộ phận Operations phải xuất báo cáo tổng hợp từ CMS về các hồ sơ bị `REJECTED` và `RETURNED_FOR_CORRECTION` trên 3 lần. Các trường hợp như vậy (ví dụ một yêu cầu từ `ID-SIM-001` bị nhập sai thông tin liên tục) phải được đánh giá để cải tiến luồng chuẩn bị hồ sơ hoặc tái đào tạo GDV.