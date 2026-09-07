# Câu hỏi xác minh yêu cầu — Xác minh thiết kế

**Trạng thái:** `ANSWERED_AND_APPROVED` — Đã trả lời và phê duyệt  
**Hướng dẫn:** Điền từng trường `[Answer]:` bằng chữ cái của phương án và các ràng buộc bổ sung, ví dụ: `A — Sử dụng Kubernetes, đã có Keycloak`. Chưa chỉnh sửa BRD/SRD ở bước này.

## Cổng phê duyệt việc xác minh

### VAL-001 — Phê duyệt workflow xác minh

Bạn có phê duyệt kế hoạch tại `../plans/design-validation-plan.md` trước khi thực hiện bất kỳ thay đổi nào với SRD không?

A) Phê duyệt toàn bộ kế hoạch hiện tại. **(Khuyến nghị)**  
B) Chỉ phê duyệt phạm vi xác minh API Gateway/bảo mật.  
C) Điều chỉnh kế hoạch trước khi trả lời câu hỏi kiến trúc.  
D) Tạm dừng task xác minh này.

[Answer]: A — Phê duyệt toàn bộ kế hoạch hiện tại.

## Câu hỏi kiến trúc — Mức chặn

### ARC-001 — Trách nhiệm của API Gateway

Những traffic nào phải đi qua API Gateway?

A) Chỉ traffic north-south từ Mobile/Web/CMS; lời gọi nội bộ giữa các service dùng service discovery/load balancer private. **(Khuyến nghị)**  
B) Cả traffic north-south và mọi lời gọi east-west đều dùng chung một API Gateway.  
C) Dùng Gateway bên ngoài và Gateway nội bộ tách biệt.  
D) Không sử dụng API Gateway trong phase 1.

[Answer]: A — API Gateway chỉ tiếp nhận traffic north-south. API nội bộ được ẩn bằng private endpoint/service discovery và NetworkPolicy/firewall; lời gọi east-west không vòng qua external Gateway.

### ARC-002 — Đường đi Proposal từ Mobile/Web

Khi khách hàng tạo hoặc truy vấn Proposal tài khoản, đường đi nào là chính thức?

A) Mobile/Web → Gateway → Money Bank → Profile Service, phù hợp ranh giới điều phối trong BRD hiện tại. **(Khuyến nghị để nhất quán với BRD)**  
B) Mobile/Web → Gateway → Profile Service trực tiếp.  
C) Mobile/Web → Gateway/BFF → Profile Service; Money Bank không tham gia.  
D) Đường đi khác; mô tả trong câu trả lời.

[Answer]: A — Mobile/Web → Gateway → Money Bank → Profile Service.

### ARC-003 — Đường đi Proposal từ CMS

CMS nên truy cập API Proposal theo cách nào?

A) CMS → Gateway → Profile Service; Profile sở hữu việc phân quyền và trạng thái Proposal. **(Khuyến nghị)**  
B) CMS → Gateway → Money Bank → Profile Service.  
C) CMS → Gateway nội bộ private → Profile Service.  
D) CMS truy cập trực tiếp Profile Service mà không qua Gateway.

[Answer]: D — CMS Angular → Gateway → CMS Backend → Profile Service qua private API. CMS là kênh phê duyệt; Profile sở hữu Proposal, trạng thái và lịch sử.

### SEC-001 — Baseline bảo vệ traffic east-west

Lời gọi Money Bank → Profile Service nên được bảo vệ như thế nào?

A) Chỉ expose trong mạng private + định danh workload bằng TLS/mTLS + OAuth2 access token + allow-list bằng NetworkPolicy/firewall. **(Khuyến nghị)**  
B) Mạng private + OAuth2 token, không dùng mTLS.  
C) Chỉ dùng mạng private.  
D) Dùng API key dùng chung giữa các service.

[Answer]: A theo kiến trúc mục tiêu, triển khai theo giai đoạn — private endpoint + TLS + OAuth2 token đúng audience + NetworkPolicy/firewall là baseline; bổ sung mTLS/workload identity khi nền tảng hỗ trợ cấp và xoay certificate tự động.

### SEC-002 — Truyền định danh người dùng

Đối với Proposal do người dùng khởi tạo, Money Bank phải trình định danh nào cho Profile Service?

A) Exchange/delegate user token thành token ngắn hạn có `aud=profile-service`, đồng thời giữ `sub` của người dùng và Money Bank là actor/client. **(Khuyến nghị)**  
B) Chuyển tiếp nguyên trạng user access token ban đầu.  
C) Chỉ dùng client-credentials token của Money Bank và gửi user ID qua header.  
D) Chỉ dùng định danh Money Bank; Profile không cần biết người dùng cuối.

