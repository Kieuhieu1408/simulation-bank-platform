# Báo cáo xác minh thiết kế — MONEY-BANK

**Trạng thái:** `COMPLETED_WITH_OPEN_ACTIONS`  
**Ngày xác minh:** 2026-07-12  
**Phạm vi:** BRD/SRD, API Gateway, bảo mật east-west, idempotency, module dùng chung và mức sẵn sàng chuyển sang Application Design

## 1. Nguồn xác minh

- `../../../docs/brd.md`
- `../../../docs/srd.md` phiên bản 0.2 trước validation
- `../../../../README.md`
- `../requirements/requirement-verification-questions.md`
- Implementation Corebank tại `../../../../corebank/src/main/java/com/hieu/corebank/`

## 2. Kết luận

Thiết kế đủ điều kiện chuyển sang Application Design sau khi cập nhật SRD theo các quyết định đã duyệt. Không còn câu hỏi nghiệp vụ/kiến trúc blocking trong phạm vi validation hiện tại. Các điểm phụ thuộc nền tảng và implementation được giữ thành action có owner ở giai đoạn tiếp theo, không được coi là đã hoàn thành.

## 3. Quyết định đã chốt

| ID | Quyết định |
|---|---|
| DV-DEC-01 | `money-bank` là tên chính thức thay `smartbank`. |
| DV-DEC-02 | API Gateway chỉ xử lý traffic north-south; không hairpin lời gọi east-west qua external Gateway. |
| DV-DEC-03 | Mobile/Web → Gateway → Money Bank → Profile Service private. |
| DV-DEC-04 | CMS Angular → Gateway → CMS Backend → Profile Service private; Profile sở hữu Proposal và lịch sử. |
| DV-DEC-05 | Private network không phải authentication; service đích vẫn xác minh token và authorization. |
| DV-DEC-06 | Baseline east-west là private endpoint + TLS + OAuth2 token đúng audience + network allow-list; mTLS là target khi nền tảng hỗ trợ tự động hóa certificate. |
| DV-DEC-07 | Dùng Keycloak Standard Token Exchange V2 cho user-initiated call Money Bank/CMS → Profile; cần PoC claim/audience/actor. |
| DV-DEC-08 | Profile quyết định cuối về ownership, role và state transition của Proposal. |
| DV-DEC-09 | Profile sở hữu durable idempotency cho Proposal; Money Bank truyền cùng key end-to-end. |
| DV-DEC-10 | Database unique constraint là lớp an toàn idempotency cuối; Redis chỉ là fast-path tùy chọn. |
| DV-DEC-11 | `common-service` là shared library không có runtime/database và không chứa domain rule riêng của service. |

## 4. Findings

| ID | Mức độ | Finding | Hướng xử lý | Trạng thái |
|---|---|---|---|---|
| DV-F-01 | Cao | SRD 0.2 chưa phân biệt rõ north-south/east-west và có thể hiểu Gateway route trực tiếp Profile cho mọi kênh | Bổ sung trust boundary, route Mobile/CMS và private service call | Đã đưa vào SRD 0.3 |
| DV-F-02 | Cao | Mạng private có nguy cơ bị hiểu là cơ chế xác thực service | Quy định TLS/OAuth2, validation tại từng service, NetworkPolicy/firewall và mTLS target | Đã đưa vào SRD 0.3 |
| DV-F-03 | Cao | Keycloak Standard Token Exchange không được phép mặc định là biểu diễn đầy đủ delegation actor chain | PoC token claim; audit riêng user subject và calling service | Chuyển Application Design |
| DV-F-04 | Cao | Corebank đang `findByIdempotencyKey` rồi mới insert, tạo TOCTOU race; unique constraint rollback bảo vệ số dư nhưng request thua có thể trả `500` | Claim-first/flush, bắt duplicate ngoài transaction rollback rồi đọc kết quả cũ | Chuyển Code Design/Implementation |
| DV-F-05 | Cao | `sameRequest` Corebank chưa so sánh toàn bộ payload có ý nghĩa và chưa có canonical hash | Định nghĩa canonical payload + SHA-256 request hash | Chuyển Application/Data Design |
| DV-F-06 | Cao | Chưa có test concurrency/failure cho idempotency | Test 100 request, nhiều replica, timeout sau commit, Redis down, Kafka redelivery | Chuyển Test Design |
| DV-F-07 | Trung bình | `INSERT IGNORE` trong tài liệu tham khảo là đặc thù MySQL nhưng Corebank dùng Oracle demo | Dùng unique constraint/JPA transaction; native optimization chỉ sau benchmark | Đã chốt quyết định |
| DV-F-08 | Trung bình | Nền tảng Docker/Kubernetes/VM chưa quyết định nên chưa thể chốt mTLS/NetworkPolicy implementation | Giữ thiết kế logic trung lập; chốt mapping tại Infrastructure Design | Mở có kiểm soát |
| DV-F-09 | Trung bình | `common-service` có nguy cơ trở thành shared business domain gây coupling | Chỉ chứa versioned contracts/shared primitives; cấm entity/repository/workflow/policy riêng | Đã cập nhật README/SRD |

## 5. Đánh giá các nhận định idempotency đầu vào

| Nhận định | Kết quả áp dụng |
|---|---|
| Kafka producer idempotence không bảo vệ consumer/database | Đúng; consumer cần Inbox unique trong cùng local transaction với business effect. |
| Check-then-act có TOCTOU race | Đúng; đã tồn tại trong Corebank hiện tại. |
| Redis/distributed lock không phải lớp an toàn cuối | Đúng; Redis chỉ được dùng làm fast-path. |
| Database unique constraint là bảo đảm cuối | Đúng; áp dụng tại Money Bank, Profile, Corebank và Consumer Inbox theo phạm vi riêng. |
| `INSERT IGNORE` luôn tốt hơn exception | Không áp dụng trực tiếp cho Oracle và không phải nguyên tắc kiến trúc chung. |
| Giới hạn Hikari/Kafka concurrency đảm bảo DB không bottleneck | Chưa đủ căn cứ; phải sizing theo partition, latency, DB capacity và load/soak test. |

## 6. Traceability sau validation

| Nguồn | Thiết kế/kiểm soát |
|---|---|
| BRD 2.4, 4.5 | Money Bank orchestration, Profile ownership, private internal API |
| BRD 3.1.4–3.1.5 | Profile Proposal/audit; CMS approval channel |
| BRD 3.2.3–3.2.6 | End-to-end idempotency, Corebank atomic transfer và ambiguous outcome |
| SRD ADR-02/03/05/08 | Database ownership, durable idempotency, no blind retry và Outbox |
| Câu trả lời ARC/SEC/OPS/AUD/RES | Gateway boundary, token exchange, fail-closed, actor audit, Profile idempotency |

## 7. Điều kiện cho giai đoạn tiếp theo

- Application Design phải tạo component boundary, public/internal API catalog và sequence diagram cho Proposal/Transfer.
- IAM design phải có PoC Standard Token Exchange V2 trước implementation.
- Data design phải đặc tả claim-first, canonical request hash và duplicate-response semantics cho Oracle/JPA.
- Infrastructure Design phải chọn Docker/Kubernetes/VM mapping, private exposure và mTLS lifecycle.
- Không triển khai code từ SRD trực tiếp trước khi Application Design được review.

