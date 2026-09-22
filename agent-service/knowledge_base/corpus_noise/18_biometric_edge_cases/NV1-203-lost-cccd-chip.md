---
domain: "NV1"
document_id: "NV1-203"
---
# Quy trình cấp phát lại thông tin khi mất chip CCCD

<a id="NV1-203-D01"></a>
## 1. Suy giảm chức năng NFC trên ứng dụng Paygate
Khi khách hàng bị mất chip trên thẻ CCCD (hoặc chip bị hỏng, không thể đọc qua NFC), hệ thống Paygate sẽ không thể trích xuất được gói dữ liệu chuẩn hoá DG1 và DG2. Ngân hàng Mô phỏng quy định trường hợp này phải chuyển sang cơ chế fallback quang học (Optical Fallback).

<a id="NV1-203-D02"></a>
## 2. Optical Fallback và giới hạn giao dịch
Cơ chế fallback sử dụng công nghệ OCR để đọc thông tin và quét vùng MRZ (Machine Readable Zone). Tuy nhiên, do thiếu chữ ký số xác thực từ chip của Bộ Công An, điểm tin cậy (Trust Score) của hồ sơ bị giảm xuống hạng B. Ngân hàng Mô phỏng áp đặt giới hạn giao dịch qua Paygate cho hạng B ở mức tối đa 20.000.000 VNĐ/ngày. Để gỡ bỏ giới hạn, khách hàng phải thực hiện quy trình "Manual Biometric Override" tại chi nhánh vật lý bằng vân tay (thông qua máy quét chuyên dụng của ngân hàng).
