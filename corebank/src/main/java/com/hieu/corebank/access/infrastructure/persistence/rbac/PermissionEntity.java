package com.hieu.corebank.access.infrastructure.persistence.rbac;

import com.hieu.common.constant.ActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"menu_code", "action"})
)
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_code", nullable = false, length = 50)
    private String menuCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private ActionType action;

    @Column(name = "description", length = 255)
    private String description;

    protected PermissionEntity() {}

    public PermissionEntity(String menuCode, ActionType action, String description) {
        this.menuCode = menuCode;
        this.action = action;
        this.description = description;
    }
}
