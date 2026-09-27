package com.hieu.cms.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
public class CmsRolePermissionId implements Serializable {
    private UUID roleId;
    private UUID permissionId;
}
