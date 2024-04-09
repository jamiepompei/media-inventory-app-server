package com.inventory.app.server.service.user;

import com.inventory.app.server.entity.payload.response.UserResponse;
import com.inventory.app.server.entity.user.UserInfo;
import com.inventory.app.server.repository.IBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;

@Service
public class UserDAOService {
    private IBaseDao<UserInfo> dao;

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public void setDao(IBaseDao<UserInfo> daoToSet) {
        dao = daoToSet;
        dao.setClazz(UserInfo.class);
    }

    //TODO fix this
    public UserInfo findByUsername(String username) {
        return dao.findOneByField("username", username, username);
    }

    public UserInfo saveUser(UserInfo userInfo) { return dao.createOrUpdate(userInfo);}

    public UserResponse getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        String usernameFromAccessToken = userDetail.getUsername();
        UserInfo user = findByUsername(usernameFromAccessToken);
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        return userResponse;
    }
    public List<UserInfo> getAllUsers() { return dao.findAll();}
}
