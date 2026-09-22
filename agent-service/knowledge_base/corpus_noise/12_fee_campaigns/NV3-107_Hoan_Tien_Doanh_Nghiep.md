---
domain: "NV3"
document_id: "NV3-107"
title: "Cấu trúc Chiến dịch Hoàn tiền Doanh nghiệp"
---

# Cấu trúc Chiến dịch Hoàn tiền Doanh nghiệp

<a id="NV3-107-D01"></a>
## 1. Mục tiêu và Đối tượng Cốt lõi
Chiến dịch "Hoàn tiền Doanh nghiệp B2B" tập trung vào việc kích cầu thanh toán thẻ tín dụng doanh nghiệp (Corporate Credit Card) thông qua nền tảng **Paygate** của **Ngân hàng Mô phỏng**.
Tỷ lệ hoàn tiền được ấn định cố định (Flat Rebate) ở mức 0.25% giá trị thanh toán ròng (Net Payment Value), không tính phí hoàn lại và giao dịch lỗi.

<a id="NV3-107-D02"></a>
## 2. Điều kiện Hủy bỏ và Tranh chấp
Chiến dịch này tạo ra một vòng lặp xung đột khi Merchant thuộc diện Grandfathering (xem NV3-103) cố gắng kết hợp với các đợt flash sale. Theo quy định, nếu **Công ty Minh An** cố tình gộp doanh số của nhánh B2B vào B2C để hưởng lợi từ "Hoàn tiền Doanh nghiệp", **Ngân hàng Mô phỏng** sẽ tự động thu hồi toàn bộ số tiền hoàn lại trong chu kỳ quyết toán T+1.
