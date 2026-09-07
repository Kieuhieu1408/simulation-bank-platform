package com.hieu.corebank.api.dto;

import com.hieu.corebank.domain.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;

public final class CustomerDtos {
    private CustomerDtos() {}

    public record CreateRequest(
            @NotBlank @Pattern(regexp = "[A-Za-z0-9-]{6,20}", message = "cifNumber must contain 6-20 letters, digits, or hyphens") String cifNumber) {}

    public record Response(String cifNumber, Instant createdAt) {
        public static Response from(Customer customer) {
            return new Response(customer.getCifNumber(), customer.getCreatedAt());
        }
    }
}
