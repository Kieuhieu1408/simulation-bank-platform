package com.hieu.cms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "cms_permission")
@Getter
@Setter
public class CmsPermission {
    @Id
    private UUID id;
    private String menuCode;
    private String actionCode;
    private String description;
}
