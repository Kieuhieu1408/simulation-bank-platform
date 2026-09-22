---
domain: "NV3"
document_id: "NV3-113"
title: "Quy trình Xử lý Tranh chấp Sai lệch Phí (Fee Dispute Resolution)"
---

# Quy trình Xử lý Tranh chấp Sai lệch Phí (Fee Dispute Resolution)

<a id="NV3-113-D01"></a>
## 1. Nguyên nhân Phổ biến Gây Sai lệch Phí
Sự phức tạp của cấu trúc Fee Campaigns kết hợp với chính sách Grandfathering trên **Paygate** thường xuyên dẫn đến các sai lệch giữa số phí Merchant tính toán và số liệu **Ngân hàng Mô phỏng** thu thực tế.
Lý do lớn nhất là việc hệ thống không áp dụng đồng thời hai mức hoàn tiền theo Giao thức NV3-106, dẫn đến merchant báo cáo bị "thu khống phí".

<a id="NV3-113-D02"></a>
## 2. Quy trình Xử lý dành riêng cho Khách VIP
Đối với **Công ty Minh An**, khi có khiếu nại về sai lệch do Fallback tự động (theo NV3-111), bộ phận Kế toán đối soát phải hoàn thành việc tra soát trong vòng 48 giờ làm việc. 
Số tiền chênh lệch sẽ không được trả bằng tiền mặt mà được quy đổi thành "Tín chỉ Phí" (Fee Credits), cho phép Minh An khấu trừ trực tiếp vào các khoản phí Chargeback vượt quá hạn mức miễn trừ 50 giao dịch/tháng của họ (theo SLA NV3-105).
