package com.hieu.corebank.transfer.infrastructure.idempotency;

import com.hieu.corebank.shared.domain.TransferId;
import com.hieu.corebank.transfer.application.port.TransferIdempotencyLockPort.LockResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisTransferIdempotencyLockAdapter Unit Tests")
class RedisTransferIdempotencyLockAdapterTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    private RedisTransferIdempotencyLockAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RedisTransferIdempotencyLockAdapter(redisTemplate, 30L);
    }

    @Test
    @DisplayName("Lấy khóa thành công (ACQUIRED) và xóa khóa đúng 1 lần bằng cùng lockToken khi release()")
    @SuppressWarnings("unchecked")
    void acquireAndReleaseShouldUseExactTokenAndTtl() {
        TransferId transferId = TransferId.of(UUID.randomUUID().toString());
        String requestHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        String expectedKey = RedisTransferIdempotencyLockAdapter.LOCK_KEY_PREFIX + transferId.value();

        when(redisTemplate.execute(any(RedisScript.class), eq(List.of(expectedKey)), anyString(), eq("30000")))
                .thenReturn("ACQUIRED");
        when(redisTemplate.execute(any(RedisScript.class), eq(List.of(expectedKey)), anyString()))
                .thenReturn(1L);

        LockResult result = adapter.tryAcquire(transferId, requestHash);

        assertThat(result).isInstanceOf(LockResult.Acquired.class);
        ArgumentCaptor<String> lockValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(redisTemplate).execute(any(RedisScript.class), eq(List.of(expectedKey)), lockValueCaptor.capture(), eq("30000"));

        String storedValue = lockValueCaptor.getValue();
        assertThat(storedValue).endsWith(":" + requestHash);

        // Gọi release() 2 lần liên tiếp -> chỉ gọi Redis DEL script đúng 1 lần với đúng storedValue
        LockResult.Acquired acquired = (LockResult.Acquired) result;
        acquired.handle().release();
        acquired.handle().release();

        verify(redisTemplate, times(1)).execute(any(RedisScript.class), eq(List.of(expectedKey)), eq(storedValue));
    }

    @Test
    @DisplayName("Khi key đang tồn tại trên Redis (IN_FLIGHT), trích xuất chính xác existingRequestHash")
    @SuppressWarnings("unchecked")
    void alreadyInFlightShouldReturnExistingRequestHash() {
        TransferId transferId = TransferId.of(UUID.randomUUID().toString());
        String existingToken = UUID.randomUUID().toString();
        String existingHash = "aaaa1111bbbb2222cccc3333dddd4444eeee5555ffff66667777888899990000";

        when(redisTemplate.execute(any(RedisScript.class), anyList(), anyString(), anyString()))
                .thenReturn(existingToken + ":" + existingHash);

        LockResult result = adapter.tryAcquire(transferId, "different-hash");

        assertThat(result).isInstanceOf(LockResult.AlreadyInFlight.class);
        LockResult.AlreadyInFlight inFlight = (LockResult.AlreadyInFlight) result;
        assertThat(inFlight.existingRequestHash()).isEqualTo(existingHash);
    }

    @Test
    @DisplayName("Khi Redis mất kết nối lúc acquire, trả về LockUnavailable để fallback an toàn xuống DB")
    @SuppressWarnings("unchecked")
    void redisFailureOnAcquireShouldReturnLockUnavailable() {
        TransferId transferId = TransferId.of(UUID.randomUUID().toString());

        when(redisTemplate.execute(any(RedisScript.class), anyList(), anyString(), anyString()))
                .thenThrow(new RedisConnectionFailureException("Redis down"));

        LockResult result = adapter.tryAcquire(transferId, "hash123");

        assertThat(result).isInstanceOf(LockResult.LockUnavailable.class);
    }

    @Test
    @DisplayName("Khi Redis gặp lỗi lúc release, không ném exception làm hỏng kết quả giao dịch đã thành công")
    @SuppressWarnings("unchecked")
    void redisFailureOnReleaseShouldBeSwallowedGracefully() {
        TransferId transferId = TransferId.of(UUID.randomUUID().toString());

        when(redisTemplate.execute(any(RedisScript.class), anyList(), anyString(), anyString()))
                .thenReturn("ACQUIRED");
        when(redisTemplate.execute(any(RedisScript.class), anyList(), anyString()))
                .thenThrow(new RedisConnectionFailureException("Redis connection lost during release"));

        LockResult.Acquired acquired = (LockResult.Acquired) adapter.tryAcquire(transferId, "hash123");
        acquired.handle().release(); // Không được ném ngoại lệ
    }
}
