---
document_id: "NV5-003"
title: "Quy định hồ sơ tài sản đảm bảo cho vay thế chấp"
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
product_scope: ["mortgage"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "credit_operations"
related_documents: []
supersedes: []
---

# Quy định hồ sơ tài sản đảm bảo cho vay thế chấp

<a id="NV5-003-D01"></a>
## 1. Xác minh danh tính chủ sở hữu tài sản

Khi tiến hành nhận tài sản thế chấp, nhân viên ngân hàng bắt buộc phải thực hiện quy trình KYC đối với chủ sở hữu tài sản. Giấy tờ tùy thân hợp lệ bao gồm CCCD gắn chip (ID-SIM-001), CMND hoặc Hộ chiếu còn hạn. Các quy định khắt khe đối với loại yêu cầu hệ thống `CUSTOMER_PERSONAL_INFORMATION_CHANGE` cũng được áp dụng cho hồ sơ chủ sở hữu tài sản nhằm đảm bảo dữ liệu cá nhân luôn đồng bộ trước khi tiến hành ký kết hợp đồng thế chấp. Nếu CCCD của chủ sở hữu hết hạn, hệ thống CMS sẽ từ chối cấp `proposal_id` mới.

<a id="NV5-003-D02"></a>
## 2. Quy định về giấy ủy quyền liên quan đến tài sản

Trong trường hợp chủ tài sản ủy quyền cho bên thứ ba thực hiện việc quản lý, định đoạt và thế chấp tài sản, bộ hồ sơ tín dụng phải bao gồm giấy ủy quyền có công chứng hợp pháp. Người được ủy quyền phải trải qua quy trình xác minh danh tính tương đương với người đại diện theo pháp luật (NĐDPL) của một doanh nghiệp thông thường. Lưu ý quan trọng: khi người ủy quyền hết hạn theo văn bản, quyền thế chấp của họ đối với tài sản đảm bảo sẽ không được AI hay hệ thống tự động gia hạn hoặc thay đổi quyền, khách hàng phải chủ động nộp hồ sơ ủy quyền bổ sung.

<a id="NV5-003-D03"></a>
## 3. Điều kiện nhận thế chấp bằng hợp đồng kinh tế

Nếu tài sản đảm bảo là quyền đòi nợ hoặc nguồn thu phát sinh từ hợp đồng kinh tế, chẳng hạn như hợp đồng phân phối của Công ty Minh An (ENT-SIM-001) đối với mạng lưới bán lẻ, thì việc định giá sẽ phụ thuộc mật thiết vào dòng tiền tương lai. Việc áp dụng biểu phí ưu đãi mới (như giảm mức thu từ 1.8% xuống 1.5% đối với các điểm chấp nhận thanh toán Paygate) có thể dẫn đến hệ quả làm giảm giá trị định giá tổng thể của quyền đòi nợ đang được thế chấp này do biên lợi nhuận của khách hàng bị ảnh hưởng.

<a id="NV5-003-D04"></a>
## 4. Phê duyệt thủ tục giải chấp tài sản

Quy trình giải chấp tài sản đảm bảo sau khi tất toán nợ đòi hỏi sự xác nhận độc lập qua nhiều cấp thẩm quyền trên hệ thống. GDV tạo hồ sơ yêu cầu giải chấp, sau đó chuyển trạng thái hệ thống thành `PENDING_APPROVAL`. Cấp phê duyệt tín dụng vùng sẽ đánh giá hồ sơ và ra quyết định `APPROVED`, `REJECTED`, hoặc `RETURNED_FOR_CORRECTION`. Cần lưu ý việc thao tác giải chấp này là quy trình riêng biệt, hoàn toàn tách biệt với quy trình thu hồi hay đóng thẻ phụ, thẻ tín dụng của khách hàng cá nhân.
