package com.inventory.app.server.service.user;

import com.inventory.app.server.entity.user.UserInfo;
import com.inventory.app.server.repository.IBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDAOService {
    private IBaseDao<UserInfo> dao;

    @Autowired
    public void setDao(IBaseDao<UserInfo> daoToSet) {
        dao = daoToSet;
        dao.setClazz(UserInfo.class);
    }

    public UserInfo findByUsername(String username) {
        return dao.findOneByField("username", username);
    }
}
