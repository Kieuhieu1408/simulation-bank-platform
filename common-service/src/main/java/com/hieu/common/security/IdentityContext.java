package com.hieu.common.security;

import java.util.Set;

/**
 * Định danh đã được xác minh của request hiện tại.
 *
 * <p>Đây là nguồn duy nhất cho {@code customerId} trong toàn bộ use case. Client
 * không được truyền {@code customerId} để quyết định ownership (SRD mục 13.1,
 * {@code AD-SEC-A05}); nếu nhận từ request body thì bất kỳ ai có token hợp lệ
 * cũng đọc/chuyển tiền được tài khoản của người khác.
 *
 * @param subject   claim {@code sub}, định danh người dùng tại IAM
 * @param customerId định danh khách hàng nghiệp vụ, lấy từ claim đã cấu hình
 * @param scopes    scope đã được cấp, không kèm prefix {@code SCOPE_}
 * @param sessionId claim {@code sid}, phục vụ thu hồi session (AD-SEC-D03)
 */
public record IdentityContext(
        String subject,
        String customerId,
        Set<String> scopes,
        String sessionId) {

    public IdentityContext {
        scopes = scopes == null ? Set.of() : Set.copyOf(scopes);
    }

    public boolean hasScope(String scope) {
        return scopes.contains(scope);
    }
}
