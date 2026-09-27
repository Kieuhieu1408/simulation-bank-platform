package com.hieu.cms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "proposal_change_log")
@Getter
@Setter
public class ProposalChangeLog {
    @Id
    private UUID id;
    private UUID proposalId;
    private String proposalCode;
    private String customerCif;
    private String proposalType;
    private String oldData;
    private String newData;
    private String documentUrls;
    private String finalStatus;
    private String makerId;
    private String checkerId;
    private String reason;
    private LocalDateTime completedAt;
}
