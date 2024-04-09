package com.inventory.app.server.service;

import com.inventory.app.server.entity.user.RefreshToken;
import com.inventory.app.server.repository.IBaseDao;
import com.inventory.app.server.service.user.UserDAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Service
public class RefreshTokenService {
    @Autowired
    IBaseDao<RefreshToken> refreshTokenDao;

    @Autowired
    UserDAOService userDao;

    public RefreshTokenService(IBaseDao<RefreshToken> refreshTokenDao, UserDAOService userDao) {
        this.refreshTokenDao = refreshTokenDao;
        this.userDao = userDao;
    }

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<RefreshToken> daoToSet) {
        refreshTokenDao = daoToSet;
        refreshTokenDao.setClazz(RefreshToken.class);
    }

    public RefreshToken createRefreshToken(String username){
        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userDao.findByUsername(username))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();
        return refreshTokenDao.createOrUpdate(refreshToken);
    }

    public List<RefreshToken> findByToken(String token){
        return refreshTokenDao.findByField("token", token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenDao.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;

    }
}
