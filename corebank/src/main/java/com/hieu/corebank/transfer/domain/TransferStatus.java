package com.hieu.corebank.transfer.domain;

/**
 * Trạng thái trong State Machine của Transfer Aggregate Root.
 */
public enum TransferStatus {
    INITIATED,
    SOURCE_RESERVED,
    DESTINATION_CREDITED,
    COMPLETED,
    FAILED
}
