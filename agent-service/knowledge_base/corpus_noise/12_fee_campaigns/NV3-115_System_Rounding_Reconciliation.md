---
domain: "NV3"
document_id: "NV3-115"
title: "Đối soát và Xử lý Sai số Làm tròn (Rounding Reconciliation)"
---

# Đối soát và Xử lý Sai số Làm tròn (Rounding Reconciliation)

<a id="NV3-115-D01"></a>
## 1. Bản chất Sai số Làm tròn trong Thanh toán
Trong quá trình xử lý vi mô (micro-processing) trên hệ thống **Paygate**, việc tính toán phần trăm theo Fee Campaigns thường tạo ra các phần lẻ thập phân. **Ngân hàng Mô phỏng** áp dụng chuẩn làm tròn xuống (Round-down to Nearest Integer) cho phần lợi ích của Merchant và làm tròn lên (Round-up) cho phần phí ngân hàng thu.
Sự chênh lệch này tích tụ thành một khoản (Rounding Variance Account) khổng lồ vào cuối kỳ.

<a id="NV3-115-D02"></a>
## 2. Nguyên tắc Bù trừ trong Thanh toán Khối lượng lớn
Đối với **Công ty Minh An**, tài khoản sai số làm tròn này sẽ được chốt lại mỗi quý. 
Thay vì ghi nhận như một khoản lỗ hệ thống, ngân hàng sẽ sử dụng số dư này để đối trừ vào khoản giải ngân Volume-Based Rebates (đã nêu ở NV3-112). Nếu số dư sai số lớn hơn tổng Rebate, Minh An không phải đóng thêm tiền, hệ thống sẽ reset tài khoản sai số về 0 (Zero-out Process).
