package com.inventory.app.server.repository.user;

import com.inventory.app.server.entity.user.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {
}
