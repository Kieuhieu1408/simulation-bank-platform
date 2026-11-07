package com.hieu.corebank.transfer.infrastructure.idempotency;

import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.TransferId;
import com.hieu.corebank.transfer.application.port.TransferIdempotencyLockPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Redis Adapter hiện thực hóa {@link TransferIdempotencyLockPort}.
 *
 * <p>Sử dụng Lua Script nguyên tử (atomic) cho cả 2 chiều:
 * <ul>
 *   <li><b>Acquire:</b> Kiểm tra key và {@code SET ... PX ttl} trong duy nhất 1 bước; nếu đã tồn tại trả về ngay
 *       giá trị đang giữ để đối chiếu {@code requestHash} trong 1 network hop.</li>
 *   <li><b>Release:</b> Chỉ thực hiện {@code DEL} nếu giá trị đang lưu khớp chính xác với {@code lockToken} của
 *       chính tiến trình đã lấy lock, tránh tuyệt đối lỗi xóa nhầm lock của Pod khác.</li>
 * </ul>
 */
@Slf4j
@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
public class RedisTransferIdempotencyLockAdapter implements TransferIdempotencyLockPort {

    public static final String LOCK_KEY_PREFIX = "idem:transfer:lock:";
    static final String ACQUIRED_SENTINEL = "ACQUIRED";

    private static final DefaultRedisScript<String> ACQUIRE_SCRIPT = new DefaultRedisScript<>(
            """
            local existing = redis.call('GET', KEYS[1])
            if not existing then
                redis.call('SET', KEYS[1], ARGV[1], 'PX', tonumber(ARGV[2]))
                return 'ACQUIRED'
            end
            return existing
            """,
            String.class
    );

    private static final DefaultRedisScript<Long> RELEASE_SCRIPT = new DefaultRedisScript<>(
            """
            if redis.call('GET', KEYS[1]) == ARGV[1] then
                return redis.call('DEL', KEYS[1])
            else
                return 0
            end
            """,
            Long.class
    );

    private final StringRedisTemplate redisTemplate;
    private final Duration lockTtl;

    public RedisTransferIdempotencyLockAdapter(
            StringRedisTemplate redisTemplate,
            @Value("${corebank.transfer.idempotency-lock-ttl-seconds:${corebank.transfer.stuck-threshold-seconds:30}}") long ttlSeconds
    ) {
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("idempotency-lock-ttl-seconds must be positive, got: " + ttlSeconds);
        }
        this.redisTemplate = redisTemplate;
        this.lockTtl = Duration.ofSeconds(ttlSeconds);
    }

    @Override
    public LockResult tryAcquire(TransferId transferId, String requestHash) {
        String redisKey = formatKey(transferId);
        String lockToken = UUID.randomUUID().toString();
        String lockValue = lockToken + ":" + (requestHash != null ? requestHash : "");
        String ttlMillisStr = String.valueOf(lockTtl.toMillis());

        try {
            String result = redisTemplate.execute(ACQUIRE_SCRIPT, List.of(redisKey), lockValue, ttlMillisStr);
            if (result == null) {
                log.warn("Redis returned null for idempotency lock transferId={}, falling back to DB constraint",
                        transferId.value());
                return new LockResult.LockUnavailable();
            }

            if (ACQUIRED_SENTINEL.equals(result)) {
                log.debug("Acquired Redis in-flight idempotency lock for transferId={} (ttl={}s)",
                        transferId.value(), lockTtl.toSeconds());
                AtomicBoolean released = new AtomicBoolean(false);
                LockHandle handle = () -> {
                    if (released.compareAndSet(false, true)) {
                        releaseQuietly(redisKey, lockValue, transferId);
                    }
                };
                return new LockResult.Acquired(handle);
            }

            String existingHash = extractRequestHash(result);
            return new LockResult.AlreadyInFlight(existingHash);
        } catch (Exception ex) {
            log.warn("Redis unavailable for transfer idempotency lock transferId={}, falling back to DB constraint: {}",
                    transferId.value(), ex.getMessage());
            return new LockResult.LockUnavailable();
        }
    }

    private void releaseQuietly(String redisKey, String lockValue, TransferId transferId) {
        try {
            Long deleted = redisTemplate.execute(RELEASE_SCRIPT, List.of(redisKey), lockValue);
            log.debug("Released Redis in-flight idempotency lock for transferId={} (deleted={})",
                    transferId.value(), deleted);
        } catch (Exception ex) {
            log.warn("Failed to release Redis idempotency lock for transferId={} (will expire via {}s TTL): {}",
                    transferId.value(), lockTtl.toSeconds(), ex.getMessage());
        }
    }

    static String formatKey(TransferId transferId) {
        return LOCK_KEY_PREFIX + transferId.value();
    }

    static String extractRequestHash(String storedLockValue) {
        int sepIdx = storedLockValue.indexOf(':');
        if (sepIdx < 0 || sepIdx + 1 >= storedLockValue.length()) {
            return null;
        }
        return storedLockValue.substring(sepIdx + 1);
    }
}