[Answer]: A — Money Bank dùng Standard Token Exchange V2 để nhận token ngắn hạn có `aud=profile-service`; Profile lấy user subject từ token đã xác minh và audit thêm service actor.

### SEC-003 — Quyền sở hữu kiểm tra phân quyền

Việc phân quyền được thực thi ở đâu?

A) Gateway kiểm tra thô; Money Bank kiểm tra quyền kênh/use case; Profile kiểm tra cuối cùng về ownership/role/trạng thái Proposal. **(Khuyến nghị)**  
B) Gateway là điểm thực thi phân quyền duy nhất.  
C) Money Bank là điểm thực thi phân quyền duy nhất.  
D) Profile Service là điểm thực thi phân quyền duy nhất.

[Answer]: A — Gateway kiểm tra thô; Money Bank/CMS Backend kiểm tra quyền use case; Profile quyết định cuối về ownership, role và trạng thái Proposal.

### SEC-004 — Khả năng của IAM

IAM hiện có khả năng nào cho lời gọi ủy quyền giữa các service?

A) Có thể cấu hình token exchange/delegation trên Keycloak. **(Khuyến nghị nếu tổ chức hỗ trợ)**  
B) Keycloak chỉ hỗ trợ client credentials và user token thông thường.  
C) Một IAM/STS khác cung cấp cơ chế on-behalf-of.  
D) Chưa rõ khả năng IAM và cần làm proof of concept.

[Answer]: A có điều kiện — dùng Keycloak Standard Token Exchange V2 trên phiên bản được hỗ trợ chính thức; phải làm PoC kiểm tra audience, scope, subject và service actor trước implementation. Không giả định Keycloak tự biểu diễn đầy đủ delegation chain.

### SEC-005 — Nền tảng nội bộ

Thiết kế nên giả định khả năng deployment/network nào?

A) Kubernetes với private Service, NetworkPolicy và certificate workload tự động/service mesh.  
B) Kubernetes với private Service và NetworkPolicy, không có service mesh. **(Baseline thực tế được khuyến nghị)**  
C) VM/private subnet với internal load balancer và firewall rule.  
D) Chưa quyết định nền tảng.

[Answer]: D — Chưa quyết định nền tảng. Thiết kế logic phải độc lập deployment; mapping Docker/Kubernetes/VM được thực hiện trong Infrastructure Design.

## Câu hỏi vận hành và audit — Ưu tiên cao

### OPS-001 — Hành vi khi IAM không khả dụng

Nếu không lấy được delegated token do IAM không khả dụng, một lệnh tạo Proposal mới phải xử lý thế nào?

A) Fail closed và trả lỗi kỹ thuật có thể retry; không gọi Profile khi thiếu định danh hợp lệ. **(Khuyến nghị)**  
B) Fallback sang client token của Money Bank cộng với user header.  
C) Đưa request vào hàng đợi để thực hiện bất đồng bộ sau.  
D) Cho phép lời gọi chỉ dựa vào mạng private.

[Answer]: A — Fail closed, trả lỗi kỹ thuật có thể retry và không gọi Profile khi thiếu token hợp lệ.

### AUD-001 — Chuỗi chủ thể bắt buộc trong audit

Profile phải audit những định danh nào đối với command do người dùng khởi tạo?

A) Subject người dùng cuối, client/service gọi, kênh, quyết định, đối tượng và các reference correlation/trace. **(Khuyến nghị)**  
B) Chỉ subject người dùng cuối.  
C) Chỉ service gọi.  
D) Chỉ correlation ID.

[Answer]: A — Audit end-user subject, client/service gọi, kênh, quyết định, đối tượng, correlation ID và trace ID.

### RES-001 — Quyền sở hữu idempotency

Việc chống trùng bền vững cho Proposal command phải nằm ở đâu?

A) Profile Service sở hữu durable unique constraint; Money Bank truyền cùng một idempotency key xuyên suốt. **(Khuyến nghị)**  
B) Chỉ Money Bank.  
C) Chỉ API Gateway.  
D) Redis dùng chung bởi hai service.

[Answer]: A — Profile sở hữu durable unique constraint; Money Bank truyền cùng idempotency key end-to-end.

## Ràng buộc bổ sung

Ghi tại đây các tiêu chuẩn của tổ chức, sản phẩm Gateway/IAM hiện có, công nghệ bị cấm, mục tiêu độ trễ hoặc yêu cầu tuân thủ.

[Answer]: Nền tảng mô phỏng/MVP; giao tiếp REST hiện tại; Spring Boot 4; deployment chưa chốt; `common-service` là shared library không có runtime/database; Corebank demo dùng Oracle và là system of record cho số dư/giao dịch.
