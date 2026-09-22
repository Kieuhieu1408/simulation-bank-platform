---
document_id: "NV1-008"
title: "Ngoại lệ trong quản lý hồ sơ doanh nghiệp"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV1"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-02-15"
effective_from: "2026-03-01"
effective_to: null
product_scope: ["business_profile"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV1-001", "NV1-002", "NV1-003"]
supersedes: []
---

# Ngoại lệ trong quản lý hồ sơ doanh nghiệp

Văn bản này quy định các trường hợp ngoại lệ phát sinh trong quá trình quản lý hồ sơ doanh nghiệp (định danh dạng `ENT-SIM-xxx`) và các điểm tiếp nhận thẻ (Merchant, định danh dạng `MRC-SIM-xxx`) trên hệ thống Ngân hàng Mô phỏng, đặc biệt trong các nghiệp vụ liên quan đến cổng thanh toán Paygate.

<a id="NV1-008-D01"></a>
## Xử lý hồ sơ doanh nghiệp có nhiều người đại diện pháp luật

Theo quy định quản trị rủi ro của Ngân hàng Mô phỏng, một doanh nghiệp có thể có nhiều người đại diện theo pháp luật (NĐDPL). Trong trường hợp này:
- Hồ sơ khách hàng trên hệ thống phải ghi nhận đầy đủ danh sách các NĐDPL cùng với thông tin định danh tương ứng (ví dụ: `ID-SIM-001`, `ID-SIM-002`).
- Bất kỳ yêu cầu cập nhật thông tin nào liên quan đến thông tin chung của doanh nghiệp đều phải có chữ ký của ít nhất một NĐDPL hoặc theo quy định cụ thể tại Điều lệ công ty.
- Trong trường hợp có sự thay đổi một trong các NĐDPL, hệ thống yêu cầu kiểm tra kỹ hồ sơ. Việc cập nhật CCCD/ID của một NĐDPL cụ thể không được coi là thay đổi toàn bộ NĐDPL của doanh nghiệp. Cần chuẩn bị yêu cầu cập nhật đối với hợp đồng merchant (như trường hợp của Công ty Minh An `ENT-SIM-001` đang sử dụng Paygate) nếu người đó đang đóng vai trò là đầu mối liên hệ chính hoặc người ký hợp đồng.

<a id="NV1-008-D02"></a>
## Doanh nghiệp tạm ngừng kinh doanh hoặc đang trong quá trình giải thể

Khi ngân hàng nhận được thông báo bằng văn bản hoặc tra cứu được thông tin từ cơ quan nhà nước có thẩm quyền về việc doanh nghiệp tạm ngừng kinh doanh hoặc đang giải thể:
- **Tạm ngừng kinh doanh:** Trạng thái hồ sơ trên hệ thống được cập nhật thành `SUSPENDED`. Các hợp đồng sử dụng dịch vụ thanh toán qua Paygate sẽ tự động chuyển sang trạng thái tạm khóa. Các giao dịch thanh toán từ thẻ khách hàng vào merchant sẽ bị từ chối với mã lỗi đặc thù. Khách hàng phải có văn bản yêu cầu mở lại khi hoạt động kinh doanh tiếp tục.
- **Giải thể/Phá sản:** Trạng thái hồ sơ chuyển thành `IN_DISSOLUTION` hoặc `CLOSED`. Các quyền truy cập của NĐDPL và người được ủy quyền bị vô hiệu hóa, ngoại trừ các nghiệp vụ liên quan đến đối soát, tất toán và thanh lý hợp đồng. Yêu cầu báo cáo lên cấp vùng phê duyệt (Approver thuộc vùng) để chấm dứt các hợp đồng merchant.

<a id="NV1-008-D03"></a>
## Xung đột thông tin giữa các chi nhánh của cùng một doanh nghiệp

Một doanh nghiệp có thể mở nhiều chi nhánh độc lập hoặc hạch toán phụ thuộc. Trong trường hợp hệ thống phát hiện có xung đột thông tin giữa mã hồ sơ tổng (`ENT-SIM-001`) và các mã chi nhánh/merchant trực thuộc (như `MRC-SIM-001`):
- Thông tin về pháp nhân (Tên doanh nghiệp, mã số thuế, địa chỉ trụ sở chính, NĐDPL) bắt buộc phải lấy theo dữ liệu của pháp nhân mẹ.
- Các thông tin về địa điểm kinh doanh, số điện thoại liên hệ, email nhận đối soát hoặc người được ủy quyền vận hành trực tiếp tại điểm bán có thể khác biệt và được cập nhật độc lập trên từng hồ sơ merchant.
- Nếu có yêu cầu cập nhật thông tin pháp nhân từ phía chi nhánh mà không có ủy quyền hợp lệ từ pháp nhân mẹ, GDV (Giao dịch viên) phải từ chối yêu cầu và yêu cầu bổ sung giấy tờ từ trụ sở chính.

<a id="NV1-008-D04"></a>
## Quản lý hồ sơ đối với các doanh nghiệp đặc thù (F&B và Bán lẻ)

Ngân hàng Mô phỏng phân loại và áp dụng quy tắc quản lý hồ sơ riêng cho một số ngành nghề:
- **Ngành F&B (Nhà hàng, Quán ăn - ví dụ: Công ty Minh An `ENT-SIM-001`):** Yêu cầu cập nhật định kỳ (mỗi 12 tháng) về giấy chứng nhận vệ sinh an toàn thực phẩm. Nếu quá hạn mà không có cập nhật, hệ thống CMS sẽ đưa ra cảnh báo (Warning) nhưng không tự động khóa dịch vụ Paygate cho đến khi có quyết định từ khối quản lý rủi ro.
- **Ngành Bán lẻ (Retail - ví dụ: Công ty Minh An Thương mại `ENT-SIM-002`):** Được phép tạo hàng loạt mã điểm bán (Store ID) dưới cùng một Merchant ID. Quản trị viên (Operations) có quyền gộp (merge) hoặc tách (split) doanh thu đối soát theo từng điểm bán lẻ mà không cần phải lập hồ sơ doanh nghiệp mới, miễn là thuộc cùng một pháp nhân `ENT-SIM-002`.
