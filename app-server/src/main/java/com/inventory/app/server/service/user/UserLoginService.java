package com.inventory.app.server.service.user;

import com.inventory.app.server.entity.media.TelevisionShow;
import com.inventory.app.server.entity.user.User;
import com.inventory.app.server.entity.user.UserLogin;
import com.inventory.app.server.repository.IGenericExtendedDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserLoginService {
    private IGenericExtendedDao<UserLogin, Long> dao;

    @Autowired
    public void setDao(IGenericExtendedDao<UserLogin, Long> daoToSet) {
        dao = daoToSet;
        dao.setClazz(UserLogin.class);
    }

    Optional<UserLogin> findByUsername(String username) {
        return dao.findByAttributeContainsText("username", username).stream().findFirst();
    }
}
