package com.hieu.corebank.transfer.application.port;

import java.time.Instant;
import java.util.List;

public interface StuckTransferQueryPort {

    List<String> findAndLeaseStuckTransferIds(Instant stuckThreshold);
}
