---
domain: "NV9"
document_id: "NV9-110"
---
# Kiểm soát Giải ngân từ Tài khoản Vốn FDI

<a id="NV9-110-D01"></a>
## 1. Nguyên tắc Chuyển vốn FDI thành Vốn lưu động

Nguồn tiền ngoại tệ từ công ty mẹ chuyển vào tài khoản vốn FDI (Direct Investment Capital Account) tại Ngân hàng Mô phỏng không được chi tiêu trực tiếp cho hoạt động hàng ngày. Doanh nghiệp FDI bắt buộc phải bán ngoại tệ cho Ngân hàng Mô phỏng để thu về VND, hoặc chuyển ngoại tệ sang Tài khoản thanh toán (Current Account) ngoại tệ của chính doanh nghiệp đó. Khi thực hiện giao dịch chuyển đổi này, hệ thống Core Banking sẽ đánh giá mức độ phù hợp với Giấy chứng nhận đăng ký đầu tư (IRC). Nếu số tiền giải ngân vượt quá tiến độ cam kết góp vốn trong IRC, lệnh chuyển tiền sẽ bị đình chỉ và chuyển sang bộ phận Tuân thủ (Compliance) xét duyệt tay. Các giao dịch này không được phép định tuyến qua cổng Paygate để tránh việc lách luật quản lý luồng vốn.

<a id="NV9-110-D02"></a>
## 2. Cảnh báo Rửa tiền trong Quá trình Giải ngân

Một thủ đoạn tinh vi thường bị hệ thống AML theo dõi là việc doanh nghiệp FDI giải ngân toàn bộ số vốn góp (VND) ngay sau khi nhận được, chuyển đến một bên thứ ba dưới hình thức "thanh toán hợp đồng tư vấn" hoặc "tạm ứng tiền hàng". Nếu hệ thống phát hiện hành vi: nhận vốn FDI -> chuyển đổi VND -> chuyển khoản cho nhiều tài khoản cá nhân hoặc doanh nghiệp nội địa (có lịch sử hoạt động dưới 6 tháng) trong vòng 48 giờ, kịch bản AML "FDI Pass-through" sẽ được kích hoạt. Hậu quả là, số tiền chuyển đi sẽ bị đóng băng (Hold) tại ngân hàng thụ hưởng, tài khoản vốn FDI của doanh nghiệp tại Ngân hàng Mô phỏng sẽ bị phong tỏa chiều ghi nợ (Debit Block), đồng thời báo cáo Giao dịch Đáng ngờ (STR) sẽ được gửi tự động đến Cơ quan Phòng chống rửa tiền quốc gia.
