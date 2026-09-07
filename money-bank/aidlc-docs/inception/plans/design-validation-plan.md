# Kế hoạch xác minh thiết kế — MONEY-BANK

**Trạng thái:** `DRAFT` — Dự thảo, cần người dùng phê duyệt  
**Phạm vi:** Đối chiếu `docs/brd.md` với `docs/srd.md`, ưu tiên API Gateway và bảo mật giao tiếp nội bộ giữa các service  
**Chính sách thay đổi:** Không chỉnh sửa BRD/SRD cho tới khi cổng câu hỏi và cổng findings được phê duyệt

## 1. Mục tiêu

Xác định SRD hiện tại có đầy đủ, nhất quán nội bộ, truy vết được về BRD và đủ an toàn để chuyển sang thiết kế chi tiết hay không. Nhóm quyết định đầu tiên bao gồm:

- Định tuyến north-south qua API Gateway.
- Giao tiếp east-west từ Money Bank → Profile Service.
- Định danh workload, truyền định danh người dùng cuối và quyền sở hữu quyết định phân quyền.
- Phạm vi private, mTLS, OAuth2/OIDC, chính sách mạng và chuỗi chủ thể audit.

## 2. Đầu vào

| Đầu vào | Vai trò |
|---|---|
| `docs/brd.md` revision 1 | Baseline yêu cầu nghiệp vụ |
| `docs/srd.md` version 0.2 | Thiết kế cần xác minh |
| `requirement-verification-questions.md` | Quyết định và giả định của các bên liên quan |
| `design-risk-register.md` | Rủi ro và biện pháp kiểm soát cần xác minh |

## 3. Workflow và cổng kiểm soát dự kiến

| Bước | Hoạt động | Đầu ra | Cổng kiểm soát |
|---:|---|---|---|
| 1 | Xác nhận phạm vi và các tác nhân | Kế hoạch xác minh đã duyệt | Người dùng phê duyệt |
| 2 | Trả lời câu hỏi kiến trúc | File câu hỏi đã hoàn thành | Câu trả lời rõ ràng, không mâu thuẫn |
| 3 | Xác minh truy vết BRD ↔ SRD | Findings về độ bao phủ và mâu thuẫn | Review findings |
| 4 | Xác minh Gateway và trust boundary | Findings về luồng bảo mật và mối đe dọa | Phê duyệt quyết định bảo mật |
| 5 | Phân loại findings | Báo cáo mức chặn/cao/trung bình/thấp | Người dùng chấp nhận hướng xử lý |
| 6 | Chuẩn bị bản vá SRD đề xuất | Chỉ tạo diff đề xuất | Phê duyệt chỉnh sửa rõ ràng |
| 7 | Áp dụng và kiểm tra thay đổi đã duyệt | SRD cập nhật và bằng chứng xác minh | Review cuối cùng |

## 4. Các khía cạnh xác minh

- **Đúng nghiệp vụ:** Trách nhiệm của Money Bank, Profile Service, CMS và Corebank phải khớp BRD.
- **Bảo mật:** Mỗi hop xác thực định danh bên gọi và phân quyền hành động; không coi mạng private là cơ chế xác thực.
- **Trust boundary:** Điểm vào public, workload nội bộ, IAM và vùng Corebank phải được thể hiện rõ.
- **Truyền định danh:** Subject người dùng và service actor không thể bị giả mạo hoặc nhầm lẫn.
- **Khả năng phục hồi:** Phải xác định hành vi khi Gateway/IAM/Profile lỗi hoặc timeout.
- **Khả năng audit:** Phải truy được cả người dùng cuối và service gọi đối với hành động nhạy cảm.
- **Khả năng vận hành:** Việc xoay vòng certificate/key, cache token, telemetry và thu hồi quyền phải khả thi.
- **Khả năng kiểm thử:** Mỗi kiểm soát bắt buộc phải có chiến lược kiểm thử contract/integration/security.

## 5. Ngoài phạm vi trước khi được phê duyệt

- Chỉnh sửa `docs/brd.md` hoặc `docs/srd.md`.
- Chọn sản phẩm API Gateway, service mesh hoặc quản lý secret cụ thể.
- Triển khai Spring Security, cấu hình Keycloak, mTLS hoặc NetworkPolicy.
- Tạo deployment manifest hoặc credential production.

## 6. Tiêu chí hoàn thành

- Mọi câu hỏi blocking có một câu trả lời được duyệt hoặc có người phụ trách và thời hạn xử lý.
- Không trình bày rủi ro critical chưa xử lý như một quyết định thiết kế cuối cùng.
- Findings trích dẫn đúng mục BRD/SRD và có hướng xử lý đề xuất.
- Chỉ áp dụng thay đổi SRD sau khi được phê duyệt rõ ràng.

## 7. Phê duyệt

Chọn một phương án cho câu hỏi `VAL-001` trong `requirement-verification-questions.md` và điền vào trường `[Answer]:`.

