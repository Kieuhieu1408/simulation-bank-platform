package com.hieu.cms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "proposal")
@Getter
@Setter
public class Proposal {
    @Id
    private UUID id;
    private String proposalCode;
    private String customerCif;
    private String proposalType;
    private String oldData;
    private String newData;
    private String documentUrls;
    private String status;
    private String makerId;
    private String checkerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
