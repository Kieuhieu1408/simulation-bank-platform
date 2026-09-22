---
domain: "NV9"
document_id: "NV9-104"
---
# Xử lý Xung đột về Khung thời gian Tạm giữ AML (AML Hold Timelines)

<a id="NV9-104-D01"></a>
## 1. Mâu thuẫn Khung thời gian giữa Các hệ thống

Tại Ngân hàng Mô phỏng, sự phức tạp của giao dịch xuyên biên giới thường dẫn đến mâu thuẫn giữa khung thời gian xử lý của các hệ thống. Hệ thống thanh toán lõi (Core Banking) quy định một lệnh chuyển tiền quốc tế chỉ được treo ở trạng thái "Pending" tối đa 48 giờ. Tuy nhiên, hệ thống AML nội bộ lại có quy định thời gian điều tra tiêu chuẩn (Standard Investigation Timeline) cho các giao dịch nghi ngờ là 72 giờ. Nếu giao dịch được thực hiện qua cổng Paygate có dán nhãn "High Velocity" (thanh toán nhanh), Paygate lại cấu hình timeout (thời gian chờ tối đa) là 24 giờ. Sự chồng chéo này dẫn đến tình huống một giao dịch bị Paygate đánh dấu là "Timeout/Failed" và hoàn tiền cho khách hàng, nhưng sau đó 72 giờ hệ thống AML lại phê duyệt (Approve) và Core Banking lại đẩy lệnh đi, gây thiệt hại kép cho ngân hàng.

<a id="NV9-104-D02"></a>
## 2. Cơ chế Giải quyết Xung đột Thời gian (Hold Resolution Mechanism)

Để khắc phục vấn đề trên, Ngân hàng Mô phỏng áp dụng cơ chế "Synchronized AML Hold" (Tạm giữ AML Đồng bộ). Theo đó, bất kỳ giao dịch ngoại hối hoặc xuyên biên giới nào kích hoạt cảnh báo AML sẽ ngay lập tức phát sóng (broadcast) trạng thái "Global Hold" đến tất cả các hệ thống (Core, Paygate, FX Hub). Khi trạng thái Global Hold được kích hoạt, thời gian timeout của Paygate (24h) và Core Banking (48h) sẽ bị vô hiệu hóa và ép buộc (override) theo khung thời gian của hệ thống AML (lên đến 120 giờ đối với các vụ việc phức tạp cần sự hỗ trợ của ngân hàng đại lý). Quá trình giải tỏa (Release) chỉ xảy ra khi Giám đốc Khối Tuân thủ cấp mã khóa điện tử, lúc này Paygate sẽ khôi phục lại phiên giao dịch dựa trên tỷ giá chốt (FX fixing) đã được lưu trữ trước đó.
