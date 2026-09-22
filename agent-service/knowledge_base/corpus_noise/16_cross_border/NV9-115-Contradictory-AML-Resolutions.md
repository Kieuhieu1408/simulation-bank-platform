---
domain: "NV9"
document_id: "NV9-115"
---
# Ma trận Giải quyết Quyết định Trái ngược về AML (Contradictory AML Resolutions)

<a id="NV9-115-D01"></a>
## 1. Vấn đề Bất đồng Phán quyết giữa Các Hệ thống

Sự vận hành độc lập của nhiều hệ thống giám sát tại Ngân hàng Mô phỏng đôi khi dẫn đến các phán quyết trái ngược nhau đối với cùng một giao dịch xuyên biên giới. Ví dụ: Bộ lọc Fraud của Paygate nhận định một giao dịch thẻ tín dụng quốc tế là hợp lệ (Green); Hệ thống Core Banking đánh giá chứng từ là đầy đủ (Valid); nhưng Hệ thống AML (dựa trên AI phân tích hành vi) lại cảnh báo "Đỏ" (Red) do nghi ngờ cấu trúc dòng tiền có dấu hiệu rửa tiền. Mâu thuẫn này dẫn đến tình trạng giao dịch liên tục chuyển đổi giữa trạng thái "Approved" và "Suspended", gây tắc nghẽn hàng đợi xử lý (Message Queue) và ảnh hưởng đến trải nghiệm của đối tác Merchant.

<a id="NV9-115-D02"></a>
## 2. Cây Quyết định Phân xử (Arbitration Decision Tree)

Để chấm dứt tình trạng vòng lặp vô hạn (Infinite Loop) do phán quyết trái ngược, Ngân hàng Mô phỏng đã triển khai "Ma trận Giải quyết Xung đột" (Conflict Resolution Matrix). Ma trận này tuân theo nguyên tắc "Bảo thủ tối đa" (Maximum Conservatism). Bất cứ khi nào có ít nhất một hệ thống (dù là Paygate, Core, hay AML) đánh cờ "Red" hoặc "Hold", toàn bộ giao dịch bắt buộc chuyển sang trạng thái "Hard Stop". Không một hệ thống nào được quyền tự động override (vượt quyền) phán quyết "Red" của hệ thống khác. Giao dịch lúc này được định tuyến thẳng đến "Hội đồng Cấp cao" (Level 3 Compliance Committee). Chỉ khi có sự phê duyệt vật lý (chữ ký số bằng token cứng) của Trưởng bộ phận Tuân thủ (Chief Compliance Officer) thì giao dịch mới được chuyển thành "Approved" trên toàn bộ kiến trúc hệ thống, đồng thời ghi đè vĩnh viễn các cảnh báo trước đó.
