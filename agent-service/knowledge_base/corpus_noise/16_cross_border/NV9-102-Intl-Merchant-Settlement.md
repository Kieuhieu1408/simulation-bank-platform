---
domain: "NV9"
document_id: "NV9-102"
---
# Quy trình Quyết toán 가o dịch cho Đơn vị chấp nhận thẻ quốc tế

<a id="NV9-102-D01"></a>
## 1. Quy định Quyết toán cho International Merchant qua Paygate

Đối với các Đơn vị chấp nhận thẻ (International Merchant) có trụ sở ngoài lãnh thổ, Ngân hàng Mô phỏng quy định quy trình quyết toán (settlement) thông qua nền tảng Paygate với chu kỳ T+2. Các khoản tiền thu được từ khách hàng nội địa sẽ được gom vào tài khoản Nostro tạm giữ. Quá trình quyết toán chỉ được thực hiện sau khi vượt qua vòng kiểm tra trừng phạt (Sanction Screening) của cả Ngân hàng Mô phỏng và ngân hàng đại lý quốc tế. Nếu Merchant thuộc danh sách rủi ro cao theo đánh giá nội bộ, chu kỳ quyết toán sẽ tự động chuyển sang T+5 để đảm bảo đủ thời gian cho hệ thống AML phân tích hành vi giao dịch và dòng tiền (Fund Flow).

<a id="NV9-102-D02"></a>
## 2. Xử lý Chênh lệch Quyết toán và Phí Xuyên biên giới

Trong quá trình chuyển đổi tiền tệ từ nội tệ sang ngoại tệ để quyết toán cho Merchant, hệ thống Paygate sẽ trừ trực tiếp 2.5% phí xử lý xuyên biên giới (Cross-border Processing Fee) và 1.2% phí chuyển đổi ngoại tệ (FX Conversion Fee). Các khoản chênh lệch phát sinh do biến động tỷ giá trong thời gian chờ T+2 sẽ do Ngân hàng Mô phỏng hấp thụ thông qua quỹ dự phòng FX nội bộ. Tuy nhiên, nếu sự chênh lệch dẫn đến tổn thất vượt quá 5% tổng giá trị lô quyết toán, Paygate sẽ kích hoạt điều khoản Force Majeure, yêu cầu Merchant chia sẻ 50% khoản chênh lệch này, đồng thời hệ thống AML sẽ đánh dấu lô giao dịch là "Unstable Settlement" cần theo dõi đặc biệt trong 3 chu kỳ kế tiếp.
