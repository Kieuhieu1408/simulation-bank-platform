# Danh mục rủi ro thiết kế — API Gateway và bảo mật giữa các service

**Trạng thái:** `VALIDATED_WITH_OPEN_ACTIONS` — Đã xác minh, còn hành động cần xử lý trong thiết kế/implementation

| ID | Rủi ro | Tác động | Khả năng xảy ra | Mức độ ban đầu | Kiểm soát/quyết định cần có | Trạng thái |
|---|---|---|---|---|---|---|
| R-SEC-01 | Service phía sau chỉ tin vào kết quả xác thực của API Gateway | Lệnh giả mạo hoặc đi vòng Gateway có thể truy cập API nghiệp vụ | Trung bình | Nghiêm trọng | Mỗi service tự xác minh token dành cho nó và áp dụng chính sách phân quyền | `CONTROL_APPROVED` — Kiểm soát đã duyệt |
| R-SEC-02 | Money Bank chuyển tiếp user token có audience không phải Profile Service | Lạm dụng token, confused deputy hoặc xác minh không nhất quán | Cao | Nghiêm trọng | Standard Token Exchange V2 tạo token có `aud=profile-service` | `POC_REQUIRED` — Cần PoC IAM |
| R-SEC-03 | Money Bank chỉ dùng client credentials cho Proposal do người dùng khởi tạo | Profile không thể kiểm tra ownership hoặc audit người dùng một cách tin cậy | Cao | Cao | Giữ subject người dùng và service actor trong token/audit có thể xác minh | `CONTROL_APPROVED` — Kiểm soát đã duyệt |
| R-SEC-04 | Định danh được truyền qua header không ký như `X-User-Id` | Giả mạo header và nâng quyền | Cao | Nghiêm trọng | Lấy định danh từ token đã xác minh; loại bỏ header định danh không đáng tin cậy | `CONTROL_APPROVED` — Kiểm soát đã duyệt |
| R-NET-01 | API nội bộ có thể truy cập public hoặc từ mọi workload | Tăng bề mặt tấn công và khả năng di chuyển ngang | Trung bình | Nghiêm trọng | Private DNS/LB, allow-list firewall/NetworkPolicy và không có public listener | `PLATFORM_PENDING` — Chờ Infrastructure Design |
| R-NET-02 | Mọi traffic east-west đều vòng qua API Gateway bên ngoài | Tăng độ trễ, coupling và phạm vi ảnh hưởng khi Gateway lỗi | Trung bình | Cao | Tách biệt đường đi north-south và east-west | `RESOLVED_BY_DESIGN` — Đã xử lý trong thiết kế |
| R-TLS-01 | Coi mạng private là đủ an toàn | Không bảo đảm định danh bên gọi và bí mật dữ liệu trên đường truyền | Trung bình | Nghiêm trọng | TLS + OAuth2 baseline; mTLS/workload identity khi nền tảng hỗ trợ | `PLATFORM_PENDING` — Chờ Infrastructure Design |
| R-IAM-01 | Token exchange làm mọi request phụ thuộc đồng bộ vào IAM | Độ trễ/lỗi IAM lan sang thao tác Proposal | Trung bình | Cao | Fail closed, token TTL/cache có giới hạn, timeout/circuit policy và IAM HA | `POC_REQUIRED` — Cần PoC IAM |
| R-AUTHZ-01 | Gateway, Money Bank và Profile áp dụng quyền không thống nhất | Request hợp lệ bị chặn hoặc hành động trái phép được cho qua | Trung bình | Cao | Gateway kiểm tra thô; Profile sở hữu quyết định Proposal cuối cùng | `RESOLVED_BY_DESIGN` — Đã xử lý trong thiết kế |
| R-AUD-01 | Audit chỉ ghi service hoặc chỉ ghi người dùng | Thiếu trách nhiệm giải trình khi điều tra | Cao | Cao | Ghi subject, client/actor, hành động, đối tượng, quyết định và correlation/trace | `CONTROL_APPROVED` — Kiểm soát đã duyệt |
| R-OPS-01 | Certificate mTLS/client secret không được xoay vòng an toàn | Gián đoạn dịch vụ hoặc lộ credential | Trung bình | Cao | Cấp phát/xoay vòng tự động, thời gian hiệu lực chồng lấn và runbook | `PLATFORM_PENDING` — Chờ Infrastructure Design |
| R-RES-01 | Retry Money Bank → Profile tạo trùng Proposal | Tạo nhiều yêu cầu quản lý tài khoản cho cùng một ý định | Trung bình | Cao | Idempotency end-to-end và durable unique constraint tại Profile | `IMPLEMENTATION_PENDING` — Thiết kế đã duyệt |
| R-IDEM-01 | Corebank hiện check-then-act trước khi insert idempotency key | Request thua race rollback an toàn nhưng có thể trả lỗi `500` thay vì kết quả cũ | Cao | Cao | Insert/claim-first, unique constraint và xử lý duplicate ngoài transaction đã rollback | `IMPLEMENTATION_PENDING` |
| R-IDEM-02 | Corebank so sánh request lặp chưa đầy đủ | Cùng key nhưng payload khác có thể bị coi là cùng ý định | Trung bình | Cao | Canonical request hash bao gồm toàn bộ trường nghiệp vụ có ý nghĩa | `IMPLEMENTATION_PENDING` |
| R-IDEM-03 | Áp dụng máy móc MySQL `INSERT IGNORE` cho Oracle | SQL không tương thích hoặc che khuất lỗi dữ liệu | Trung bình | Cao | Dùng unique constraint Oracle/JPA làm nguồn quyết định; chỉ tối ưu native sau benchmark | `RESOLVED_BY_DECISION` |
| R-IDEM-04 | Chưa có concurrency/failure test cho idempotency | Không có bằng chứng chống double effect khi race/crash/redelivery | Cao | Cao | Test nhiều replica, 100 request cùng key, timeout sau commit và Kafka redelivery | `TEST_DESIGN_PENDING` |

## Quy tắc chấp nhận rủi ro

- Rủi ro nghiêm trọng chặn quyết định thiết kế cuối cùng cho tới khi được giảm thiểu hoặc được chủ sở hữu Security/Architecture có thẩm quyền chấp nhận rõ ràng.
- Private subnet không tự giải quyết rủi ro xác thực, phân quyền hoặc mã hóa.
- Việc chọn sản phẩm được hoãn lại; các thuộc tính bảo mật bắt buộc phải được phê duyệt trước.
