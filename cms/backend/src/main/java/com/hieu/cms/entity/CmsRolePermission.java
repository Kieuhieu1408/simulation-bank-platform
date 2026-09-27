package com.hieu.cms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "cms_role_permission")
@Getter
@Setter
@IdClass(CmsRolePermissionId.class)
public class CmsRolePermission {
    @Id
    private UUID roleId;
    @Id
    private UUID permissionId;
    private boolean isGranted;
}
