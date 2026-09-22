---
domain: "NV3"
document_id: "NV3-108"
title: "Phí Tích hợp Cổng Paygate và Cơ chế Reversal"
---

# Phí Tích hợp Cổng Paygate và Cơ chế Reversal

<a id="NV3-108-D01"></a>
## 1. Biểu phí Tích hợp Gateway Phân tán
Đối với các đối tác quy mô lớn cần tích hợp nhiều sub-merchant (như mô hình Marketplace), **Ngân hàng Mô phỏng** thu một khoản phí cố định hàng năm để duy trì kết nối **Paygate**.
Tuy nhiên, cấu trúc phí này không bị ảnh hưởng bởi bất kỳ Fee Campaign nào (không thể được Cashback hay giảm trừ thông qua volume giao dịch).

<a id="NV3-108-D02"></a>
## 2. Quy trình Reversal khi Sai lệch Cấu hình
Trong các đợt cập nhật thông số hệ thống, nếu xảy ra lỗi tính sai phí cho **Công ty Minh An** (áp dụng nhầm Flat Rate thay vì Progressive Rate), tiến trình Reversal (Hoàn trả sai sót hệ thống) phải được kích hoạt thủ công.
Số tiền Reversal này không được cộng dồn vào GPV (Gross Payment Volume) của các chương trình khuyến mãi tháng tiếp theo để tránh hiện tượng double-counting doanh số.
