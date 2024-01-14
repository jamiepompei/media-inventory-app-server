package com.inventory.app.server.service.user;

import com.inventory.app.server.entity.user.UserPermissions;
import com.inventory.app.server.repository.IGenericExtendedDao;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPermissionService {
    private IGenericExtendedDao<UserPermissions, Long> dao;

    public void setDao(IGenericExtendedDao<UserPermissions, Long> daoToSet) {
        dao = daoToSet;
        dao.setClazz(UserPermissions.class);
    }

    Optional<UserPermissions> findByUsername(String username) {
        return dao.findByAttributeContainsText("username", username).stream().findFirst();
    }
}
