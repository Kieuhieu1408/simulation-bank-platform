package com.hieu.moneybank.dto.request;

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
 * <p>idempotencyKey phải tuân thủ khuôn dạng [A-Za-z0-9_-]{16,64}
 * theo IdempotencyService.KEY_PATTERN.
 */
@ValidCommand
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransferProposalCommand implements Command<TransferProposalResponseDTO> {

    /**
     * CIF của người khởi tạo.
     * Được inject từ identity context tại handler — không đặt trong request body.
     * Field này blank khi đến từ client; handler sẽ set trước khi dispatch.
     */
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

    /**
     * Key do client tạo — phải giữ nguyên khi retry.
     * Khuôn dạng: [A-Za-z0-9_-]{16,64}.
     */
    @NotBlank(message = "idempotencyKey không được để trống")
    @Pattern(
        regexp = "[A-Za-z0-9_-]{16,64}",
        message = "idempotencyKey phải có 16-64 ký tự [A-Za-z0-9_-]"
    )
    private String idempotencyKey;

    @Size(max = 255, message = "description tối đa 255 ký tự")
    private String description;
}
