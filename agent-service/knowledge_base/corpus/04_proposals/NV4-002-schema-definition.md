---
document_id: "NV4-002"
title: "Cấu trúc dữ liệu và Schema form"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV4"
document_type: "reference"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-04"]
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["all"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "operations"
related_documents: ["NV4-001"]
supersedes: []
---
# Cấu trúc dữ liệu và Schema form

<a id="NV4-002-D01"></a>
## 1. Nguyên tắc thiết kế Schema

Hệ thống Ngân hàng Mô phỏng sử dụng các định dạng Schema chuẩn để định nghĩa cấu trúc dữ liệu cho từng loại yêu cầu (proposal). Schema đảm bảo tính nhất quán của dữ liệu đầu vào cho cổng thanh toán Paygate cũng như các phân hệ khác. Các trường thông tin bắt buộc phải đi kèm với kiểu dữ liệu rõ ràng và mô tả nghiệp vụ cụ thể. Việc xác thực (validation) dữ liệu được thực hiện ngay tại giao diện người dùng dựa trên cấu trúc Schema này trước khi dữ liệu được đóng gói thành hồ sơ để gửi duyệt.

<a id="NV4-002-D02"></a>
## 2. Các trường dữ liệu định danh cốt lõi

Mỗi Schema đều phải chứa nhóm trường định danh cốt lõi để liên kết hồ sơ với khách hàng.
*   `customer_id`: Mã khách hàng duy nhất. Đối với cá nhân sử dụng định dạng `CIF-SIM-xxx` (ví dụ: CIF-SIM-001), đối với doanh nghiệp sử dụng `ENT-SIM-xxx` (ví dụ: Công ty Minh An - ENT-SIM-001) hoặc `MRC-SIM-xxx` cho merchant.
*   `legal_id`: Mã số định danh pháp lý (CCCD/CMND), bắt buộc sử dụng định dạng giả lập như `ID-SIM-xxx`.
*   `proposal_type`: Loại yêu cầu nghiệp vụ, ví dụ: `CUSTOMER_PERSONAL_INFORMATION_CHANGE`.

<a id="NV4-002-D03"></a>
## 3. Cấu trúc Schema cho yêu cầu thay đổi thông tin

Khi khách hàng yêu cầu thay đổi thông tin, Schema được thiết kế gồm các phần chính để đối chiếu:
*   `current_data`: Chứa dữ liệu hiện tại đang lưu trữ trên hệ thống, chỉ dùng làm cơ sở so sánh.
*   `proposed_changes`: Chứa các trường thông tin mà khách hàng yêu cầu thay đổi. 
Cần lưu ý về mặt dữ liệu: Đổi giấy tờ tùy thân của cùng một người đại diện pháp luật (NĐDPL) không phải là đổi người NĐDPL. Hệ thống yêu cầu cập nhật profile cá nhân trước, sau đó rà soát hồ sơ từng doanh nghiệp; chỉ tạo nhánh chuẩn bị yêu cầu cập nhật merchant nếu thực sự ảnh hưởng đến dữ liệu hợp đồng. Dữ liệu không tự đồng bộ giữa các thực thể mà phải thực hiện qua Schema form hợp lệ.

<a id="NV4-002-D04"></a>
## 4. Quản lý trạng thái và phiên bản trong cấu trúc

Schema chứa các trường metadata của hệ thống để CMS theo dõi vòng đời của proposal.
*   `proposal_id`: Mã hồ sơ. Chưa có trong giai đoạn chuẩn bị, chỉ được sinh ra khi bấm Tạo và gửi duyệt.
*   `status`: Trạng thái hiện tại của hồ sơ như `PENDING_APPROVAL`, `APPROVED`, `REJECTED`, `RETURNED_FOR_CORRECTION`.
*   `version`: Số phiên bản. Khi hồ sơ nhận trạng thái trả lại (`RETURNED_FOR_CORRECTION`) và GDV chỉnh sửa để gửi duyệt lại, hệ thống giữ nguyên `proposal_id` nhưng sẽ tăng `version`.