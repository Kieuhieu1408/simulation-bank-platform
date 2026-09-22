# Quy ước biên soạn corpus mô phỏng 1.0.0

Tài liệu quản trị, không đưa vào chỉ mục. Ngày chốt corpus: 2026-09-22, múi giờ Asia/Ho_Chi_Minh. Người dùng đã yêu cầu làm sạch, bỏ thương hiệu ngoài, làm giàu bốn nghiệp vụ và tạo nhiễu để chuẩn bị chunking/vector DB. Đây là quyết định thiết kế dữ liệu mô phỏng, không xác nhận quy định ngân hàng thật hay chức năng phần mềm đã triển khai.

## Quy ước chung bắt buộc

- Tên tổ chức duy nhất: **Ngân hàng Mô phỏng**; cổng thanh toán **Paygate**. Không tên thương hiệu, URL, hotline thật; không dẫn số hiệu luật thật để bảo chứng chính sách tự tạo. Không đọc PDF. Chỉ viết Markdown UTF-8.
- Raw Markdown là nguồn đầu vào chưa tin cậy, giữ nguyên ngoài corpus; chỉ `knowledge_base/corpus/**/*.md` là ứng viên index. Văn bản mới là biên soạn/adaptation có chọn lọc, không phải bản sao chính thức đã ẩn danh.
- Các con số, thẩm quyền, sản phẩm và doanh nghiệp được tự thiết kế trong phạm vi yêu cầu mô phỏng. Mỗi tài liệu có ghi rõ mô phỏng và phạm vi. Không giả vờ được chuyên gia ngân hàng phê duyệt.
- Khoảng ngày `[effective_from, effective_to)`: ngày kết thúc bị loại. `null` nghĩa chưa ấn định kết thúc. Tài liệu lịch sử/future vẫn có `status: published`; published chỉ nói đã ban hành trong thế giới mô phỏng, không nghĩa đang hiệu lực tại 2026-09-22.
- Điều kiện áp dụng xét thời điểm nghiệp vụ, sản phẩm, phân khúc, pháp nhân/merchant, hợp đồng, sửa đổi và quyền. Chỉ ngày ký hợp đồng không đủ quyết định.
- Mã hồ sơ giả lập: `CIF-SIM-001`, `ENT-SIM-001`, `MRC-SIM-001`, `UNIT-SIM-01`. Không CCCD/PAN/số điện thoại có vẻ thật; dùng `ID-SIM-001`. DN chính `Công ty Minh An` (ENT-SIM-001, MRC-SIM-001, F&B); DN thứ hai `Công ty Minh An Thương mại` (ENT-SIM-002, MRC-SIM-002, retail); không suy quan hệ chỉ vì gần tên.
- Roles: `gdv`, `approver`, `knowledge_admin`, `operations`. Mọi tài liệu chính sách dùng `access_roles` cả bốn, `unit_scope: ["all"]`, `customer_scope: ["all"]`. Hợp đồng/hồ sơ riêng phải giới hạn đơn vị và customer_scope chính xác. Admin tri thức không tự được đọc hợp đồng khách hàng: các file riêng bỏ knowledge_admin khỏi access_roles.
- Mọi file có H1 và ít nhất 4 mục nghiệp vụ có anchor HTML rõ ` <a id="NV1-001-D01"></a> ` ngay trước H2. ID anchor bắt đầu document_id. Không tạo hàng loạt đoạn giống nhau để tăng số lượng. Viết bảng có tên cột và đơn vị; điều kiện/ngoại lệ không tách khỏi kết luận.
- Dùng `[nhãn](../02_cards/filename.md#NV2-001-D01)` cho liên kết nếu biết đường dẫn; hoặc mã document_id trong prose. `related_documents` chỉ liệt kê ID thật đã dự kiến. Không ghi đáp án eval/chú thích 'nhiễu' trong nội dung policy. Không thêm câu hỏi và đáp án kiểm thử vào corpus.

## Frontmatter bắt buộc

```yaml
---
document_id: "NV1-001"
title: "Quy định thay đổi thông tin cá nhân"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV1"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-01"]
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["customer_profile"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV1-002", "NV4-002"]
supersedes: []
---
```

`document_type`: policy, procedure, tariff, contract, amendment, case_record, reference. `source_type`: synthetic_adaptation (từ raw), synthetic_enrichment (bổ sung; source_refs []). Không invent provenance. `supersedes` dùng ID tài liệu chỉ khi thay toàn bộ; sửa một phần ghi cụ thể điều/phạm vi ở nội dung và related_documents, không khai toàn bộ tài liệu cũ hết hiệu lực.

## Nguồn đầu vào dùng mã ẩn danh

- RAW-REG-01: nhóm 8 Markdown mẫu gần trùng trong raw regulations (tất cả trừ merchant dài). Đọc bản đại diện khi cần; không đưa tên riêng hoặc số hiệu pháp lý sang corpus.
- RAW-REG-02: Markdown điều khoản merchant dài có nguồn URL; tham khảo merchant onboarding, changes, settlement, refund, installment; viết lại trong bối cảnh Paygate.
- RAW-INT-01: nv1_cross_system_update.md.
- RAW-INT-02: nv2_card_operations.md.
- RAW-INT-03: nv3_fee_schedules_conflicts.md.
- RAW-INT-04: cms_approval_guidelines.md.

