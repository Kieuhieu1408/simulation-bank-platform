---
document_id: "NV4-003"
title: "Danh mục hồ sơ bắt buộc theo tình huống"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV4"
document_type: "procedure"
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
related_documents: ["NV4-001", "NV4-002"]
supersedes: []
---

# Danh mục hồ sơ bắt buộc theo tình huống

<a id="NV4-003-D01"></a>
## Yêu cầu hồ sơ đối với thay đổi thông tin cá nhân

Khi tạo proposal cập nhật hoặc thay đổi thông tin cá nhân của khách hàng (ví dụ mã CIF-SIM-001), hồ sơ bắt buộc bao gồm:
- Bản sao giấy tờ tùy thân hợp lệ mới (như ID-SIM-001) có đối chiếu với bản chính.
- Biểu mẫu đề nghị thay đổi thông tin khách hàng có chữ ký trực tiếp hoặc xác thực qua phương thức điện tử hợp lệ (chữ ký số, eKYC tiên tiến). Thay vì bắt buộc khách hàng nộp văn bản giấy, hệ thống cho phép chuẩn bị dưới dạng `ELECTRONIC_PROPOSAL`, trong đó chữ ký số, mã OTP, hoặc xác thực sinh trắc học trên ứng dụng có giá trị thay thế hoàn toàn chứng từ giấy.
- Đối với trường hợp đổi ID dẫn đến thay đổi nhân thân căn bản, cần có thêm biên bản kiểm tra rủi ro.

<a id="NV4-003-D02"></a>
## Yêu cầu hồ sơ đối với nghiệp vụ Merchant (Paygate)

Đối với các merchant như Công ty Minh An (MRC-SIM-001), khi đăng ký onboarding hoặc cập nhật thông tin:
- Giấy chứng nhận đăng ký doanh nghiệp bản sao hợp lệ hoặc trích lục điện tử.
- Giấy tờ tùy thân của người đại diện theo pháp luật (NĐDPL).
- Hợp đồng dịch vụ Paygate hoặc phụ lục hợp đồng, có đủ chữ ký, đóng dấu hoặc chữ ký số doanh nghiệp.
- Mọi hồ sơ liên quan đến biểu phí (floating/fixed) phải được đính kèm trong CMS dưới dạng chứng từ số hóa. Đặc biệt đối với các biểu phí thả nổi (floating fee tariffs), việc khách hàng (như ENT-SIM-002) bấm xác nhận "Đồng ý" thông qua thỏa thuận click-wrap trên ứng dụng doanh nghiệp được công nhận là thông điệp dữ liệu có giá trị giao kết hợp lệ, ghi đè và thay thế hoàn toàn thủ tục ký văn bản giấy truyền thống.

<a id="NV4-003-D03"></a>
## Hồ sơ ủy quyền và thay đổi người đại diện

Trường hợp thay đổi người đại diện theo pháp luật hoặc thêm người được ủy quyền (ví dụ từ NĐDPL cũ sang NĐDPL mới):
- Văn bản ủy quyền hợp lệ theo quy định pháp luật (có công chứng/chứng thực nếu pháp luật yêu cầu).
- Bản sao ID của người được ủy quyền.
- Xin lưu ý: Đổi giấy tờ tùy thân của một NĐDPL không đồng nghĩa với việc thay đổi NĐDPL. Hệ thống không tự động gia hạn quyền ủy quyền khi văn bản ủy quyền hết hạn.

<a id="NV4-003-D04"></a>
## Hồ sơ đối với các yêu cầu ngoại lệ

Khi có các yêu cầu ngoại lệ (ví dụ: áp dụng biểu phí campaign đặc thù cho MRC-SIM-002 ngoài quy định):
- Tờ trình ngoại lệ nêu rõ lý do, đánh giá rủi ro và hiệu quả kinh doanh dự kiến.
- Các tài liệu chứng minh merchant thỏa mãn các tiêu chí của chương trình ưu đãi (campaign).
- Bằng chứng phê duyệt từ cấp có thẩm quyền thông qua luồng proposal ngoại lệ trên CMS trước khi hệ thống Paygate được cập nhật.
