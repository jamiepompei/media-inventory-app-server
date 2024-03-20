package com.inventory.app.server.entity.payload.response;

import com.inventory.app.server.entity.user.UserRole;
import com.inventory.app.server.error.Error;
import lombok.*;

import java.util.Set;
@Data
@ToString
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private Set<UserRole> roles;
    private Error errorObject;
}
