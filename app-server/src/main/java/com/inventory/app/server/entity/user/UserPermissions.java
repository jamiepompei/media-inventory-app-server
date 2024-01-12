package com.inventory.app.server.entity.user;

import javax.persistence.*;

@Entity(name = "PermissionLevel")
@Table(name = "permission_level")
public class UserPermissions extends BaseUser {
    //TODO figure out how to implement this?
    @ManyToOne
    @JoinTable(
            name = "collection_details",
            joinColumns = @JoinColumn(name = "created_by"),
            inverseJoinColumns = @JoinColumn(name = "user_name")
    )
    private Long collectionId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "permission_level")
    private Enum permissionLevel;
}
