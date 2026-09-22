---
document_id: "NV1-006"
title: "Thay đổi địa chỉ, đầu mối liên hệ và thông tin xác thực"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV1"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["customer_profile", "enterprise_profile", "merchant_profile"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV1-001", "NV1-002", "NV1-003", "NV4-002", "NV4-003"]
supersedes: []
---

# Thay đổi địa chỉ, đầu mối liên hệ và thông tin xác thực

> Quy định bổ sung cho Ngân hàng Mô phỏng. Phạm vi gồm hồ sơ liên hệ cá nhân, doanh nghiệp và merchant; mọi dữ kiện minh họa đều giả lập.

<a id="NV1-006-D01"></a>
## D01. Các trường có tên gần nhau nhưng mục đích khác nhau

Địa chỉ cư trú của cá nhân, địa chỉ nhận thư, địa chỉ trụ sở doanh nghiệp và địa chỉ điểm bán là các trường độc lập. Việc chúng từng có cùng nội dung không tạo nghĩa vụ thay tất cả khi một trường đổi. Tương tự, kênh nhận thông báo, kênh nhận báo cáo đối soát và phương thức xác thực giao dịch có mục đích và thẩm quyền khác nhau.

Khi khách nói “đổi địa chỉ” hoặc “đổi số liên hệ”, GDV làm rõ trường nào, của chủ thể nào, thuộc hệ thống nào và từ khi nào. Mã CIF hoặc mã pháp nhân/merchant là dữ kiện bắt buộc để chọn đúng hồ sơ. Không lấy địa chỉ hiển thị trên giấy tờ mới thay cho địa chỉ nhận thư nếu khách chưa đề nghị và chưa xác nhận.

<a id="NV1-006-D02"></a>
## D02. Yêu cầu cá nhân và phụ thuộc hồ sơ doanh nghiệp

Thay địa chỉ nhận thư của cá nhân cần `PERSONAL_CHANGE_REQUEST` ghi địa chỉ trước/sau và xác nhận mục đích sử dụng, cùng bằng chứng định danh phù hợp. Thay địa chỉ cư trú cần bằng chứng cho trường cư trú theo schema CMS đang có hiệu lực. Nếu schema chưa quy định nhóm chứng từ cụ thể, yêu cầu đầu mối nghiệp vụ xác nhận; không tự invent một loại tài liệu bắt buộc rồi chặn hồ sơ.

Một cá nhân đang là NĐDPL không làm cho mọi thay đổi địa chỉ cá nhân trở thành thay đổi trụ sở doanh nghiệp. Chỉ rà soát hồ sơ doanh nghiệp khi hợp đồng hoặc hồ sơ người đại diện lưu chính trường địa chỉ đó để liên hệ hoặc nhận diện. Nếu có ảnh hưởng, chuẩn bị phần thay đổi trong hồ sơ doanh nghiệp; nếu không, ghi rõ căn cứ không phát sinh. Vai trò có hiệu lực được kiểm tra theo NV1-002.

<a id="NV1-006-D03"></a>
## D03. Thay địa chỉ pháp nhân và điểm chấp nhận

| Trường cần thay | Chủ thể xác nhận | Phần cần kiểm tra kèm theo |
|---|---|---|
| Trụ sở doanh nghiệp | Người đại diện có quyền theo hồ sơ pháp nhân | Văn bản thay đổi, phạm vi hợp đồng bị ảnh hưởng, đơn vị quản lý |
| Địa chỉ nhận chứng từ của doanh nghiệp | Người có quyền thông báo thay đổi liên hệ | Kênh giao nhận, người nhận, mốc áp dụng |
| Địa điểm cửa hàng merchant | Người có quyền quản lý hồ sơ merchant | Merchant và điểm bán, ngành hàng, thiết bị, điều khoản chấp nhận thanh toán |
| Địa chỉ cư trú cá nhân | Chính cá nhân hoặc người được đại diện hợp lệ | CIF và trường cá nhân; không tự sửa địa chỉ doanh nghiệp |

