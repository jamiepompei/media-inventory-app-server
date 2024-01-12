package com.inventory.app.server.repository.user;

import com.inventory.app.server.entity.user.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
}
