package com.inventory.app.server.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "PermissionLevel")
@Table(name = "permission_level")
public class UserPermissions extends BaseUser {
    //TODO figure out how to implement this?
    @Column(name = "collection_id")
    private Long collectionId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "permission_level")
    private Enum permissionLevel;
}
