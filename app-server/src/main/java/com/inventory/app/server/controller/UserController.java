package com.inventory.app.server.controller;

import com.inventory.app.server.entity.payload.request.UserRequest;
import com.inventory.app.server.entity.payload.response.UserResponse;
import com.inventory.app.server.entity.user.UserInfo;

import com.inventory.app.server.error.ErrorResponse;
import com.inventory.app.server.service.user.UserDAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserDAOService userService;

    //save user
    @PostMapping("/save")
    public ResponseEntity<UserResponse> saveUser(@RequestBody UserRequest userRequest) {
        try {
            UserInfo userInfo = mapUserInto(userRequest);
            UserInfo userInfoResponse = userService.saveUser(userInfo);
            UserResponse userResponse = mapUserResponse(userInfoResponse);

            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    //get all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            List<UserInfo> userList = userService.getAllUsers();
            List<UserResponse> userResponseList = userList
                    .stream()
                    .map(this::mapUserResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userResponseList);
        } catch (Exception e) {
            return (ResponseEntity<List<UserResponse>>) Arrays.asList(createErrorResponse(e));
        }
    }

    private UserResponse mapUserResponse(UserInfo userInfo) {
        return UserResponse.builder()
                .username(userInfo.getUsername())
                .roles(userInfo.getRoles())
                .build();
    }

    private UserInfo mapUserInto(UserRequest userRequest) {
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(userRequest.getPassword());
        userInfo.setUsername(userRequest.getUsername());
        userInfo.setRoles(userRequest.getRoles());
        return userInfo;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile() {
        UserResponse userResponse;
        try {
            userResponse = userService.getUser();
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    private ResponseEntity<UserResponse> createErrorResponse(Exception e) {
        String message = e.getMessage();
        ErrorResponse errorResponse = new com.inventory.app.server.error.ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserResponse.builder().errorResponse(errorResponse).build());
    }

    //test
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        try {
            return ResponseEntity.ok("Welcome!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((e.getMessage()));
        }
    }
}
