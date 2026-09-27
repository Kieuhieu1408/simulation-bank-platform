package com.hieu.cms.repository;

import com.hieu.cms.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProposalRepository extends JpaRepository<Proposal, UUID> {
}
