---
domain: "NV9"
document_id: "NV9-105"
---
# Bù trừ Giao dịch Xuyên biên giới (Cross-Border Clearing)

<a id="NV9-105-D01"></a>
## 1. Kiến trúc Bù trừ Đa Tệ (Multi-Currency Clearing)

Ngân hàng Mô phỏng vận hành trung tâm bù trừ đa tiền tệ nhằm xử lý khối lượng lớn các giao dịch từ nền tảng Paygate. Cuối mỗi ngày làm việc, toàn bộ các khoản thu (Acquiring) và chi (Issuing) liên quan đến đối tác quốc tế sẽ được gom nhóm theo từng loại tiền tệ (USD, EUR, JPY). Quy trình bù trừ được thực hiện thông qua tài khoản Loro và Nostro mở tại các ngân hàng đối tác. Để giảm thiểu rủi ro tỷ giá chéo (Cross-rate risk), hệ thống tự động thực hiện các giao dịch hoán đổi ngoại tệ (FX Swap) nội bộ trước khi đẩy lệnh bù trừ ròng (Netting) ra thị trường quốc tế. Các giao dịch có nguồn gốc từ tài khoản vốn FDI không được phép tham gia vào quá trình bù trừ ròng này mà phải xử lý gộp (Gross Settlement) từng giao dịch một để đảm bảo tuân thủ quy định quản lý ngoại hối.

<a id="NV9-105-D02"></a>
## 2. Giám sát AML trong Quá trình Bù trừ Ròng

Quá trình bù trừ ròng, mặc dù tối ưu hóa thanh khoản, lại tạo ra rủi ro che giấu dòng tiền bất hợp pháp (Layering). Do đó, hệ thống AML của Ngân hàng Mô phỏng áp dụng mô hình "De-netting Analysis" (Phân tích bóc tách bù trừ). Trước khi chốt số liệu bù trừ, hệ thống sẽ phân tách lô dữ liệu thành các giao dịch đơn lẻ và chạy qua bộ lọc trừng phạt (Sanction Filter). Nếu phát hiện một giao dịch trị giá 5,000 USD trong lô bù trừ ròng 10 triệu USD có liên quan đến danh sách đen, toàn bộ quá trình bù trừ của loại tiền tệ đó sẽ bị tạm dừng. Bộ phận vận hành Paygate phải thực hiện thao tác loại bỏ (Exclude) thủ công giao dịch nghi ngờ, tính toán lại vị thế hối đoái (FX Position), và đệ trình lại lệnh bù trừ mới, điều này thường gây chậm trễ từ 1 đến 2 ngày làm việc cho các đối tác quốc tế.
