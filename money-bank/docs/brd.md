# TÀI LIỆU ĐẶC TẢ YÊU CẦU PHẦN MỀM

**Tên dự án:** Hệ thống quản lý giao dịch và thông tin liên quan đến tiền của người dùng

**Mã dự án:** MONEY-BANK

**Mã số:**

**Lần sửa đổi:** 1

**Trạng thái:** Dự thảo

## Soạn thảo

| STT | Họ và tên | Vai trò | Đơn vị | Chữ ký |
|---:|---|---|---|---|
| | | | | |

## Mục lục

- [1. Giới thiệu](#1-giới-thiệu)
- [2. Tổng quan](#2-tổng-quan)
- [3. Đặc tả yêu cầu chức năng](#3-đặc-tả-yêu-cầu-chức-năng)
- [4. Đặc tả yêu cầu phi chức năng](#4-đặc-tả-yêu-cầu-phi-chức-năng)
- [5. Nội dung cần tiếp tục làm rõ](#5-nội-dung-cần-tiếp-tục-làm-rõ)

## 1. Giới thiệu

### 1.1. Mục đích

Tài liệu mô tả yêu cầu nghiệp vụ phase 1 của Money Bank, làm cơ sở cho quá trình phân tích, thiết kế, phát triển, tích hợp và kiểm thử backend phục vụ ứng dụng mobile và web.

Phase 1 gồm hai nhóm chức năng:

1. Tạo mới và quản lý thông tin tài khoản thanh toán (TKTT).
2. Chuyển tiền nội bộ trong cùng ngân hàng.

### 1.2. Phạm vi

Trong phạm vi:

- Cung cấp API backend cho mobile và web.
- Tiếp nhận yêu cầu tạo mới, sửa thông tin và xóa/đóng TKTT.
- Yêu cầu tạo, sửa và xóa/đóng TKTT đều phải được giao dịch viên (GDV) phê duyệt trên CMS.
- Quản lý trạng thái và toàn bộ lịch sử thay đổi của yêu cầu.
- Gọi Corebank thực hiện nghiệp vụ TKTT sau khi yêu cầu được phê duyệt.
- Lấy thông tin tài khoản nguồn và số dư từ Corebank.
- Tìm kiếm, xác minh tài khoản thụ hưởng nội bộ.
- Tạo lệnh chuyển tiền nội bộ trên Corebank và trả kết quả cho mobile/web.

Ngoài phạm vi phase 1:

- Chuyển tiền liên ngân hàng và chuyển tiền quốc tế.
- Quản lý thẻ, khoản vay và tiền gửi có kỳ hạn.
- Thiết kế chi tiết giao diện mobile, web và CMS.
- Các chức năng chưa được mô tả hoặc chưa được phê duyệt trong tài liệu này.

### 1.3. Giải thích thuật ngữ và các từ viết tắt

| STT | Viết tắt và thuật ngữ | Giải thích |
|---:|---|---|
| 1 | Money Bank | Backend cung cấp API cho mobile/web và điều phối nghiệp vụ chuyển tiền |
| 2 | TKTT | Tài khoản thanh toán |
| 3 | Corebank | Hệ thống ngân hàng lõi, nguồn dữ liệu chính thức của tài khoản, số dư và giao dịch |
| 4 | Profile Service | Dịch vụ sở hữu yêu cầu quản lý TKTT, trạng thái và lịch sử yêu cầu |
| 5 | CMS | Hệ thống để GDV tiếp nhận và phê duyệt/từ chối yêu cầu |
| 6 | GDV | Giao dịch viên |
| 7 | Proposal | Yêu cầu tạo mới, sửa hoặc xóa/đóng TKTT |
| 8 | `proposal_log` | Bảng lưu lịch sử tạo và thay đổi Proposal tại Profile Service |
| 9 | Tài khoản nguồn | TKTT bị ghi nợ khi chuyển tiền |
| 10 | Tài khoản đích | TKTT nhận tiền trong giao dịch chuyển tiền nội bộ |
| 11 | Idempotency key | Khóa duy nhất dùng để ngăn một lệnh chuyển tiền bị xử lý lặp |
| 12 | TBD | Nội dung chưa được chốt, cần tiếp tục làm rõ |

### 1.4. Tài liệu tham khảo

| STT | Tên tài liệu | Giải thích |
|---:|---|---|
| 1 | Tài liệu API Corebank | TBD |
| 2 | Quy định mở và quản lý TKTT | TBD |
| 3 | Quy định hạn mức và chuyển tiền nội bộ | TBD |

## 2. Tổng quan

### 2.1. Phát biểu bài toán

Mobile và web cần cho phép khách hàng gửi yêu cầu tạo mới, sửa hoặc xóa/đóng TKTT và thực hiện chuyển tiền nội bộ.

Mọi yêu cầu làm thay đổi TKTT phải được GDV kiểm tra và phê duyệt trên CMS trước khi thực thi trên Corebank. Lịch sử tạo yêu cầu, thay đổi dữ liệu, thay đổi trạng thái và kết quả xử lý phải được lưu để phục vụ tra soát.

Đối với chuyển tiền nội bộ, Money Bank phải lấy thông tin tài khoản và số dư từ Corebank, xác minh tài khoản thụ hưởng nội bộ, tạo lệnh chuyển tiền trên Corebank và trả kết quả nhất quán cho kênh giao dịch.

### 2.2. Chức năng của hệ thống

| STT | Chức năng chính | Mô tả |
|---:|---|---|
| 1 | Tạo mới TKTT | Tiếp nhận yêu cầu, trình GDV phê duyệt và mở tài khoản trên Corebank sau phê duyệt |
| 2 | Sửa thông tin TKTT | Tiếp nhận thay đổi, lưu dữ liệu trước/sau, trình phê duyệt và cập nhật Corebank |
| 3 | Xóa/đóng TKTT | Tiếp nhận yêu cầu, trình phê duyệt và thực thi trên Corebank |
| 4 | Quản lý Proposal | Tra cứu chi tiết, trạng thái và lịch sử xử lý yêu cầu TKTT |
| 5 | Phê duyệt Proposal | Cho phép GDV trên CMS phê duyệt hoặc từ chối yêu cầu |
| 6 | Tra cứu tài khoản và số dư | Lấy thông tin tài khoản nguồn và số dư khả dụng từ Corebank |
| 7 | Tìm tài khoản thụ hưởng | Tìm và xác minh tài khoản đích trong cùng ngân hàng |
| 8 | Chuyển tiền nội bộ | Kiểm tra điều kiện và tạo lệnh chuyển tiền trên Corebank |
| 9 | Tra cứu kết quả giao dịch | Trả trạng thái và mã tham chiếu của giao dịch chuyển tiền |

### 2.3. Người sử dụng hệ thống

| STT | Đối tượng | Mô tả |
|---:|---|---|
| 1 | Khách hàng | Sử dụng mobile/web để gửi yêu cầu TKTT và chuyển tiền |
| 2 | GDV | Sử dụng CMS để kiểm tra, phê duyệt hoặc từ chối Proposal |
| 3 | Cán bộ vận hành/tra soát | Tra cứu yêu cầu, lịch sử và giao dịch theo phân quyền |
| 4 | Hệ thống tích hợp | Money Bank, Profile Service, CMS và Corebank |

### 2.4. Phân định trách nhiệm hệ thống

| Hệ thống | Trách nhiệm |
|---|---|
| Money Bank | Cung cấp API cho mobile/web; xác thực và chuyển yêu cầu TKTT sang Profile Service; điều phối tra cứu và chuyển tiền |
| Profile Service | Sở hữu Proposal, trạng thái và `proposal_log`; cung cấp dữ liệu cho CMS; thực thi yêu cầu TKTT đã được duyệt qua Corebank |
| CMS | Cung cấp màn hình để GDV xem và ra quyết định; không sở hữu dữ liệu Proposal và không gọi trực tiếp Corebank |
| Corebank | Nguồn dữ liệu chính thức; thực hiện tạo/sửa/đóng TKTT và chuyển tiền |

Mỗi dịch vụ chỉ truy cập cơ sở dữ liệu do chính dịch vụ đó sở hữu. Money Bank và CMS không truy cập trực tiếp bảng `proposal_log`.

## 3. Đặc tả yêu cầu chức năng

### 3.1. Tạo mới và quản lý thông tin TKTT

#### 3.1.1. Tạo mới TKTT

**Mục đích:** Cho phép khách hàng gửi yêu cầu mở TKTT từ mobile/web.

**Điều kiện đầu vào:**

- Khách hàng đã đăng nhập và được xác thực hợp lệ.
- Khách hàng có hồ sơ đáp ứng điều kiện mở TKTT; điều kiện chi tiết: TBD.
- Loại tài khoản và loại tiền tệ được hỗ trợ: TBD.

**Luồng chính:**

| Bước | Mô tả | Người/Hệ thống thực hiện |
|---:|---|---|
| 1 | Khách hàng nhập thông tin và gửi yêu cầu | Mobile/Web |
| 2 | Xác thực người dùng và kiểm tra dữ liệu đầu vào | Money Bank |
| 3 | Chuyển yêu cầu sang Profile Service | Money Bank |
| 4 | Tạo Proposal ở trạng thái `PENDING_APPROVAL` và ghi `proposal_log` | Profile Service |
| 5 | Hiển thị yêu cầu chờ xử lý | CMS/Profile Service |
| 6 | Kiểm tra và phê duyệt hoặc từ chối | GDV trên CMS |
| 7 | Nếu được duyệt, cập nhật `APPROVED`, gọi Corebank mở TKTT và ghi lịch sử | Profile Service |
| 8 | Cập nhật `COMPLETED` nếu thành công hoặc `FAILED` nếu không thành công | Profile Service |
| 9 | Trả trạng thái để khách hàng tra cứu | Profile Service/Money Bank |

**Đầu ra:** Mã Proposal, trạng thái xử lý; khi thành công có số/mã tài khoản do Corebank trả về.

#### 3.1.2. Sửa thông tin TKTT

- Khách hàng chỉ được sửa các trường được nghiệp vụ cho phép; danh sách trường: TBD.
- Hệ thống phải kiểm tra TKTT tồn tại, thuộc khách hàng và có trạng thái cho phép sửa.
- Proposal phải lưu dữ liệu hiện tại, dữ liệu đề nghị và danh sách trường thay đổi.
- Mọi yêu cầu sửa phải được GDV phê duyệt.
- Chỉ cập nhật Corebank sau khi yêu cầu được phê duyệt.
- Khi GDV từ chối, Corebank không được cập nhật.
- Luồng trạng thái tương tự yêu cầu tạo mới TKTT.

#### 3.1.3. Xóa/đóng TKTT

- Mọi yêu cầu xóa/đóng TKTT phải được GDV phê duyệt.
- Hệ thống phải kiểm tra tài khoản tồn tại, thuộc khách hàng và đủ điều kiện đóng.
- Điều kiện về số dư, giao dịch treo, phong tỏa, phí còn nợ và tài khoản liên kết: TBD.
- Khi GDV từ chối, Corebank không được thay đổi.
- Hệ thống không xóa vật lý Proposal, log hoặc dữ liệu lịch sử.
- Thuật ngữ nghiệp vụ chính thức là “xóa” hay “đóng tài khoản” cần được xác nhận với Corebank.

#### 3.1.4. Quản lý Proposal và `proposal_log`

Profile Service là hệ thống sở hữu Proposal và `proposal_log`.

| Trường Proposal | Bắt buộc | Mô tả |
|---|:---:|---|
| Proposal ID | Y | Mã yêu cầu duy nhất |
| Proposal type | Y | `CREATE_ACCOUNT`, `UPDATE_ACCOUNT` hoặc `CLOSE_ACCOUNT` |
| Customer ID/CIF | Y | Định danh khách hàng |
| Account ID/number | Có điều kiện | Bắt buộc với sửa và đóng TKTT |
| Request data | Y | Dữ liệu đề nghị tạo hoặc thay đổi |
| Current data | Có điều kiện | Dữ liệu trước thay đổi đối với sửa/đóng |
| Status | Y | Trạng thái hiện tại |
| Created by/at | Y | Người và thời điểm tạo |
| Updated by/at | Y | Người và thời điểm cập nhật gần nhất |
| Reviewer | Có điều kiện | GDV ra quyết định |
| Review reason | Có điều kiện | Bắt buộc khi từ chối |
| Corebank reference | Có điều kiện | Mã tham chiếu do Corebank trả về |

`proposal_log` phải ghi tối thiểu: Proposal ID, hành động, trạng thái trước/sau, dữ liệu thay đổi hoặc bản chụp phù hợp, người/hệ thống thực hiện, thời điểm, correlation ID và lý do.

#### 3.1.5. Phê duyệt trên CMS

- CMS lấy danh sách và chi tiết Proposal từ Profile Service theo phân quyền GDV.
- GDV được phê duyệt hoặc từ chối Proposal đang ở `PENDING_APPROVAL`.
- Lý do từ chối là bắt buộc.
- Chỉ quyết định hợp lệ đầu tiên được ghi nhận; thao tác lặp/đồng thời không được làm yêu cầu xử lý hai lần.
- Mọi quyết định phải được ghi `proposal_log`.
- CMS không gọi trực tiếp Corebank và không cập nhật trực tiếp database của Profile Service.

#### 3.1.6. Trạng thái Proposal

```text
PENDING_APPROVAL
    ├── REJECTED
    └── APPROVED → PROCESSING → COMPLETED
                              └→ FAILED
```

Quy tắc khách hàng hủy yêu cầu, GDV yêu cầu bổ sung và retry khi `FAILED`: TBD.

### 3.2. Chuyển tiền nội bộ

#### 3.2.1. Tra cứu tài khoản nguồn và số dư

- Money Bank lấy danh sách và thông tin TKTT của khách hàng từ Corebank.
- Chỉ trả các tài khoản thuộc khách hàng và có trạng thái cho phép ghi nợ.
- Số dư dùng để hiển thị và kiểm tra phải lấy từ Corebank, tối thiểu gồm số dư khả dụng.
- Không sử dụng số dư lưu cục bộ làm căn cứ cuối cùng để cho phép chuyển tiền.
- Số tài khoản và dữ liệu nhạy cảm phải được mask phù hợp trên log.

#### 3.2.2. Tìm kiếm tài khoản thụ hưởng nội bộ

- Phase 1 chỉ hỗ trợ tài khoản đích trong cùng ngân hàng.
- Tiêu chí tìm kiếm ban đầu là số tài khoản; tiêu chí khác: TBD.
- Money Bank gọi Corebank để xác minh tài khoản đích.
- Kết quả tối thiểu gồm tên chủ tài khoản theo quy định hiển thị, số tài khoản, loại tiền tệ và trạng thái hợp lệ/không hợp lệ.
- Không cho phép tiếp tục nếu tài khoản không tồn tại, đã đóng, bị chặn nhận tiền hoặc không đáp ứng điều kiện Corebank.
- Quy tắc chuyển giữa hai tài khoản cùng chủ sở hữu: TBD.

#### 3.2.3. Tạo lệnh chuyển tiền nội bộ

| Bước | Mô tả | Người/Hệ thống thực hiện |
|---:|---|---|
| 1 | Chọn tài khoản nguồn | Khách hàng |
| 2 | Nhập số tài khoản đích | Khách hàng |
| 3 | Xác minh và trả thông tin thụ hưởng từ Corebank | Money Bank/Corebank |
| 4 | Nhập số tiền, nội dung và xác nhận thông tin | Khách hàng |
| 5 | Xác thực giao dịch theo cơ chế được quy định | Mobile/Web/Money Bank |
| 6 | Kiểm tra dữ liệu, quyền sở hữu, trạng thái, loại tiền, hạn mức và idempotency | Money Bank |
| 7 | Gửi lệnh chuyển tiền nội bộ | Money Bank/Corebank |
| 8 | Kiểm tra số dư tại thời điểm xử lý, hạch toán và trả mã tham chiếu | Corebank |
| 9 | Trả kết quả cho mobile/web | Money Bank |

| Dữ liệu đầu vào | Bắt buộc | Mô tả |
|---|:---:|---|
| Source account | Y | Tài khoản nguồn thuộc khách hàng |
| Destination account | Y | Tài khoản đích nội bộ đã được xác minh |
| Amount | Y | Số tiền lớn hơn 0 |
| Currency | Y | Loại tiền hỗ trợ: TBD |
| Description | TBD | Nội dung chuyển tiền và giới hạn ký tự: TBD |
| Idempotency key | Y | Khóa duy nhất cho một yêu cầu chuyển tiền |
| Authentication data | Y | Dữ liệu xác thực giao dịch theo cơ chế được chốt |

Kết quả tối thiểu gồm mã giao dịch Money Bank, mã tham chiếu Corebank, trạng thái, số tiền, loại tiền, tài khoản nguồn/đích đã mask và thời gian giao dịch.

#### 3.2.4. Quy tắc nghiệp vụ chuyển tiền

- Tài khoản nguồn phải thuộc khách hàng đang đăng nhập.
- Tài khoản nguồn và đích phải tồn tại và có trạng thái cho phép giao dịch.
- Số tiền phải lớn hơn 0 và không vượt quá số dư khả dụng sau khi tính phí, nếu có.
- Hạn mức theo giao dịch/ngày/tháng, phí và loại tiền hỗ trợ: TBD.
- Corebank là nguồn quyết định cuối cùng về số dư và kết quả hạch toán.
- Mỗi yêu cầu phải có idempotency key. Gửi lại cùng khóa và cùng dữ liệu không được tạo giao dịch mới.
- Gửi lại cùng idempotency key nhưng khác dữ liệu phải bị từ chối.
- Khi timeout nhưng chưa xác định kết quả Corebank, không được tự động kết luận thất bại hoặc tạo lệnh mới.
- Không ghi thông tin xác thực hoặc dữ liệu nhạy cảm vào application log.

#### 3.2.5. Trạng thái giao dịch

| Trạng thái | Ý nghĩa |
|---|---|
| `PROCESSING` | Đã tiếp nhận và đang xử lý |
| `SUCCESS` | Corebank xác nhận hạch toán thành công |
| `FAILED` | Corebank xác nhận giao dịch thất bại |
| `UNKNOWN` | Chưa xác định kết quả do timeout/lỗi kết nối, cần truy vấn lại hoặc tra soát |

#### 3.2.6. Các trường hợp lỗi chính

| Trường hợp | Kết quả mong đợi |
|---|---|
| Không tìm thấy tài khoản nguồn/đích | Từ chối và trả lỗi nghiệp vụ phù hợp |
| Tài khoản không hoạt động hoặc bị phong tỏa | Từ chối giao dịch |
| Tài khoản nguồn không thuộc khách hàng | Từ chối và không để lộ dữ liệu tài khoản |
| Số dư khả dụng không đủ | Từ chối theo kết quả Corebank |
| Vượt hạn mức | Từ chối và thông báo phù hợp |
| Xác thực giao dịch sai hoặc hết hiệu lực | Từ chối giao dịch |
| Idempotency key đã dùng với dữ liệu khác | Từ chối yêu cầu |
| Corebank timeout/chưa rõ kết quả | Trả `UNKNOWN` hoặc `PROCESSING`, không tự động tạo lệnh mới |
| Corebank xác nhận lỗi | Trả `FAILED` cùng mã lỗi đã được ánh xạ an toàn |

## 4. Đặc tả yêu cầu phi chức năng

### 4.1. Yêu cầu về hiệu năng

- SLA phản hồi cho từng API, số người dùng đồng thời và sản lượng giao dịch: TBD.
- Mọi kết nối Corebank và dịch vụ nội bộ phải có timeout phù hợp.
- Các API danh sách phải hỗ trợ phân trang.

### 4.2. Yêu cầu an toàn bảo mật

- Mọi API phải xác thực và phân quyền theo đối tượng sử dụng.
- API phê duyệt chỉ dành cho GDV có quyền phù hợp.
- Dữ liệu truyền giữa các hệ thống phải được mã hóa bằng TLS.
- Dữ liệu nhạy cảm phải được mask trên giao diện, phản hồi và log theo quy định.
- Không lưu thông tin bí mật xác thực ở dạng rõ.
- Mọi Proposal, quyết định phê duyệt và giao dịch chuyển tiền phải có audit trail.
- Cơ chế OTP/soft token/sinh trắc học và chính sách session: TBD.

### 4.3. Yêu cầu vận hành lưu trữ

- Proposal và `proposal_log` được lưu tại Profile Service.
- Dữ liệu phục vụ truy vết giao dịch được lưu tại Money Bank; Corebank là nguồn hạch toán chính thức.
- Mọi yêu cầu xuyên hệ thống phải có correlation ID/trace ID.
- Hệ thống phải có log, metric và cảnh báo cho lỗi tích hợp, timeout và giao dịch `UNKNOWN`.
- Backup, thời gian lưu trữ, RPO và RTO: TBD.

### 4.4. Yêu cầu về tính hỗ trợ

- Mã lỗi kỹ thuật phải được ánh xạ thành thông báo phù hợp cho mobile/web và CMS.
- Hỗ trợ tra cứu theo Proposal ID, mã giao dịch, mã Corebank và correlation ID theo phân quyền.
- Cung cấp tài liệu API và danh mục mã lỗi cho hệ thống tích hợp.

### 4.5. Yêu cầu về giao tiếp

- Mobile/Web ↔ Money Bank: API cho kênh khách hàng.
- Money Bank ↔ Profile Service: API nội bộ tạo và tra cứu Proposal.
- CMS ↔ Profile Service: API nội bộ tra cứu và phê duyệt/từ chối Proposal.
- Profile Service ↔ Corebank: API tạo/sửa/đóng TKTT sau phê duyệt.
- Money Bank ↔ Corebank: API tra cứu tài khoản, số dư, thụ hưởng và chuyển tiền nội bộ.
- Chuẩn giao tiếp, cơ chế ký request và API contract cụ thể: TBD.

### 4.6. Các yêu cầu khác

- Thao tác tài chính và phê duyệt phải chống xử lý lặp và có khả năng tra soát.
- Chuẩn thời gian và múi giờ sử dụng giữa các hệ thống: TBD.
- Hạn mức, phí và điều kiện TKTT phải được quản lý theo quy trình thay đổi phù hợp.

## 5. Nội dung cần tiếp tục làm rõ (khi thiết kế tài liệu SRD sẽ làm rõ)

1. Danh sách trường của yêu cầu tạo mới TKTT.
2. Các trường TKTT khách hàng được phép sửa.
3. “Xóa TKTT” là đóng tài khoản hay nghiệp vụ khác trên Corebank.
4. Điều kiện mở, sửa và đóng TKTT.
5. Loại TKTT và loại tiền hỗ trợ trong phase 1.
6. Cơ chế phân công GDV, thẩm quyền và SLA phê duyệt trên CMS.
7. Khách hàng có được hủy Proposal trước khi GDV xử lý hay không.
8. Cơ chế retry khi Corebank xử lý Proposal thất bại.
9. Cơ chế xác thực giao dịch chuyển tiền.
10. Hạn mức, phí và giới hạn nội dung chuyển tiền.
11. SLA hiệu năng và sản lượng giao dịch dự kiến.
12. Cơ chế đồng bộ và tra soát giao dịch `UNKNOWN`.
13. Chính sách lưu trữ, mã hóa, backup, RPO và RTO.
