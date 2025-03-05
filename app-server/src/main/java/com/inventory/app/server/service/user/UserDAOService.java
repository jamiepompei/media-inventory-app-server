package com.inventory.app.server.service.user;

import com.inventory.app.server.entity.payload.response.UserResponse;
import com.inventory.app.server.entity.user.UserInfo;
import com.inventory.app.server.mapper.UserMapper;
import com.inventory.app.server.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDAOService {
    @Autowired
    private UserDao userDao;

    public UserDAOService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserInfo findByUsername(String username) {
        return userDao.findByUsername(username).orElseThrow(() -> new RuntimeException("Username not found " + username));
    }

    public UserInfo saveUser(UserInfo userInfo) { return userDao.save(userInfo);}

    public UserResponse getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        String usernameFromAccessToken = userDetail.getUsername();
        UserInfo user = findByUsername(usernameFromAccessToken);
        return UserMapper.INSTANCE.mapUserInfoToUserResponse(user);
    }
    public List<UserInfo> getAllUsers() { return userDao.findAll();}
}
