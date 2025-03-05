package com.inventory.app.server.repository;

import com.inventory.app.server.entity.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<UserInfo, Long> {

   Optional<UserInfo> findByUsername(String username);
}
