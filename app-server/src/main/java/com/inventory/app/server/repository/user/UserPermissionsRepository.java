package com.inventory.app.server.repository.user;

import com.inventory.app.server.entity.user.UserPermissions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionsRepository extends JpaRepository<UserPermissions, Long> {
    UserPermissions findByUsername(String username);

}
