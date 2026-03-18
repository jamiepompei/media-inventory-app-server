package com.inventory.app.server.controller;

import com.inventory.app.server.entity.payload.response.UserResponse;
import com.inventory.app.server.entity.user.UserInfo;
import com.inventory.app.server.error.ErrorResponse;
import com.inventory.app.server.mapper.UserMapper;
import com.inventory.app.server.service.RefreshTokenService;
import com.inventory.app.server.service.authorization.JwtService;
import com.inventory.app.server.service.user.UserDAOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserDAOService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;



    //TODO is this endpoint required?
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            List<UserInfo> userList = userService.getAllUsers();
            List<UserResponse> userResponseList = userList
                    .stream()
                    .map(UserMapper.INSTANCE::mapUserInfoToUserResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userResponseList);
        } catch (Exception e) {
            return (ResponseEntity<List<UserResponse>>) Arrays.asList(createErrorResponse(e));
        }
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

    //test
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        try {
            log.info("Testing admin access...");
            return ResponseEntity.ok("Welcome!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((e.getMessage()));
        }
    }

    private ResponseEntity<UserResponse> createErrorResponse(Exception e) {
        String message = e.getMessage();
        ErrorResponse errorResponse = new com.inventory.app.server.error.ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserResponse.builder().errorResponse(errorResponse).build());
    }
}
