package com.hieu.cms.repository;

import com.hieu.cms.entity.ProposalChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProposalChangeLogRepository extends JpaRepository<ProposalChangeLog, UUID> {
}
