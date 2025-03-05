package com.inventory.app.server.service;

import com.inventory.app.server.entity.user.RefreshToken;
import com.inventory.app.server.entity.user.UserInfo;
import com.inventory.app.server.repository.RefreshTokenDao;
import com.inventory.app.server.service.user.UserDAOService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
@Service
public class RefreshTokenService {
    @Autowired
    RefreshTokenDao refreshTokenDao;
    @Autowired
    UserDAOService userDAOService;

    public RefreshTokenService(RefreshTokenDao refreshTokenDao, UserDAOService userDAOService) {
        this.refreshTokenDao = refreshTokenDao;
        this.userDAOService = userDAOService;
    }

    public RefreshToken createRefreshToken(String username){
        UserInfo userInfo = userDAOService.findByUsername(username);

        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userInfo)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();
        return refreshTokenDao.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenDao.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if (token.isExpired()){
            refreshTokenDao.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please login again.");
        }
        return token;
    }

    @Transactional
    public RefreshToken generateNewRefreshToken(UserInfo userInfo) {
        // invalidate old token
        refreshTokenDao.deleteByUserInfo(userInfo);
        // generate new token
        return createRefreshToken(userInfo.getUsername());
    }

    @Transactional
    public void deleteRefreshToken(String token){
        refreshTokenDao.findByToken(token)
                .ifPresent(refreshTokenDao::delete);
    }
}
