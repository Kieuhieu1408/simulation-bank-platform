package com.hieu.corebank.projection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transfer_view")
public class TransferView {

    @Id
    @Column(name = "transfer_id", length = 50, nullable = false)
    private String transferId;

    @Column(name = "request_hash", length = 64, nullable = false)
    private String requestHash;

    @Column(name = "source_account_id", length = 50)
    private String sourceAccountId;

    @Column(name = "destination_account_id", length = 50)
    private String destinationAccountId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
