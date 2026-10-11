package com.hieu.corebank.transfer.domain;

import com.hieu.corebank.shared.domain.TransferId;

import java.util.Optional;

/**
 * Domain Port cho lưu trữ và truy xuất Transfer Aggregate Root.
 */
public interface TransferRepository {

    void save(Transfer transfer);

    Optional<Transfer> findById(TransferId id);
}
