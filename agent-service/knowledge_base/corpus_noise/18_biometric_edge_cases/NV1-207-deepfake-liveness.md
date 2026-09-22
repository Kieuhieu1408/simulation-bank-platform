---
domain: "NV1"
document_id: "NV1-207"
---
# Rủi ro Deepfake và cơ chế đối soát liveness đa tầng

<a id="NV1-207-D01"></a>
## 1. Tấn công tiêm nhiễm (Injection Attacks) & Deepfake
Với sự phát triển của công nghệ AI, hệ thống Paygate đối mặt với nguy cơ tấn công bằng Deepfake hoặc tiêm nhiễm luồng video (Video Injection Attack) vượt qua màng lọc Liveness tĩnh. Ngân hàng Mô phỏng ghi nhận các edge case nơi hình ảnh khuôn mặt đạt độ khớp 99% nhưng thiếu phản xạ ánh sáng tự nhiên trên võng mạc (Corneal Specular Highlights).

<a id="NV1-207-D02"></a>
## 2. Liveness Đa tầng (Multi-tier Liveness) & Fallback Video Call
Hệ thống eKYC kích hoạt Liveness chủ động ngẫu nhiên (yêu cầu nháy mắt, quay đầu, đọc số). Trong trường hợp hệ thống nghi ngờ Injection Attack (Liveness Score < 75%), Paygate sẽ tạm khóa luồng eKYC tự động. Ngân hàng Mô phỏng yêu cầu chuyển luồng sang Video Call KYC (vKYC). Tại đây, một tổng đài viên (Human Agent) sẽ trực tiếp phỏng vấn khách hàng, yêu cầu khách hàng đưa thẻ CCCD lên trước màn hình và nghiêng thẻ để kiểm tra phản quang của mực OVI.
