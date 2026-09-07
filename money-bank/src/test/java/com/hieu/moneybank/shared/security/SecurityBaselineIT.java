package com.hieu.moneybank.shared.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Kiểm chứng baseline bảo mật: deny-by-default và fail closed
 * ({@code SEC-001}, {@code SEC-002}, {@code AD-SEC-T02}).
 *
 * <p>Test này bảo vệ một sai sót dễ xảy ra và khó thấy: cấu hình security bị sửa
 * thành {@code permitAll} hoặc {@code authenticated} cho {@code anyRequest}, làm
 * mọi endpoint mới mặc định mở.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityBaselineIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Request không có token bị từ chối")
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/transfers/any-id"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Command không có token bị từ chối, không phải 404 hay 405")
    void unauthenticatedCommandIsRejected() throws Exception {
        // Quan trọng: phải là 401 trước khi routing quyết định endpoint có tồn tại,
        // để không tiết lộ bề mặt API cho caller chưa xác thực.
        mockMvc.perform(post("/api/v1/transfers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Endpoint chưa khai báo quyền bị chặn thay vì mở")
    void undeclaredEndpointIsDeniedByDefault() throws Exception {
        mockMvc.perform(get("/some/endpoint/not/declared"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Endpoint quản trị không mở qua đường request thông thường")
    void managementEndpointsAreNotPubliclyReachable() throws Exception {
        mockMvc.perform(get("/actuator/env"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Liveness probe mở để orchestrator không restart oan")
    void livenessProbeIsReachable() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Response luôn có correlation id để đối soát log")
    void responseCarriesCorrelationId() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(header().exists("X-Correlation-Id"));
    }

    @Test
    @DisplayName("Correlation id do client gửi bị loại nếu không đúng khuôn dạng an toàn")
    void unsafeInboundCorrelationIdIsReplaced() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness")
                        .header("X-Correlation-Id", "bad value\nInjected: log-line"))
                .andExpect(header().string("X-Correlation-Id",
                        org.hamcrest.Matchers.matchesPattern("[A-Za-z0-9_-]{8,64}")));
    }

    @Test
    @DisplayName("Correlation id hợp lệ do client gửi được giữ nguyên")
    void safeInboundCorrelationIdIsPreserved() throws Exception {
        String correlationId = "abcdef0123456789";
        mockMvc.perform(get("/actuator/health/liveness")
                        .header("X-Correlation-Id", correlationId))
                .andExpect(header().string("X-Correlation-Id", correlationId));
    }
}