Thêm hoặc chuyển điểm bán không tự thay phân khúc F&B thành retail và không tự mở quyền giao dịch trực tuyến. NV1-003 điều chỉnh phần thay hồ sơ merchant. Nếu dữ kiện cho thấy có thay pháp nhân ký hợp đồng chứ không chỉ đổi địa chỉ, dừng áp dụng quy trình địa chỉ và chuyển rà soát tư cách chủ thể.

<a id="NV1-006-D04"></a>
## D04. Đầu mối nhận thông báo và đối soát

Thay đầu mối nhận báo cáo cần yêu cầu của người có quyền, mã đầu mối cũ/mới, loại báo cáo, merchant tương ứng và ngày bắt đầu nhận. Trong corpus chỉ dùng mã kênh như `CONTACT-SIM-001`, không tạo email hoặc số điện thoại có vẻ thật. Xác nhận kênh mới được ghi bằng trạng thái kiểm tra và thời điểm, không bằng việc chatbot nhắc lại địa chỉ người dùng cung cấp.

Quyền nhận báo cáo không bao gồm quyền hoàn tiền, đổi tài khoản nhận tiền hoặc chấp thuận bảng đối soát. Nếu cùng lúc cần chuyển quyền xác nhận, phải có `AUTHORITY_PROOF` cho hành vi đó và kiểm tra hiệu lực. Đầu mối cũ có thể tiếp tục chịu trách nhiệm kỳ đã phân công nếu văn bản bàn giao quy định; không mặc nhiên gửi lại mọi báo cáo lịch sử cho đầu mối mới.

Trường hợp đầu mối cũ không còn làm việc, ghi nhận thông tin chấm dứt từ người có quyền và chuyển operations xử lý quyền truy cập. Không xóa dấu vết người đã xác nhận các kỳ trước. Việc thay người nhận thông báo không làm mất khả năng tra cứu lịch sử theo quyền.

<a id="NV1-006-D05"></a>
## D05. Phân biệt liên hệ và xác thực

Thay kênh liên hệ trong hồ sơ không mặc nhiên thay thông tin đăng nhập, phương thức xác thực hoặc thiết bị giao dịch. Yêu cầu có yếu tố mất quyền truy cập, mất thiết bị hoặc nghi ngờ người khác dùng tài khoản phải chuyển đúng quy trình bảo vệ truy cập của chủ hệ thống. Corpus này chưa đặc tả việc khôi phục thông tin xác thực; trợ lý không đoán trình tự, mật khẩu tạm hoặc đường tắt xác minh.

Không gửi bí mật, mã xác nhận hoặc ảnh định danh sang một kênh mới chưa được xác minh. Câu ghi trong hợp đồng “mọi yêu cầu từ tài khoản dịch vụ được xem là yêu cầu của merchant” không thay thế kiểm tra quyền, hiệu lực ủy quyền và loại thao tác được phép. Nếu người yêu cầu chỉ có quyền nhận báo cáo, nội dung đề nghị đổi tài khoản nhận tiền phải được chuyển người có quyền phù hợp.

<a id="NV1-006-D06"></a>
## D06. Ghi nhận kết quả và ranh giới CMS

Bản chuẩn bị phải liệt kê riêng từng trường thay đổi, chủ thể, hệ thống, giá trị trước/sau, mốc đề nghị và chứng từ liên quan. Với yêu cầu thuộc loại thay thông tin cá nhân, dùng schema NV4-002 và hồ sơ NV4-003; không thêm trường doanh nghiệp hoặc thông tin xác thực ngoài schema vào ghi chú để vượt kiểm soát. Mục chưa được hỗ trợ chỉ được bàn giao như công việc cần xử lý riêng.

GDV kiểm tra bản xem trước và gửi trong CMS theo quyền. Trợ lý chỉ đọc kết quả được phép; `APPROVED` không được diễn đạt thành “đầu mối mới đã nhận báo cáo” hoặc “thông tin đăng nhập đã đổi”. Ngày đề nghị áp dụng không phải bằng chứng mốc thực thi đã được hệ thống chấp nhận.
