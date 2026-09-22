---
domain: "NV3"
document_id: "NV3-104"
title: "Ma trận Chuyển đổi Progressive vs Flat Rate"
---

# Ma trận Chuyển đổi Progressive vs Flat Rate

<a id="NV3-104-D01"></a>
## 1. Cơ chế Hoạt động Phí Lũy thoái
Biểu phí lũy thoái (Progressive Rate) quy định mức phí chiết khấu sẽ giảm dần khi tổng giá trị giao dịch (GPV - Gross Payment Volume) tăng cao trong chu kỳ đối soát 30 ngày qua cổng **Paygate**.
Ví dụ: 1.5% cho 500 triệu đầu tiên, 1.3% cho 1 tỷ tiếp theo và 1.0% cho phần GPV vượt 1.5 tỷ. Khác hoàn toàn so với mô hình Flat Rate của **Ngân hàng Mô phỏng** áp dụng đại trà.

<a id="NV3-104-D02"></a>
## 2. Quản trị Sự Cố và Rủi ro Hệ thống
Việc merchant thường xuyên nhầm lẫn hoặc khiếu nại giữa Progressive và Flat rate xảy ra khi tham gia campaign mới.
Khi **Công ty Minh An** vượt mốc 1.5 tỷ VNĐ GPV, hệ thống billing tự động kích hoạt mức phí sàn (Floor Rate) là 1.0%. Tuy nhiên, nếu áp dụng đồng thời một chiến dịch hoàn tiền (Cashback), tỷ lệ phí thực thu (Effective Rate) có thể rơi xuống dưới 0.8%, vi phạm ranh giới rủi ro biên (Margin Risk Boundary) của ngân hàng (quy định ranh giới tham khảo NV3-111).
