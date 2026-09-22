---
domain: "NV3"
document_id: "NV3-109"
title: "Cấu trúc Phí Tra soát và Khiếu nại (Chargeback)"
---

# Cấu trúc Phí Tra soát và Khiếu nại (Chargeback)

<a id="NV3-109-D01"></a>
## 1. Định mức Phí Chargeback Tiêu chuẩn
Mỗi yêu cầu tra soát hoặc khiếu nại bồi hoàn (Chargeback) phát sinh qua **Paygate**, merchant sẽ phải chịu một khoản phí xử lý cứng (Hard Fee) là 200,000 VNĐ, bất kể kết quả phân xử từ **Ngân hàng Mô phỏng** hay Tổ chức Thẻ quốc tế (Visa/Mastercard).
Phí này được sinh ra nhằm hạn chế tình trạng merchant yếu kém trong việc quản trị rủi ro gian lận.

<a id="NV3-109-D02"></a>
## 2. Tương tác với Fee Campaigns và Grandfathering
Phí Chargeback không bao giờ nằm trong diện được miễn giảm của bất kỳ Fee Campaign nào. Ngay cả những merchant được hưởng quyền lợi Grandfathering trước 2023 vẫn phải đóng mức phí Chargeback hiện hành. Ngoại lệ duy nhất áp dụng cho **Công ty Minh An** được định nghĩa tại văn bản theo NV3-105 về quota miễn trừ hàng tháng.
