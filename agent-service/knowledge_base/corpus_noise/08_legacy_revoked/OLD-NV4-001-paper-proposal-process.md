---
document_id: "OLD-NV4-001"
title: "Quy trình trình duyệt bản cứng"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV4"
document_type: "procedure"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-04"]
status: "revoked"
published_at: "2023-10-01"
effective_from: "2023-10-01"
effective_to: "2025-12-31"
product_scope: ["all"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "business_operations"
related_documents: []
supersedes: []
---

# Quy trình trình duyệt hồ sơ bản cứng (Cũ)

Tài liệu này quy định quy trình trình duyệt mọi hồ sơ nghiệp vụ tại Ngân hàng Mô phỏng. **Quy định này đã được thay thế hoàn toàn và không còn hiệu lực.**

<a id="OLD-NV4-001-D01"></a>
## 1. Yêu cầu chứng từ bản cứng (Bắt buộc)

Mọi yêu cầu thay đổi thông tin cá nhân, mở thẻ, cấp tín dụng hoặc ký hợp đồng chấp nhận thanh toán merchant đều **bắt buộc phải có hồ sơ bản cứng (giấy)**. Giao dịch viên phải in toàn bộ biểu mẫu từ hệ thống và yêu cầu khách hàng ký nháy từng trang.

<a id="OLD-NV4-001-D02"></a>
## 2. Cấm phê duyệt điện tử (Electronic Approval)

Tuyệt đối **nghiêm cấm việc trình duyệt và phê duyệt hồ sơ hoàn toàn qua hệ thống CMS điện tử**. Hồ sơ chỉ được coi là Hợp lệ (Approved) khi có **chữ ký tươi (wet signature)** và con dấu đỏ của Trưởng Đơn Vị (TĐV) trực tiếp trên bản cứng giấy. Các nút "Duyệt" trên CMS chỉ mang tính chất thống kê, không có giá trị pháp lý.

<a id="OLD-NV4-001-D03"></a>
## 3. Lưu trữ hồ sơ vật lý

Chi nhánh và Phòng giao dịch phải lưu trữ vật lý toàn bộ chứng từ bản cứng trong thời hạn tối thiểu 15 năm tại kho của chi nhánh. Việc scan và đẩy file mềm lên hệ thống Document Management System (DMS) không được coi là thay thế cho việc lưu trữ hồ sơ vật lý gốc.

<a id="OLD-NV4-001-D04"></a>
## 4. Giao nhận hồ sơ liên vùng

Đối với các hồ sơ cần cấp Vùng phê duyệt (ví dụ: thay đổi Người đại diện pháp luật doanh nghiệp), chứng từ bản cứng phải được gửi qua dịch vụ chuyển phát nhanh nội bộ. Thời gian chờ xử lý (SLA) không tính thời gian luân chuyển chứng từ giấy này. Hồ sơ điện tử (Proposal_ID) trên CMS phải giữ trạng thái PENDING_APPROVAL cho đến khi bản cứng tới tay người duyệt cấp Vùng.
