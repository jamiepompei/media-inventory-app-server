package com.inventory.app.server.service.user;

import com.inventory.app.server.entity.user.UserDetails;
import com.inventory.app.server.repository.IGenericExtendedDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsService {
    private IGenericExtendedDao<UserDetails, Long> dao;

    @Autowired
    public void setDao(IGenericExtendedDao<UserDetails, Long> daoToSet) {
        dao = daoToSet;
        dao.setClazz(UserDetails.class);
    }

    Optional<UserDetails> findByEmailAddress(String emailAddress) {
        return dao.findByAttributeContainsText("email_address", emailAddress).stream().findFirst();
    }
}
