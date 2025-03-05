package com.inventory.app.server.repository;

import com.inventory.app.server.entity.user.RefreshToken;
import com.inventory.app.server.entity.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenDao extends JpaRepository<RefreshToken, Long> {


   Optional<RefreshToken> findByToken(String token);

   void deleteByUserInfo(UserInfo userInfo);
}
