package com.hieu.moneybank.shared.idempotency;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import com.hieu.moneybank.shared.error.IdempotencyConflictException;
import com.hieu.moneybank.shared.error.InvalidRequestException;
import com.hieu.moneybank.shared.observability.LogFields;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Điều phối claim idempotency theo mô hình claim-first.
 *
 * <p>Thứ tự bắt buộc: <b>insert trước, đọc sau</b>. Cách làm ngược lại
 * ({@code SELECT} kiểm tra tồn tại rồi mới {@code INSERT}) có khoảng trống giữa
 * hai câu lệnh; hai request đồng thời đều thấy "chưa tồn tại" và đều tạo giao
 * dịch. Đó là race TOCTOU đã được ghi nhận trong {@code DV-F-04} và là lý do
 * class này không có đường nào đọc trước khi ghi.
 *
 * <p>Bean này cố tình <b>không</b> có {@code @Transactional}. Nó gọi hai bean
 * khác có ranh giới transaction riêng, vì việc xử lý duplicate phải diễn ra sau
 * khi transaction insert đã rollback.
 */
@Service
public class IdempotencyService {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);

    /**
     * Khuôn dạng key cho phép. Giới hạn charset để key an toàn khi đưa vào log/index,
     * giới hạn độ dài dưới để chặn key dễ đoán như {@code "1"} — key đoán được cho
     * phép một khách hàng chặn chính ý định hợp lệ sau này của mình.
     */
    private static final Pattern KEY_PATTERN = Pattern.compile("[A-Za-z0-9_-]{16,64}");

    private static final String METRIC_CLAIM = "moneybank.idempotency.claim";
    private static final String TAG_RESULT = "result";
    private static final String TAG_OPERATION = "operation";

    private final IdempotencyRecordWriter writer;
    private final IdempotencyRecordReader reader;
    private final IdempotencyProperties properties;
    private final MeterRegistry meterRegistry;

    public IdempotencyService(IdempotencyRecordWriter writer,
                              IdempotencyRecordReader reader,
                              IdempotencyProperties properties,
                              MeterRegistry meterRegistry) {
        this.writer = writer;
        this.reader = reader;
        this.properties = properties;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Claim một ý định command.
     *
     * @param customerId     lấy từ identity context đã xác minh, không từ request body
     * @param operation      loại command
     * @param idempotencyKey key do client sinh, phải giữ nguyên khi retry
     * @param requestHash    hash canonical của payload nghiệp vụ
     * @return {@link IdempotencyClaim.Claimed} nếu đây là request duy nhất được
     *         phép thực thi; ngược lại là trạng thái của ý định đã tồn tại
     * @throws IdempotencyConflictException nếu key đã dùng với dữ liệu khác
     */
    public IdempotencyClaim claim(String customerId,
                                  IdempotencyOperation operation,
                                  String idempotencyKey,
                                  String requestHash) {
        validate(customerId, operation, idempotencyKey, requestHash);

        String transactionId = UUID.randomUUID().toString();
        String workflowId = UUID.randomUUID().toString();
        try {
            IdempotencyRecord claimed = writer.insertClaim(
                    customerId, operation, idempotencyKey, requestHash, transactionId, workflowId);
            count("new", operation);
            log.info("eventName={} {}={} {}={} operation={}",
                    "IDEMPOTENCY_CLAIMED",
                    LogFields.TRANSACTION_ID, claimed.transactionId(),
                    LogFields.WORKFLOW_ID, claimed.workflowId(),
                    operation);
            return new IdempotencyClaim.Claimed(claimed);
        } catch (DataIntegrityViolationException duplicate) {
            // Không phải lỗi hệ thống: đây là đường đi bình thường khi client retry
            // hoặc double click. Tuyệt đối không để exception này lọt ra ngoài dưới
            // dạng 500 (AD-DC-C05).
            return resolveDuplicate(customerId, operation, idempotencyKey, requestHash, duplicate);
        }
    }

    private IdempotencyClaim resolveDuplicate(String customerId,
                                              IdempotencyOperation operation,
                                              String idempotencyKey,
                                              String requestHash,
                                              DataIntegrityViolationException duplicate) {
        int attempts = Math.max(1, properties.getDuplicateReadAttempts());
        for (int attempt = 1; attempt <= attempts; attempt++) {
            Optional<IdempotencyRecord> existing =
                    reader.findExisting(customerId, operation, idempotencyKey);
            if (existing.isPresent()) {
                return classify(existing.get(), requestHash, operation);
            }
        }
        // Insert bị chặn vì trùng khóa nhưng không đọc được bản ghi: trạng thái
        // không nhất quán, không được đoán. Ném lỗi kỹ thuật để caller trả 503 và
        // client thử lại cùng key, thay vì âm thầm tạo giao dịch mới.
        count("unresolved", operation);
        log.error("eventName={} operation={} attempts={}",
                "IDEMPOTENCY_DUPLICATE_UNRESOLVED", operation, attempts, duplicate);
        throw new IllegalStateException(
                "Trùng idempotency key nhưng không đọc được bản ghi đã tồn tại", duplicate);
    }

    private IdempotencyClaim classify(IdempotencyRecord existing,
                                      String requestHash,
                                      IdempotencyOperation operation) {
        if (!existing.matchesHash(requestHash)) {
            count("conflict", operation);
            log.warn("eventName={} {}={} operation={}",
                    "IDEMPOTENCY_CONFLICT",
                    LogFields.TRANSACTION_ID, existing.transactionId(),
                    operation);
            throw new IdempotencyConflictException(operation.name());
        }
        if (existing.status().isTerminal()) {
            count("duplicate_settled", operation);
            return new IdempotencyClaim.AlreadySettled(existing);
        }
        count("duplicate_in_progress", operation);
        return new IdempotencyClaim.InProgress(existing);
    }

    private void validate(String customerId,
                          IdempotencyOperation operation,
                          String idempotencyKey,
                          String requestHash) {
        if (!StringUtils.hasText(customerId)) {
            // Không có customer thì không có phạm vi cho key; fail closed.
            throw new InvalidRequestException("customerId rỗng khi claim idempotency");
        }
        if (operation == null) {
            throw new InvalidRequestException("operation rỗng khi claim idempotency");
        }
        if (idempotencyKey == null || !KEY_PATTERN.matcher(idempotencyKey).matches()) {
            throw new InvalidRequestException("Idempotency key không đúng khuôn dạng cho phép");
        }
        if (requestHash == null || requestHash.length() != CanonicalRequestHasher.HASH_LENGTH) {
            throw new InvalidRequestException("Request hash không đúng độ dài mong đợi");
        }
    }

    /**
     * Đếm theo {@code result} và {@code operation}. Cả hai tag đều có tập giá trị
     * hữu hạn nhỏ; không dùng customer/transaction id làm label
     * ({@code OPS-004}).
     */
    private void count(String result, IdempotencyOperation operation) {
        meterRegistry.counter(METRIC_CLAIM, TAG_RESULT, result, TAG_OPERATION, operation.name())
                .increment();
    }
}
