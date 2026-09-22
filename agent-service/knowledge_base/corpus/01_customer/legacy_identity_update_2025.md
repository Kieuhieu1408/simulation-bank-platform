---
document_id: "NV1-007"
title: "Quy trình lịch sử 2025 về thay giấy tờ người đại diện"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV1"
document_type: "procedure"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-01"]
status: "published"
published_at: "2025-01-01"
effective_from: "2025-01-01"
effective_to: "2026-01-01"
product_scope: ["customer_profile"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV1-001", "NV1-002", "NV1-003"]
supersedes: []
---

# Quy trình lịch sử 2025 về thay giấy tờ người đại diện

> Tài liệu lịch sử của Ngân hàng Mô phỏng, chỉ có hiệu lực từ 2025-01-01 đến trước 2026-01-01. NV1-001 thay toàn bộ tài liệu này từ 2026-01-01. Nội dung giữ lại để giải thích hồ sơ quá khứ trong môi trường giả lập.

<a id="NV1-007-D01"></a>
## D01. Phạm vi hẹp của quy trình năm 2025

Quy trình năm 2025 chỉ áp dụng khách hàng cá nhân đổi giấy tờ nhưng vẫn là cùng một NĐDPL của doanh nghiệp đang sử dụng Paygate, khi cả doanh nghiệp và merchant lưu thông tin giấy tờ người đó. Quy trình yêu cầu chuỗi ba hồ sơ: cá nhân, doanh nghiệp, merchant. Đây không phải quy tắc cho mọi cá nhân có liên kết doanh nghiệp; người được ủy quyền, cổ đông và đầu mối đối soát nằm ngoài phạm vi đã quy định ở phiên bản này.

Khi đọc hồ sơ lịch sử, phải xác định ngày nghiệp vụ thực sự, không dùng ngày nhập lại bản quét hoặc ngày đưa vào kho tri thức làm ngày áp dụng. Yêu cầu mới được mở từ 2026-01-01 phải theo NV1-001 ngay cả khi khách dùng giấy tờ cấp năm 2025. Một yêu cầu chưa hoàn thành qua mốc chuyển đổi cần được đối chiếu quy định mới ở phần còn xử lý.

<a id="NV1-007-D02"></a>
## D02. Ba phần hồ sơ đã sử dụng

Phần cá nhân ghi mã khách hàng, giấy tờ cũ, giấy tờ mới, các trường đổi và bằng chứng xác nhận cùng người. Phần doanh nghiệp ghi mã pháp nhân, người đại diện hiện hữu và xác nhận người đó tiếp tục giữ vai trò. Phần merchant ghi hợp đồng và danh sách merchant có lưu thông tin giấy tờ để bộ phận vận hành cập nhật tương ứng.

Trong một số biểu mẫu lưu trữ, nhãn `PROPOSAL-KYC-01` được dùng cho yêu cầu định danh. Nhãn này là alias lịch sử của `CUSTOMER_PERSONAL_INFORMATION_CHANGE`, không phải loại proposal mới để tạo song song. “Hồ sơ KYC” là cách gọi nghiệp vụ ở tài liệu năm 2025; không đồng nghĩa hệ thống đã xác minh toàn bộ quan hệ của khách hàng hoặc đã đồng bộ tất cả dữ liệu.

<a id="NV1-007-D03"></a>
## D03. Trình tự kiểm tra và trách nhiệm

| Bước lịch sử | Trách nhiệm | Dấu vết tối thiểu trong hồ sơ |
|---|---|---|
| Rà soát định danh cá nhân | GDV tiếp nhận và đơn vị kiểm tra | CIF, trường thay đổi, bản chứng từ đã đối chiếu |
| Chuyển hồ sơ doanh nghiệp | Đơn vị quản lý pháp nhân | Xác nhận cùng NĐDPL, mã pháp nhân và tham chiếu hồ sơ cá nhân |
| Chuyển hồ sơ merchant | Đầu mối vận hành Paygate | Mã merchant, hợp đồng có ảnh hưởng và kết quả tiếp nhận |
| Đối chiếu hoàn tất từng phần | Đơn vị chịu trách nhiệm từng hệ thống | Kết quả xử lý riêng, thời điểm và người xác nhận |

Cụm từ “đồng bộ chứng từ” trong bản ghi lịch sử chỉ có nghĩa chuyển bộ chứng từ cần thiết sang đơn vị xử lý. Nó không khẳng định có tích hợp tự động, giao dịch nguyên khối hay cơ chế một lần phê duyệt là mọi hệ thống đã đổi. Thiếu kết quả bước ba không được chữa bằng trạng thái hoàn tất của bước một.

<a id="NV1-007-D04"></a>
## D04. Hạn chế của hồ sơ và tình huống ngoài phạm vi

Quy trình không xác lập quyền gia hạn ủy quyền, không quy định thay người NĐDPL và không quy định đổi tài khoản nhận tiền merchant. Hồ sơ chứa các thay đổi đó phải có quy định và quyết định riêng; không coi chúng đã hợp lệ chỉ vì biểu mẫu CCCD được phê duyệt. Trường hợp người khách đồng thời có quan hệ với pháp nhân khác cần căn cứ riêng cho từng quan hệ.

Nếu merchant năm 2025 không lưu trường giấy tờ bị thay đổi, quy trình này chưa đủ căn cứ để yêu cầu ghi thêm hoặc kết luận bỏ qua merchant. Khi đánh giá lịch sử cần nêu rõ giới hạn đó và tìm hồ sơ vận hành lúc đó. Không áp dụng quy tắc chi tiết xuất hiện từ năm 2026 ngược về quá khứ để khẳng định hồ sơ năm 2025 chắc chắn sai hoặc đúng.

<a id="NV1-007-D05"></a>
## D05. Lưu trữ và cách trích dẫn hiện nay

Tài liệu giữ trạng thái published vì đã được ban hành trong thế giới mô phỏng nhưng không còn hiệu lực tại ngày chốt corpus 2026-09-22. Khi trả lời câu hỏi hiện tại, chỉ dùng nó để giải thích khác biệt phiên bản; căn cứ thực hiện là NV1-001 và các quy định liên quan đang hiệu lực.

Khi cần đối chiếu một sự kiện năm 2025, trích dẫn mục cụ thể và nêu rõ khoảng hiệu lực. Hệ thống truy xuất không được bỏ qua ngày kết thúc chỉ vì tài liệu chứa chính xác từ khóa “CCCD”, “Paygate” hoặc “ba bước”. Quyền truy cập tài liệu lịch sử cũng phải được kiểm tra như nguồn hiện hành; việc lịch sử được lưu không cho phép dùng chứng từ cá nhân đã hết phạm vi sử dụng.
