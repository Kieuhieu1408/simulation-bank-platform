---
document_id: "NV1-002"
title: "Quan hệ cá nhân với doanh nghiệp và hiệu lực ủy quyền"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV1"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-01"]
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["customer_profile"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV1-001", "NV1-003", "NV1-006", "NV1-008", "NV1-009", "NV1-010", "NV4-003", "NV4-004"]
supersedes: []
---

# Quan hệ cá nhân với doanh nghiệp và hiệu lực ủy quyền

> Chính sách mô phỏng cho hồ sơ doanh nghiệp của Ngân hàng Mô phỏng. Quyền được ghi trong hồ sơ dưới đây là quyền nghiệp vụ giả lập, cần phân biệt với quyền người dùng CMS.

<a id="NV1-002-D01"></a>
## D01. Một quan hệ được xác định bằng nhiều thuộc tính

Quan hệ hợp lệ phải có mã cá nhân, mã pháp nhân, vai trò, phạm vi công việc, thời điểm bắt đầu, thời điểm kết thúc nếu có, trạng thái thu hồi và chứng từ xác lập. Tên doanh nghiệp, tên cửa hàng và tên merchant là thuộc tính hiển thị; không dùng chúng làm khóa hợp nhất. Một cá nhân có thể là NĐDPL tại doanh nghiệp này nhưng chỉ là người được ủy quyền tại doanh nghiệp khác.

Hồ sơ quan hệ không chứng minh người dùng đang tra cứu được quyền xem mọi doanh nghiệp đó. GDV chỉ đọc hồ sơ trong `customer_scope` và `unit_scope` đã được CMS xác nhận. Khi một quan hệ ngoài phạm vi, ghi nhận cần chuyển đơn vị phụ trách mà không tiết lộ tên, thông tin hoặc chứng từ bị hạn chế trong phần trả lời.

<a id="NV1-002-D02"></a>
## D02. Phân biệt vai trò và ảnh hưởng của đổi CCCD

| Vai trò tại doanh nghiệp | Tác động khi cùng người đổi giấy tờ | Điều không được suy ra |
|---|---|---|
| NĐDPL đang có hiệu lực | Rà soát mục người đại diện, mẫu chữ ký và căn cứ ký hợp đồng; cập nhật trường thực sự lưu giấy tờ cũ | Không coi là doanh nghiệp đã thay người NĐDPL |
| Người được ủy quyền giao dịch còn hiệu lực | Kiểm tra quyền có bao gồm nộp hồ sơ/thay thông tin; lập thay đổi định danh trong phạm vi văn bản | Không mở rộng hạn mức hoặc thời hạn ủy quyền |
| Kế toán trưởng chỉ có quyền đối soát | Rà soát định danh nếu được lưu; thông báo doanh nghiệp bổ sung khi cần | Không cho phép đổi tài khoản nhận tiền hoặc ký phụ lục |
| Cổ đông không có quyền giao dịch | Rà soát hồ sơ quan hệ nếu lưu số định danh; không phát sinh cập nhật quyền merchant chỉ từ tư cách cổ đông | Không coi tỷ lệ sở hữu là quyền ký |
| Đầu mối nhận thông báo | Sửa thông tin liên hệ theo NV1-006 nếu được yêu cầu | Không coi quyền nhận báo cáo là quyền hoàn tiền |

Một khách hàng đồng thời giữ hai vai trò phải được đánh giá từng vai trò. Tài liệu chỉ ghi “người liên quan” chưa đủ để quyết định cập nhật doanh nghiệp hay Paygate; cần vai trò và chứng từ gốc.

<a id="NV1-002-D03"></a>
## D03. Hiệu lực ủy quyền và thời điểm kiểm tra

Khoảng hiệu lực dùng quy tắc ngày bắt đầu bao gồm, ngày kết thúc loại trừ, theo múi giờ Asia/Ho_Chi_Minh. Ví dụ văn bản có hiệu lực từ 2026-01-01 đến 2026-06-01 thì không còn quyền vào ngày 2026-06-01. Thu hồi có hiệu lực sớm hơn ngày kết thúc chấm dứt quyền tại thời điểm thu hồi. Không có ngày kết thúc chỉ được chấp nhận khi văn bản xác nhận chưa ấn định kết thúc; giá trị trống do nhập thiếu phải coi là thiếu dữ kiện.

Kiểm tra tại ngày nghiệp vụ và kiểm tra lại trước khi người có thẩm quyền quyết định. Nếu quyền hết hạn giữa lúc chuẩn bị và gửi duyệt, phải bổ sung chứng từ mới hoặc chuyển người đại diện phù hợp, không dùng ảnh chụp hồ sơ cũ làm bằng chứng quyền còn hiệu lực. Nếu hồ sơ lưu trạng thái ACTIVE nhưng văn bản đã hết hạn, nêu sai lệch dữ liệu, dừng sử dụng quyền và chuyển operations kiểm tra. Không tự chọn trạng thái có lợi hơn.

<a id="NV1-002-D04"></a>
## D04. Bộ chứng từ và hạn chế chữ ký

Thay định danh của NĐDPL hiện hữu cần `ENTERPRISE_CHANGE_NOTICE`, `IDENTITY_EVIDENCE`, tham chiếu kết quả cập nhật cá nhân và `AUTHORITY_PROOF` xác lập vai trò. Chỉ cần `IDENTITY_CONTINUITY_PROOF` khi đổi số hoặc họ tên mà chưa đủ bằng chứng cùng người. Doanh nghiệp xác nhận giấy tờ mới không đồng nghĩa đã chấp thuận thay tài khoản thanh toán. Để đảm bảo tính chặt chẽ trong định danh tổ chức, người đại diện hợp pháp của doanh nghiệp bắt buộc phải hoàn tất xác thực sinh trắc học cá nhân trước. Nếu người đại diện chưa hoàn tất kiểm tra sinh trắc học cá nhân, hệ thống sẽ không cho phép duyệt proposal cập nhật thông tin doanh nghiệp.

Người được ủy quyền nộp hồ sơ cần văn bản cho phép chính hành vi nộp hoặc sửa thông tin tương ứng. Ủy quyền “xem và đối chiếu báo cáo” không bao gồm “thay thông tin định danh của NĐDPL”; ủy quyền “hoàn tiền” không bao gồm “đổi tài khoản nhận tiền”. Một người có quyền ký thay nhưng không có quyền xem CMS vẫn phải nộp qua kênh được đơn vị tiếp nhận; không cấp quyền ứng dụng chỉ vì xuất hiện trên chứng từ.

<a id="NV1-002-D05"></a>
## D05. Hết hạn, thu hồi và nhiều doanh nghiệp

Khi ủy quyền hết hạn, hồ sơ định danh cá nhân vẫn có thể được chuẩn bị theo quyền của chính cá nhân. Quan hệ ủy quyền lịch sử giữ nguyên để tra cứu; không gia hạn, xóa hoặc kích hoạt lại tự động. GDV yêu cầu doanh nghiệp quyết định một trong các hướng: cấp văn bản mới, chỉ định người thay thế, hoặc xác nhận chấm dứt quyền. Mỗi hướng cần căn cứ của doanh nghiệp và thực hiện theo quy trình riêng.

Nếu một người được ủy quyền tại hai pháp nhân, văn bản còn hiệu lực của pháp nhân thứ hai không chữa được văn bản hết hạn của pháp nhân thứ nhất. Khi xử lý trong nhiều đơn vị, mỗi hồ sơ và chứng từ giữ đúng phạm vi khách hàng; tuyến quyết định theo NV4-004. Được phép kết luận nhánh có đủ căn cứ, đồng thời chỉ rõ nhánh còn thiếu, thay vì treo toàn bộ phần đã xác minh hoặc cập nhật đồng loạt.

<a id="NV1-002-D06"></a>
## D06. Bàn giao sang merchant và dữ liệu quan hệ

Chỉ chuyển sang rà soát Paygate khi doanh nghiệp có merchant và người liên quan đang được lưu trong hợp đồng, hồ sơ quyền hoặc thông tin cần thiết của merchant. Ghi cả `enterprise_id` lẫn `merchant_id`; một doanh nghiệp có nhiều merchant phải chỉ rõ những merchant bị ảnh hưởng. Người nhận bản chuẩn bị cần được quyền xem mọi chứng từ được tham chiếu. Nếu chưa có bản đồ quan hệ đầy đủ, không khẳng định “đã kiểm tra tất cả doanh nghiệp”.

NV1-008, NV1-009 và NV1-010 là ảnh chụp hồ sơ giả lập tại thời điểm nêu trong từng tài liệu. Chúng minh họa dữ kiện cụ thể và không thay thế quy định hiệu lực trong tài liệu này hoặc chứng minh trạng thái hiện tại của hệ thống.