## Quyết định nghiệp vụ xuyên miền

1. Chatbot chỉ tìm/đọc/hỏi/giải thích/chuẩn bị. Không tạo, gửi, phê duyệt, khóa thẻ, tính lại số dư hay báo đã cập nhật hệ thống. CMS/adapter mô phỏng mới xác nhận trạng thái. APPROVED không là EXECUTED.
2. Loại yêu cầu MVP `CUSTOMER_PERSONAL_INFORMATION_CHANGE`. Mã raw PROPOSAL-KYC-01 chỉ là alias lịch sử của loại này; PROPOSAL-CARD-02 là alias cho CARD_CLOSURE_REVIEW, loại hướng dẫn, chưa là thao tác của AI.
3. CMS dùng nút Tạo và gửi duyệt -> PENDING_APPROVAL; APPROVED/REJECTED/RETURNED_FOR_CORRECTION; trả lại giữ proposal_id và tăng version khi gửi lại. Chuẩn bị chưa có proposal_id. Một người đủ thẩm quyền quyết định; GDV tạo không phải cấp duyệt. TĐV duyệt hồ sơ đơn vị thông thường; liên quan nhiều đơn vị/đổi NĐDPL/đổi tài khoản nhận tiền merchant -> cấp vùng có scope tương ứng. Không tự duyệt; không tự gán người cụ thể bằng LLM.
4. Đổi CCCD của cùng một NĐDPL không phải đổi người NĐDPL. Cập nhật profile cá nhân trước; rà soát hồ sơ từng DN/vai trò; chỉ chuẩn bị yêu cầu merchant nếu thực sự ảnh hưởng dữ liệu hợp đồng/đầu mối có quyền. Không tự đồng bộ. Người ủy quyền hết hạn không tự gia hạn/xóa/đổi quyền.
5. NV3 là chủ quy tắc chọn biểu phí merchant. Standard cũ đến 2026-06-01: whole-volume <500tr 1.5%, [500tr,2tỷ] 1.2%, >2tỷ 1.0%. Standard mới từ 2026-06-01: 1.8% whole-volume (phân khúc retail/default). F&B riêng cả năm 2026: <100tr 2.0%, [100tr,500tr] 1.7%, >500tr 1.4%, whole-volume. Không gộp hai bảng như mâu thuẫn vô điều kiện. Có thêm tariff progressive cho gói riêng; ghi rõ khác product_scope.
6. Hợp đồng fixed giữ mức đã ký trong kỳ nếu không có phụ lục thay điều đó; hợp đồng floating dẫn chiếu bảng tại thời điểm nghiệp vụ. Campaign chỉ override khi hợp đồng/policy cho phép và merchant đăng ký/đáp ứng điều kiện, không đè tất cả. Mọi công thức xác định VAT/rounding; các giả định thuế chỉ là tham số bài mô phỏng, không pháp luật.
7. NV2 là chủ phí thẻ/trả góp, NV3 không lặp mức phí thẻ; NV4 chỉ tham chiếu điều kiện và hồ sơ, không tự đặt thêm mức phí.
8. Nhánh có đủ căn cứ cần kết luận được; nhánh thiếu không suy đoán. Hồ sơ DN/case_record là snapshot giả lập thời điểm ghi, không kết nối dữ liệu sống. Nhiễu hợp lệ đến từ từ khóa gần nhau, sản phẩm/kênh/khách hàng khác, điều khoản gần giống, sửa một phần, lịch sử/tương lai, phạm vi hợp đồng và lỗi thời có căn cứ.

## Phân công và ID đã giữ chỗ

- Parent: COM-001 (phạm vi/thuật ngữ), COM-002 (hiệu lực/quyền/chọn nguồn), NV4-001..008 (quy trình, schema, hồ sơ, thẩm quyền, lỗi, quản trị/handoff, lịch sử, biểu mẫu khác).
- NV1: NV1-001..010 trong corpus/01_customer; ghi rõ danh mục ID và file khi hoàn thành. NV1-001 thay đổi cá nhân, NV1-002 quan hệ/ủy quyền, NV1-003 merchant onboarding/update, NV1-004 đối soát, NV1-005 refund/chấm dứt; 006..010 mở rộng/lịch sử/hồ sơ doanh nghiệp.
- NV2: NV2-001..008 trong corpus/02_cards; 001 đóng thẻ, 002 trả góp/phí, 003 thẻ phụ gốc, 004 sửa đổi thẻ tín dụng, 005 mất thẻ, 006 đóng tài khoản thanh toán, 007 hạn mức, 008 biến thể tương lai.
- NV3: NV3-001..011 trong corpus/03_fees; 001 chọn chính sách, 002 default cũ, 003 default mới, 004 F&B, 005 progressive, 006 campaign, 007 hợp đồng ENT-SIM-001 fixed, 008 ENT-SIM-002 floating, 009 phụ lục 007, 010 default tương lai, 011 phí tài khoản doanh nghiệp.
