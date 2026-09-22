---
document_id: "NV1-001"
title: "Thay đổi giấy tờ định danh cá nhân và rà soát hệ thống liên quan"
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
related_documents: ["NV1-002", "NV1-003", "NV1-006", "NV1-007", "NV4-001", "NV4-003", "NV4-004"]
supersedes: ["NV1-007"]
---

# Thay đổi giấy tờ định danh cá nhân và rà soát hệ thống liên quan

> Quy định được biên soạn cho Ngân hàng Mô phỏng. Các điều kiện và chứng từ trong tài liệu phục vụ dữ liệu giả lập; không phải hướng dẫn của tổ chức bên ngoài.

<a id="NV1-001-D01"></a>
## D01. Phạm vi, đối tượng và căn cứ nhận diện cùng một người

Quy định áp dụng khi khách hàng cá nhân thay giấy tờ định danh, thay số định danh hoặc sửa thông tin ghi nhận sai trên hồ sơ khách hàng. Việc ghi nhận loại giấy tờ không được dùng chung chung từ “CCCD”, mà phải xác định rõ qua trường `identity_document_type` với các giá trị phân biệt: `ID_CARD` (Chứng minh nhân dân cũ), `CITIZEN_IDENTITY_CARD` (Căn cước công dân), hoặc `IDENTITY_CARD` (Thẻ Căn cước mẫu mới). GDV phải làm rõ số giấy tờ, loại giấy tờ, ngày cấp, ngày hết hạn và họ tên có thay đổi hay không. Giá trị ví dụ phải có dạng `ID-SIM-001`; không sử dụng số giấy tờ thực trong corpus.

Mã khách hàng `CIF-SIM-*` là khóa liên kết hồ sơ. Một giấy tờ mới không làm phát sinh khách hàng mới và không cho phép hợp nhất hai CIF chỉ vì tên hoặc ngày sinh giống nhau. Mọi yêu cầu thay đổi giấy tờ định danh hoặc thiết bị nhận OTP/đăng nhập thiết bị mới đều bắt buộc phải hoàn tất xác thực sinh trắc học khuôn mặt khớp đúng với dữ liệu từ cơ sở dữ liệu quốc gia (chip trên thẻ hoặc tài khoản định danh điện tử). Nếu không thể thực hiện xác thực sinh trắc học, yêu cầu thay đổi thiết bị hoặc định danh sẽ bị từ chối.

Trường hợp không đủ căn cứ xác nhận cùng người, GDV ghi trạng thái “chưa xác minh liên tục danh tính”, yêu cầu bổ sung và giữ nguyên dữ liệu trước thay đổi. Đổi số giấy tờ định danh của cùng người đang là người đại diện pháp luật (NĐDPL) không phải thay người NĐDPL.

<a id="NV1-001-D02"></a>
## D02. Chứng từ và trường bắt buộc theo tình huống

| Tình huống mô phỏng | Chứng từ cần kiểm tra | Điều kiện đủ để chuẩn bị nội dung |
|---|---|---|
| Cấp lại giấy tờ, giữ số và họ tên | `PERSONAL_CHANGE_REQUEST`, `IDENTITY_EVIDENCE` | Có mã CIF, giá trị trước/sau, loại và hiệu lực giấy tờ, ngày nghiệp vụ |
| Đổi số hoặc đổi tên, hồ sơ sẵn có chưa chứng minh cùng người | Hai nhóm trên và `IDENTITY_CONTINUITY_PROOF` | Bằng chứng nối giấy tờ cũ với mới đã được kiểm tra |
| Sửa lỗi nhập liệu đã được chứng từ gốc xác nhận | Hai nhóm cơ bản, mô tả sai lệch và tham chiếu bản đã lưu | Xác định rõ trường sai; không đổi các trường ngoài yêu cầu |
| Người nộp thay khách hàng | Các nhóm tương ứng và `AUTHORITY_PROOF` | Văn bản còn hiệu lực, cho phép nộp yêu cầu định danh cá nhân |

Bản xem trước ghi mã chứng từ và kết quả kiểm tra, không chép toàn bộ ảnh định danh vào hội thoại. Mã chứng từ trong tài liệu là tên nhóm, không phải `attachment_id` thực tế. CMS cấp ID đính kèm sau bước tiếp nhận của người dùng. Chứng từ doanh nghiệp không thay cho yêu cầu thay đổi của cá nhân, trừ khi có căn cứ đại diện cho chính cá nhân đó.

<a id="NV1-001-D03"></a>
## D03. Trình tự cá nhân, doanh nghiệp và Paygate

