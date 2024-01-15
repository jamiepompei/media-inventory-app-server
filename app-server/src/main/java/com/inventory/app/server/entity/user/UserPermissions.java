package com.inventory.app.server.entity.user;

import jakarta.persistence.*;

import java.util.List;

@Entity(name = "PermissionLevel")
@Table(name = "permission_level")
public class UserPermissions extends BaseUser {
    @ElementCollection
    private List<Long> collectionId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "permission_level")
    private Enum permissionLevel;
    @OneToOne
    private User user;
}
