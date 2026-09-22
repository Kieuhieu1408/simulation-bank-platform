---
document_id: "NV2-003"
title: "Quy định gốc về phát hành thẻ phụ tín dụng và ghi nợ"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV2"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["supplementary_credit_card", "supplementary_debit_card"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "card_operations"
related_documents: ["NV2-004", "NV2-007"]
supersedes: []
---

# Quy định gốc về phát hành thẻ phụ tín dụng và ghi nợ

Chính sách mô phỏng này phải được đọc cùng sửa đổi NV2-004. Tài liệu vẫn có hiệu lực cho các điều khoản không bị sửa đổi; ngày hiệu lực của toàn văn không đủ để quyết định tuổi tối thiểu cho mọi loại thẻ.

<a id="NV2-003-D01"></a>
## D01. Đối tượng, tuổi và mốc xét

Người đề nghị phát hành phải là chủ thẻ chính hoặc chủ tài khoản thanh toán có quyền chỉ định người dùng thẻ phụ trong sản phẩm tương ứng. Người dùng thẻ phụ phải có hồ sơ định danh xác thực; quan hệ cùng hộ gia đình không thay thế sự chấp thuận của chủ thể có quyền. Tài liệu này không cấp quyền đại diện doanh nghiệp và không áp dụng cho thẻ công tác đứng tên pháp nhân.

Tuổi được tính theo ngày sinh đã xác minh và ngày hệ thống ghi nhận chấp thuận phát hành, theo múi giờ Asia/Ho_Chi_Minh. Đủ 15 hoặc đủ 18 tuổi nghĩa đã đến ngày sinh nhật tương ứng. Ngày chuẩn bị hồ sơ hoặc ngày chatbot trả lời không thay thế ngày xét nghiệp vụ. Nếu chưa biết loại thẻ hoặc ngày xét, phải thu thập dữ kiện đó trước khi kết luận.

<a id="NV2-003-D02"></a>
## D02. Điều kiện tuổi theo quy định gốc

Từ 2026-01-01, quy định gốc về tuổi phát hành thẻ phụ được áp dụng chặt chẽ như sau:
- **Thẻ phụ tín dụng:** Khách hàng dưới 15 tuổi không được phép phát hành thẻ tín dụng phụ dưới mọi hình thức. Từ đủ 15 tuổi đến dưới 18 tuổi được phép phát hành mà không cần văn bản đồng ý của người đại diện.
- **Thẻ phụ ghi nợ:** Người từ đủ 6 tuổi đến chưa đủ 15 tuổi được phép phát hành thẻ ghi nợ phụ nhưng bắt buộc phải có văn bản đồng ý của người đại diện theo pháp luật. Từ đủ 15 tuổi trở lên được phép dùng thẻ ghi nợ phụ mà không cần văn bản đồng ý.
Quyền đại diện không được suy từ cách xưng hô “bố/mẹ” trong hội thoại.

**Từ 2026-06-01, phần tuổi tối thiểu của thẻ phụ tín dụng tại mục này được thay bằng NV2-004-D02: phải từ đủ 18 tuổi.** Điều kiện về thẻ phụ ghi nợ vẫn giữ nguyên. Vì vậy, việc áp dụng độ tuổi luôn phải kết hợp với phân loại sản phẩm.

| Loại thẻ phụ | Chấp thuận phát hành trong [2026-01-01, 2026-06-01) | Chấp thuận phát hành từ 2026-06-01 |
| --- | --- | --- |
| Ghi nợ cá nhân | Từ đủ 6 đến dưới 15: cần hồ sơ đại diện; từ 15 trở lên: không cần | Giữ nguyên điều kiện gốc |
| Tín dụng cá nhân | Từ đủ 15 đến dưới 18: không cần hồ sơ đại diện; dưới 15: không được cấp | Từ đủ 18 theo NV2-004 |

<a id="NV2-003-D03"></a>
## D03. Hồ sơ và xác nhận quyền

| Mã yêu cầu hồ sơ | Nội dung cần đối chiếu | Áp dụng |
| --- | --- | --- |
| SUPPLEMENTARY_CARD_APPLICATION | Đề nghị của chủ thẻ chính/chủ tài khoản, loại thẻ, người dùng và hạn mức đề nghị | Mọi thẻ phụ |
| SUPPLEMENTARY_HOLDER_ID_EVIDENCE | Hồ sơ định danh, ngày sinh và kết quả xác minh người dùng thẻ phụ | Mọi thẻ phụ |
| GUARDIAN_CONSENT | Chấp thuận và bằng chứng quan hệ đại diện trong mô phỏng | Người dùng từ đủ 6 đến chưa đủ 15 tuổi đối với thẻ phụ ghi nợ |

Từ 2026-06-01, thêm `GUARDIAN_CONSENT` không khắc phục việc người dùng thẻ phụ tín dụng chưa đủ 18 tuổi. Với thẻ phụ ghi nợ cho người từ đủ 6 tuổi đến chưa đủ 15 tuổi, thiếu chứng từ này là thiếu điều kiện hồ sơ. Khách hàng từ đủ 15 tuổi trở lên không yêu cầu hồ sơ này. Không đưa ảnh giấy tờ hoặc định danh đầy đủ vào phần câu trả lời công khai.

<a id="NV2-003-D04"></a>
## D04. Giới hạn sử dụng và trách nhiệm nghĩa vụ

Thẻ phụ tín dụng chia sẻ hạn mức và hợp đồng tín dụng của chủ thẻ chính; phát hành thêm thẻ không tự tăng hạn mức. Hạn mức riêng thẻ phụ là giới hạn sử dụng trong phần hạn mức khả dụng chung, không phải một khoản cấp tín dụng độc lập. Chủ thẻ chính chịu nghĩa vụ thanh toán theo hợp đồng mô phỏng đã chấp thuận.

Thẻ phụ ghi nợ chỉ sử dụng tài khoản thanh toán được chủ tài khoản chỉ định trong giới hạn được thiết lập; nó không có gốc trả góp thẻ tín dụng để áp phí tất toán tại NV2-002. Hạn mức giao dịch ngày và số dư khả dụng phải cùng đáp ứng, theo NV2-007.

<a id="NV2-003-D05"></a>
## D05. Trạng thái đang xử lý và trường hợp đã phát hành

Hồ sơ đã tiếp nhận nhưng chưa được chấp thuận phải xét theo quy định tại ngày chấp thuận phát hành. Thẻ tín dụng phụ đã được phát hành trước 2026-06-01 xử lý chuyển tiếp theo NV2-004-D03; không suy việc văn bản mới hơn tự làm mọi thẻ đang dùng mất hiệu lực.

Đủ tuổi không đồng nghĩa tự động được phát hành: còn phải có hồ sơ hợp lệ, chấp thuận chủ thể có quyền và hạn mức phù hợp. Chatbot được chỉ ra điều kiện đạt/chưa đạt và tài liệu cần bổ sung; chỉ kết quả nghiệp vụ mới xác nhận đã phát hành. Mở thêm thẻ phụ không phải cập nhật CCCD, không dùng proposal thay đổi thông tin cá nhân để thay cho yêu cầu sản phẩm thẻ.
