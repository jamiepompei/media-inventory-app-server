package com.inventory.app.server.util;

import com.inventory.app.server.entity.payload.request.UsernameAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class MediaServerUtility {

    private MediaServerUtility(){
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static void setUsername(UserDetails userDetails, UsernameAware request) {
        String username = userDetails.getUsername() != null ? userDetails.getUsername() : "null";
        request.setUsername(username);
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "SYSTEM";
    }
}