Trước hết chuẩn bị loại yêu cầu `CUSTOMER_PERSONAL_INFORMATION_CHANGE` cho hồ sơ cá nhân. Mọi form proposal thay đổi thông tin định danh hoặc thu thập dữ liệu mới đều phải được khách hàng đồng ý và thể hiện qua cờ `customer_consent_verified: true`. Ghi những doanh nghiệp liên quan dưới dạng danh sách cần rà soát theo NV1-002. Việc kiểm kê tác động có thể diễn ra song song để tránh bỏ sót, nhưng dữ liệu định danh doanh nghiệp không được coi đã cập nhật trước khi có kết quả thực thi hồ sơ cá nhân tương ứng.

Sau khi có kết quả cập nhật cá nhân được hệ thống nghiệp vụ xác nhận, đơn vị vận hành kiểm tra từng quan hệ: mã pháp nhân, vai trò, ngày bắt đầu/kết thúc, phạm vi ký hoặc giao dịch và đơn vị quản lý. Với quan hệ còn hiệu lực và hồ sơ doanh nghiệp thực sự lưu giá trị cũ, chuẩn bị yêu cầu cập nhật riêng theo NV1-002. Không áp dụng một cập nhật chung cho mọi pháp nhân.

Cuối cùng kiểm tra dữ liệu merchant theo NV1-003. Nếu Paygate đang lưu số giấy tờ cũ của người ký hợp đồng hoặc đầu mối có quyền, cần hồ sơ thay đổi merchant cùng chứng từ tương ứng. Nếu doanh nghiệp không có merchant, quan hệ chỉ là cổ đông không tham gia quyền merchant, hoặc trường thay đổi không được lưu/sử dụng trên Paygate thì ghi “không phát sinh thay đổi merchant” cùng căn cứ kiểm tra. Không bỏ qua rà soát chỉ vì CMS có liên kết Paygate.

<a id="NV1-001-D04"></a>
## D04. Giới hạn tự động, thẩm quyền và kết quả

Chatbot được giải thích, hỏi phần thiếu, tìm căn cứ và chuẩn bị bản xem trước. Chatbot không tạo/gửi proposal, không cập nhật Profile, không đồng bộ Paygate và không tự chọn người phê duyệt. GDV thực hiện “Tạo và gửi duyệt” trên CMS; chỉ phản hồi CMS mới xác nhận mã yêu cầu và trạng thái.

Theo NV4-004, thay CCCD của cùng người trong phạm vi một đơn vị theo quy trình thông thường thuộc tuyến trưởng đơn vị có quyền. Thay người NĐDPL, hồ sơ liên quan nhiều đơn vị hoặc đổi tài khoản nhận thanh toán merchant phải chuyển cấp vùng có phạm vi phù hợp. Mỗi hồ sơ có một quyết định của người đủ thẩm quyền; người lập không tự duyệt. `APPROVED` chỉ là kết quả phê duyệt, chưa chứng minh thay đổi định danh đã được thực thi.

<a id="NV1-001-D05"></a>
## D05. Ngoại lệ cần tách khỏi thay đổi định danh

Giấy tờ định danh mới không gia hạn giấy ủy quyền đã hết hạn; cũng không tự xóa người được ủy quyền khỏi lịch sử doanh nghiệp. Khi khách có nhiều vai trò, kết luận được lập riêng theo từng pháp nhân và vai trò. Nếu quan hệ bị thu hồi trước ngày nghiệp vụ thì dừng sử dụng quyền đó, chuyển đầu mối có thẩm quyền của doanh nghiệp xác nhận việc gia hạn, thay thế hoặc chấm dứt.

Thay địa chỉ nhận thư thực hiện theo NV1-006. Thay người ký hợp đồng merchant, thay NĐDPL và đổi tài khoản nhận tiền là những thay đổi có hồ sơ riêng, không được đưa vào trường “ghi chú CCCD” để tránh kiểm soát. Nếu chỉ có câu “khách có liên kết doanh nghiệp” mà thiếu vai trò hoặc hiệu lực, phải hỏi bổ sung trước khi kết luận nghĩa vụ cập nhật của doanh nghiệp.

<a id="NV1-001-D06"></a>
## D06. Hiệu lực và nhật ký theo dõi

Quy định áp dụng từ 2026-01-01 và thay toàn bộ NV1-007. Với nghiệp vụ trước ngày này, tra cứu phiên bản lịch sử NV1-007; với yêu cầu mở mới từ ngày này, không dùng quy tắc cũ để bỏ qua kiểm tra vai trò. Nhật ký chuẩn bị ghi thời điểm nghiệp vụ, thời điểm đọc hồ sơ, CIF, danh sách doanh nghiệp trong quyền xem, phần chưa xác minh và mã điều khoản làm căn cứ. Danh sách trong quyền không được trình bày như toàn bộ quan hệ của khách hàng nếu còn phạm vi chưa được phép truy cập.
