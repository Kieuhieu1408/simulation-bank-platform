package com.hieu.common.observability;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Đặt {@code correlationId} vào MDC cho toàn bộ request (SRD mục 4, {@code OPS-001}).
 *
 * <p>Correlation id đến từ ngoài chỉ được nhận nếu khớp {@link #SAFE_PATTERN}.
 * Giá trị do client tự đặt vẫn đi vào log nên phải coi là untrusted input: nếu
 * không validate, một client có thể chèn ký tự điều khiển để bẻ dòng log
 * (log injection) hoặc bơm chuỗi rất dài làm phình index.
 *
 * <p>Filter chạy sớm nhất để log của filter chain phía sau, gồm cả lỗi
 * authentication, đều có correlation id.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    /** Chỉ cho phép ký tự an toàn cho log/index và giới hạn độ dài. */
    private static final Pattern SAFE_PATTERN = Pattern.compile("[A-Za-z0-9_-]{8,64}");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = resolve(request.getHeader(LogFields.CORRELATION_ID_HEADER));
        MDC.put(LogFields.CORRELATION_ID, correlationId);
        response.setHeader(LogFields.CORRELATION_ID_HEADER, correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Bắt buộc dọn MDC: thread được pool tái sử dụng, bỏ sót sẽ khiến
            // request sau mang correlation id của request trước.
            MDC.remove(LogFields.CORRELATION_ID);
        }
    }

    private String resolve(String inboundValue) {
        if (inboundValue != null && SAFE_PATTERN.matcher(inboundValue).matches()) {
            return inboundValue;
        }
        return UUID.randomUUID().toString();
    }

    /** Correlation id của request hiện tại, hoặc {@code null} nếu ngoài request scope. */
    public static String current() {
        return MDC.get(LogFields.CORRELATION_ID);
    }
}
