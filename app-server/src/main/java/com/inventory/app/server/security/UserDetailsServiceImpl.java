package com.inventory.app.server.security;

import com.inventory.app.server.entity.user.CustomUserDetails;
import com.inventory.app.server.entity.user.UserInfo;
import com.inventory.app.server.service.user.UserDAOService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserDAOService userDaoService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo userInfo = userDaoService.findByUsername(email);
        if (userInfo == null) {
            throw new UsernameNotFoundException("User does not exist. Email: "+ email);
        }
        return new CustomUserDetails(userInfo);
    }
}
