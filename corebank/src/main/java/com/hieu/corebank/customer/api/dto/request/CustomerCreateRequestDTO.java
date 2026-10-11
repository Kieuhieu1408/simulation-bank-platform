package com.hieu.corebank.customer.api.dto.request;

import com.hieu.corebank.customer.application.command.CreateCustomerCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerCreateRequestDTO {
    @Pattern(regexp = "[A-Za-z0-9-]{6,20}", message = "cifNumber must contain 6-20 letters, digits, or hyphens")
    private String cifNumber;

    @Pattern(regexp = "^([0-9]{12}|[A-Z][0-9]{7,8})$", message = "nationalId must be exactly 12-digit CCCD or valid passport number")
    private String nationalId;

    private String fullName;

    @Pattern(regexp = "(0\\d{9})", message = "phoneNumber must be valid Vietnamese format (10 digits starting with 0)")
    private String phoneNumber;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "email must be valid format")
    private String email;

    public CustomerCreateRequestDTO(String cifNumber) {
        this.cifNumber = cifNumber;
    }

    public CreateCustomerCommand toCommand() {
        return new CreateCustomerCommand(cifNumber, nationalId, fullName, phoneNumber, email);
    }
}
