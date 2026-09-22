---
domain: "NV1"
document_id: "NV1-213"
---
# Đánh giá rủi ro xác thực giọng nói (Voice Biometrics) trong môi trường tạp âm

<a id="NV1-213-D01"></a>
## 1. Nhiễu âm thanh môi trường (Environmental Noise)
Ngân hàng Mô phỏng triển khai tính năng xác thực giao dịch giá trị cao trên Paygate bằng Voice Biometrics (Sinh trắc học giọng nói). Tuy nhiên, khách hàng thực hiện lệnh chuyển tiền tại môi trường nhiều tạp âm (quán cafe, công trường, nhà máy) thường gặp lỗi False Rejection Rate (FRR) cao do hệ thống không tách được voiceprint của chính chủ khỏi tiếng ồn nền (Background Noise).

<a id="NV1-213-D02"></a>
## 2. Pre-processing âm thanh và Fallback Call Center
Để xử lý edge case này, module Voice trên Paygate được tích hợp bộ lọc AI Audio Pre-processing để khử nhiễu (Noise Cancellation). Nếu điểm tin cậy giọng nói (Voice Confidence Score) vẫn nằm trong khoảng mập mờ (60% - 75%), luồng giao dịch bị ngưng đọng (Suspended). Hệ thống tự động chuyển tiếp (Route) yêu cầu tới Call Center. Một tư vấn viên của Ngân hàng Mô phỏng sẽ thực hiện cuộc gọi Outbound Verify, đặt câu hỏi bảo mật động (Dynamic Challenge Questions - ví dụ: 2 số cuối CCCD, giao dịch gần nhất) để duyệt lệnh thủ công thay vì bắt khách hàng thử lại giọng nói nhiều lần.
