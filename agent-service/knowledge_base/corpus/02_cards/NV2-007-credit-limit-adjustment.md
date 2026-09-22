---
document_id: "NV2-007"
title: "Điều chỉnh hạn mức thẻ tín dụng"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV2"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-04-01"
effective_from: "2026-05-01"
effective_to: null
product_scope: ["credit_card"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "card_operations"
related_documents: ["NV2-002", "NV2-004"]
supersedes: []
---

# Điều chỉnh hạn mức thẻ tín dụng

Văn bản này quy định các nguyên tắc, điều kiện và quy trình xét duyệt việc điều chỉnh hạn mức thẻ tín dụng đối với khách hàng đang sử dụng dịch vụ của Ngân hàng Mô phỏng. Việc điều chỉnh hạn mức được thực hiện nhằm quản lý rủi ro đồng thời đáp ứng nhu cầu chi tiêu của khách hàng.

<a id="NV2-007-D01"></a>
## Các trường hợp yêu cầu tăng hoặc giảm hạn mức thẻ

Việc điều chỉnh hạn mức thẻ tín dụng có thể do khách hàng chủ động yêu cầu hoặc do ngân hàng đánh giá lại rủi ro:
- **Tăng hạn mức:** Khách hàng có nhu cầu mở rộng khả năng chi tiêu và cung cấp đủ các chứng từ chứng minh năng lực tài chính gia tăng.
- **Giảm hạn mức tự nguyện:** Khách hàng chủ động yêu cầu giảm hạn mức nhằm tự kiểm soát rủi ro chi tiêu và bảo mật cá nhân.
- **Ngân hàng chủ động điều chỉnh:** Hệ thống phân tích rủi ro của Ngân hàng Mô phỏng tự động đề xuất giảm hạn mức hoặc khóa thẻ đối với các trường hợp khách hàng phát sinh nợ nhóm, thường xuyên thanh toán trễ hạn hoặc suy giảm năng lực tài chính dựa trên lịch sử giao dịch.

<a id="NV2-007-D02"></a>
## Điều kiện xét duyệt tăng hạn mức đối với khách hàng cá nhân và doanh nghiệp

Yêu cầu tăng hạn mức chỉ được xem xét nếu khách hàng đáp ứng các tiêu chuẩn tối thiểu:
- **Khách hàng cá nhân (`CIF-SIM-xxx`):** Phải duy trì thẻ tối thiểu 06 tháng kể từ ngày phát hành. Không có nợ quá hạn từ nhóm 2 trở lên. Hồ sơ chứng minh thu nhập mới phải cao hơn mức đã kê khai trước đây hoặc đáp ứng điều kiện nâng hạng thẻ (ví dụ từ Gold lên Platinum).
- **Khách hàng doanh nghiệp (`ENT-SIM-xxx`):** Yêu cầu tăng hạn mức thẻ tín dụng doanh nghiệp cần có Báo cáo tài chính gần nhất và sao kê dòng tiền qua tài khoản thanh toán tại Ngân hàng Mô phỏng. Việc đánh giá rủi ro phải xem xét hạn mức tổng thể của cả nhóm công ty nếu có dữ liệu lưu trữ theo quy định (NV1-009).
- GDV tiếp nhận hồ sơ qua luồng `CREDIT_LIMIT_REVIEW` trên CMS, đính kèm chứng từ và chuyển duyệt. AI chỉ đóng vai trò tư vấn điều kiện, không có thẩm quyền nhận định khách hàng đã đủ điều kiện hay tự động duyệt lệnh tăng.

<a id="NV2-007-D03"></a>
## Thẩm quyền phê duyệt yêu cầu điều chỉnh hạn mức

Thẩm quyền xét duyệt yêu cầu thay đổi hạn mức được phân cấp chặt chẽ trong hệ thống CMS:
- **Yêu cầu giảm hạn mức tự nguyện:** Không yêu cầu xét duyệt rủi ro. GDV có thể trực tiếp thực hiện lệnh và một Approver cấp cơ sở (chi nhánh) xác nhận.
- **Yêu cầu tăng hạn mức dưới 30% mức hiện tại:** Thẩm quyền thuộc về Approver cấp Đơn vị (Branch Manager).
- **Yêu cầu tăng hạn mức từ 30% trở lên hoặc vượt quá 500 triệu VNĐ:** Bắt buộc phải qua bước thẩm định của Khối Quản trị Rủi ro Tín dụng và phê duyệt bởi Approver cấp Vùng hoặc Hội sở.
- Mọi quyết định phê duyệt `APPROVED` hay từ chối `REJECTED` đều phải ghi rõ lý do. Nếu hồ sơ thiếu chứng từ, Approver sẽ trả lại `RETURNED_FOR_CORRECTION`.

<a id="NV2-007-D04"></a>
## Hạn mức tạm thời và quy trình tự động hoàn nguyên hạn mức cũ

Ngân hàng Mô phỏng cung cấp tính năng cấp hạn mức tín dụng tạm thời cho khách hàng có nhu cầu đột xuất (như đi du lịch, chi trả y tế viện phí):
- Khách hàng có thể được phê duyệt tăng tối đa 20% hạn mức thẻ hiện tại trong một khoảng thời gian giới hạn (từ 15 đến 30 ngày).
- Khi hết thời gian cấp hạn mức tạm thời, hệ thống CMS tự động hoàn nguyên hạn mức thẻ về mức gốc mà không cần bất kỳ phê duyệt hay thao tác thủ công nào từ Operations.
- Nếu tại thời điểm hoàn nguyên, dư nợ thực tế lớn hơn hạn mức gốc, hệ thống sẽ tính phí vượt hạn mức theo biểu phí quy định tại miền NV3 đối với phần dư nợ vượt mức. Giao dịch mới trong trường hợp này sẽ bị Paygate từ chối.

<a id="NV2-007-D05"></a>
## Hạn mức rút tiền mặt từ thẻ tín dụng

Khách hàng có thể sử dụng thẻ tín dụng để rút tiền mặt tại các máy ATM hoặc thiết bị chấp nhận thẻ. Tuy nhiên, để kiểm soát rủi ro tín dụng:
- Hạn mức rút tiền mặt tối đa được giới hạn theo một tỷ lệ phần trăm nhất định hoặc số tiền cụ thể dựa trên hạn mức tín dụng đã thỏa thuận.
- Việc kiểm soát này áp dụng tự động trên hệ thống, các giao dịch vượt mức rút tiền mặt cho phép sẽ bị từ chối.

<a id="NV2-007-D06"></a>
## Giới hạn hạn mức cho hồ sơ định danh trực tuyến (eKYC)

Ngân hàng Mô phỏng áp dụng các biện pháp kiểm soát hạn mức chặt chẽ đối với các thẻ (bao gồm thẻ tín dụng và thẻ ghi nợ) được phát hành qua phương thức điện tử không có gặp mặt trực tiếp:
- Tổng hạn mức giao dịch sẽ không được vượt quá 100 triệu VND/tháng.
- Đối với thẻ tín dụng mở trực tuyến, hạn mức tín dụng cấp phát tự động bị giới hạn (block) ở mức tối đa 100 triệu VND.
- Để sử dụng hạn mức cao hơn ngưỡng này, khách hàng bắt buộc phải hoàn thành quy trình xác thực sinh trắc học khuôn mặt (Face ID) trùng khớp với dữ liệu Căn cước công dân gắn chip.
