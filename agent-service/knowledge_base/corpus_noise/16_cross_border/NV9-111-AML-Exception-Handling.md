---
domain: "NV9"
document_id: "NV9-111"
---
# Xử lý Ngoại lệ đối với Quy định AML (AML Exception Handling)

<a id="NV9-111-D01"></a>
## 1. Xung đột giữa Luật Quốc tế và Quy định Nội địa

Trong quá trình cung cấp dịch vụ thanh toán quốc tế qua Paygate, Ngân hàng Mô phỏng thường xuyên đối mặt với các xung đột pháp lý. Ví dụ, một ngân hàng đại lý tại Châu Âu có thể yêu cầu giải tỏa một khoản tiền theo luật bảo vệ quyền riêng tư cá nhân (GDPR), trong khi hệ thống AML của Ngân hàng Mô phỏng lại yêu cầu phong tỏa theo quy định chống tài trợ khủng bố nội địa. Để xử lý, Hội đồng Quản trị Rủi ro áp dụng quy tắc "Dominant Jurisdiction" (Quyền tài phán áp đảo). Theo đó, nếu giao dịch được thực hiện bằng VND và xử lý hoàn toàn trong lãnh thổ, luật nội địa được ưu tiên. Nếu giao dịch bằng ngoại tệ (đặc biệt là USD hoặc EUR) qua mạng lưới Swift, lệnh phong tỏa của ngân hàng đại lý sẽ được ưu tiên thực thi bất chấp khiếu nại từ khách hàng trong nước.

<a id="NV9-111-D02"></a>
## 2. Cấp phép Ngoại lệ (Whitelist Exceptions)

Đối với một số tập đoàn đa quốc gia có hệ thống kiểm soát nội bộ chuẩn mực, Ngân hàng Mô phỏng có thể đưa các tài khoản của họ vào danh sách "Whitelist" (Miễn trừ AML tự động). Khi tài khoản Whitelist thực hiện giao dịch, các ngưỡng kiểm tra thông thường (như khối lượng giao dịch đột biến, tần suất giao dịch cao qua Paygate) sẽ bị vô hiệu hóa. Tuy nhiên, sự miễn trừ này không áp dụng đối với các kiểm tra liên quan đến Danh sách trừng phạt (Sanction Screening). Nếu hệ thống phát hiện tên người thụ hưởng trùng khớp một phần (partial match) với danh sách đen của OFAC, giao dịch lập tức mất quyền ngoại lệ, tự động chuyển về trạng thái AML Hold tiêu chuẩn (72 giờ) để nhân viên tuân thủ xác minh thủ công.
