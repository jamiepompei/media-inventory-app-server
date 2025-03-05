package com.inventory.app.server.security;

import com.inventory.app.server.entity.user.CustomUserDetails;
import com.inventory.app.server.entity.user.UserInfo;
import com.inventory.app.server.service.user.UserDAOService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for loading user details and creating a UserDetails object for authentication purposes.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserDAOService userDaoService;

    /**
     * This method is an implementation of the loadUserByUsername method defined in UserDetailsService interface. This
     * method is called by Spring Security when it needs to retrieve user details for authentication.
     *
     * This method is responsible for looking up the user in the user repository based on the provided username.
     * If the username is not found in the db, the method throws a UsernameNotFoundExpcetion.
     * If the user is found, the method creates a CustomUserDetails object. This wraps the user information, ie username,
     * password, roles, and authorities.
     *
     * The CustomUserDetails object is used by Spring Security for authentication and authorization checks.
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userDaoService.findByUsername(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("User does not exist. Email: " + username);
        }
        return new CustomUserDetails(userInfo);
    }
}
