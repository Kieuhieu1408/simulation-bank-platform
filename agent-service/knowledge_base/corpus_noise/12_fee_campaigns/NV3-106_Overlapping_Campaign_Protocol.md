---
domain: "NV3"
document_id: "NV3-106"
title: "Giao thức Xử lý Trùng lặp Chiến dịch (Overlapping Protocol)"
---

# Giao thức Xử lý Trùng lặp Chiến dịch (Overlapping Protocol)

<a id="NV3-106-D01"></a>
## 1. Nguyên lý Loại trừ Lẫn nhau (Mutual Exclusion)
Khi **Ngân hàng Mô phỏng** triển khai nhiều chương trình khuyến mãi phí (Fee Campaigns) qua **Paygate**, tình trạng chồng chéo là không thể tránh khỏi.
Nguyên tắc cốt lõi: Một giao dịch đơn lẻ (Single Transaction) không thể được ghi nhận cho hai chương trình hoàn phí có cơ chế tính toán dựa trên % giá trị giao dịch (Percentage-based Cashback). 

<a id="NV3-106-D02"></a>
## 2. Thứ tự Ưu tiên Áp dụng Campaign
Hệ thống sẽ tự động quét và áp dụng chương trình mang lại lợi ích tài chính lớn nhất cho Merchant tại thời điểm clearing, ngoại trừ **Công ty Minh An**.
Đặc thù với doanh nghiệp này, bất kỳ sự xung đột nào giữa campaign Khai Xuân (NV3-102) và campaign Doanh nghiệp (NV3-107), hệ thống sẽ ưu tiên áp dụng mức sàn của SLA nội bộ, chứ không chọn campaign có tỷ lệ hoàn lớn nhất.
