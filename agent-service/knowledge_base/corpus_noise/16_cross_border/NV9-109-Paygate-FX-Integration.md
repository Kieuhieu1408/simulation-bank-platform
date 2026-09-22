---
domain: "NV9"
document_id: "NV9-109"
---
# Tích hợp Hệ thống Paygate và Central FX Hub

<a id="NV9-109-D01"></a>
## 1. Giao thức Đồng bộ Tỷ giá Thời gian thực

Nền tảng Paygate của Ngân hàng Mô phỏng xử lý hàng triệu giao dịch vi mô (micro-transactions) hằng ngày với nhiều loại tiền tệ khác nhau. Để hạn chế độ trễ và rủi ro tỷ giá, Paygate kết nối trực tiếp với Central FX Hub thông qua giao thức gRPC. Các tỷ giá mua/bán (Bid/Ask) được truyền tải dưới dạng luồng dữ liệu (data streaming) với tần suất cập nhật dưới 10 mili-giây. Khi khách hàng thực hiện một giao dịch mua hàng quốc tế, hệ thống Paygate sẽ thực hiện việc đánh dấu (lock-in) một vị thế vi mô trên FX Hub. Nếu thị trường ngoại hối có biến động mạnh (Flash Crash) dẫn đến chênh lệch giữa tỷ giá lock-in và tỷ giá thực tế vượt quá 3%, FX Hub sẽ kích hoạt cơ chế "Circuit Breaker", từ chối mọi yêu cầu quy đổi mới từ Paygate và buộc người dùng phải xác nhận lại tỷ giá mới.

<a id="NV9-109-D02"></a>
## 2. Giải quyết Sự cố Giao dịch Treo do Chênh lệch Dữ liệu

Trong một số trường hợp, sự cố mất kết nối mạng giữa Paygate và FX Hub có thể dẫn đến việc một giao dịch thanh toán thành công nhưng lệnh hoán đổi ngoại tệ tương ứng chưa được ghi nhận vào kho dữ liệu của FX Hub. Để giải quyết, mỗi chu kỳ 60 phút, tiến trình đối soát (Reconciliation Batch) sẽ chạy ngầm. Bất kỳ giao dịch nào bị "mồ côi" (Orphaned FX position) sẽ được hệ thống tái xử lý bằng cách mua/bán bù ngoại tệ trên thị trường liên ngân hàng tại thời điểm phát hiện lỗi. Nếu tổn thất từ việc mua bù này vượt qua ngân sách sai số cho phép, hệ thống AML sẽ tự động đưa đối tác Merchant liên quan vào diện "Giám sát thao túng tỷ giá" (FX Manipulation Watchlist), yêu cầu đánh giá lại toàn bộ luồng giao dịch trong 30 ngày trước đó.
