package com.hieu.moneybank.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hieu.common.annotation.ValidCommand;
import com.hieu.common.cqrs.Command;
import com.hieu.moneybank.dto.response.TransferProposalResponseDTO;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Command tạo Transfer Proposal (U-06 — bước 1/2).
 *
 * <p>Command này chỉ tạo proposal ở trạng thái PENDING.
 * Corebank chưa được gọi ở bước này.
 *
 * <p>Tạo proposal không idempotent: mỗi lần gọi tạo một proposal mới. Tính
 * idempotent chỉ áp dụng khi chuyển tiền thật (bước confirm → Corebank).
 */
@ValidCommand
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransferProposalCommand implements Command<TransferProposalResponseDTO> {

    /**
     * CIF của người khởi tạo.
     * Luôn được controller set từ identity context (JWT); giá trị gửi trong request
     * body bị bỏ qua ({@code READ_ONLY} khi deserialize).
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String initiatorCustomerId;

    @NotBlank(message = "sourceAccountId không được để trống")
    private String sourceAccountId;

    @NotBlank(message = "destinationAccountId không được để trống")
    private String destinationAccountId;

    @NotNull(message = "amount không được null")
    @DecimalMin(value = "0.01", message = "amount phải ≥ 0.01")
    @Digits(integer = 17, fraction = 2, message = "amount tối đa 17 chữ số nguyên, 2 chữ số thập phân")
    private BigDecimal amount;

    @NotBlank(message = "currency không được để trống")
    @Pattern(regexp = "[A-Za-z]{3}", message = "currency phải là mã ISO-4217 3 ký tự")
    private String currency;

    @Size(max = 255, message = "description tối đa 255 ký tự")
    private String description;
}
