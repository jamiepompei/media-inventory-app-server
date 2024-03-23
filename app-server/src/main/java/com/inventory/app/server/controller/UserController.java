package com.inventory.app.server.controller;

import com.inventory.app.server.entity.payload.request.AuthRequestDTO;
import com.inventory.app.server.entity.payload.request.UserRequest;
import com.inventory.app.server.entity.payload.response.JwtResponseDTO;
import com.inventory.app.server.entity.payload.response.UserResponse;
import com.inventory.app.server.entity.user.RefreshToken;
import com.inventory.app.server.entity.user.UserInfo;

import com.inventory.app.server.error.ErrorResponse;
import com.inventory.app.server.service.RefreshTokenService;
import com.inventory.app.server.service.authorization.JwtService;
import com.inventory.app.server.service.user.UserDAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
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


    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody UserRequest userRequest) {
        try {
            UserInfo userInfo = mapUserInto(userRequest);
            UserInfo userInfoResponse = userService.saveUser(userInfo);
            UserResponse userResponse = mapUserResponse(userInfoResponse);

            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    /**
     * This method is responsible for creating the access and refresh tokens if a user is authenticated.
     * @param authRequestDTO
     * @return
     */
    @PostMapping("/login")
    public JwtResponseDTO authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        //TODO this logic should be moved to the user service
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if (authentication.isAuthenticated()) {
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            return JwtResponseDTO.builder()
                    .accessToken(jwtService.generateToken(authRequestDTO.getUsername()))
                    .refreshToken(refreshToken.getToken())
                    .build();
        } else {
            //todo add more useful info
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    //TODO is this endpoint required?
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

    //TODO refactor into mapper class
    private UserResponse mapUserResponse(UserInfo userInfo) {
        return UserResponse.builder()
                .username(userInfo.getUsername())
                .roles(userInfo.getRoles())
                .build();
    }

    //TODO refactor into mapper class
    private UserInfo mapUserInto(UserRequest userRequest) {
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(userRequest.getPassword());
        userInfo.setUsername(userRequest.getUsername());
        userInfo.setRoles(userRequest.getRoles());
        return userInfo;
    }
}
