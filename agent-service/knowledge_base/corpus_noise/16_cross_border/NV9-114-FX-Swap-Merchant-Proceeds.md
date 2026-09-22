---
domain: "NV9"
document_id: "NV9-114"
---
# Sử dụng FX Swap cho Quyết toán Dòng tiền Merchant

<a id="NV9-114-D01"></a>
## 1. Cơ chế FX Swap để Giảm thiểu Đóng băng Vốn

Đối với các International Merchant lớn có luồng tiền vào (Inbound) từ Paygate bằng VND nhưng cần chi phí hoạt động (Outbound) bằng EUR hoặc JPY, tình trạng đóng băng vốn thường xuyên xảy ra khi các giao dịch Outbound bị vướng lệnh AML Hold. Để giải quyết vấn đề thanh khoản cho Merchant, Ngân hàng Mô phỏng cung cấp sản phẩm "Synthetic FX Swap". Theo đó, ngân hàng sẽ tạm ứng trước số ngoại tệ cần thiết (Leg 1 của Swap) cho Merchant dựa trên số dư VND đang bị AML tạm giữ. Khi lệnh AML Hold được gỡ bỏ, số dư VND này sẽ được dùng để tất toán giao dịch (Leg 2 của Swap). Lãi suất áp dụng cho khoản tạm ứng này dựa trên chi phí vốn (Cost of Funds) của ngân hàng cộng biên độ 1.5%.

<a id="NV9-114-D02"></a>
## 2. Rủi ro Tín dụng trong Cấu trúc Swap gắn với AML

Sản phẩm Synthetic FX Swap tiềm ẩn rủi ro tín dụng rất lớn. Nếu sau thời gian điều tra (tối đa 120 ngày), cơ quan chức năng kết luận lô giao dịch VND của Merchant có liên quan đến lừa đảo xuyên biên giới và ra quyết định tịch thu (Confiscation), Ngân hàng Mô phỏng sẽ mất trắng khoản ngoại tệ đã tạm ứng ở Leg 1. Để phòng ngừa, hệ thống Quản trị Rủi ro (Risk Engine) tự động đánh giá "Điểm Tín nhiệm AML" (AML Trust Score) của Merchant. Chỉ những Merchant có điểm số trên 85/100 và không có lịch sử vi phạm trong 3 năm gần nhất mới được cấp hạn mức FX Swap. Đối với các Merchant dưới chuẩn, họ buộc phải chờ quy trình quyết toán tự nhiên (Natural Settlement) sau khi AML Hold được giải tỏa hoàn toàn.
