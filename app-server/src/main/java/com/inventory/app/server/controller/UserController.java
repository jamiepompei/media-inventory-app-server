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


    /**
     * Retrieves all users from the system.
     *
     * @return A ResponseEntity containing a list of all users.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        log.info("Received request to fetch all users.");
        try {
            List<UserInfo> userList = userService.getAllUsers();
            List<UserResponse> userResponseList = userList
                    .stream()
                    .map(UserMapper.INSTANCE::mapUserInfoToUserResponse)
                    .collect(Collectors.toList());
            log.info("Successfully retrieved {} users.", userResponseList.size());
            return ResponseEntity.ok(userResponseList);
        } catch (Exception e) {
            log.error("Error occurred while fetching all users: {}", e.getMessage(), e);
            return createErrorResponse(e);
        }
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return A ResponseEntity containing the user's profile.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        log.info("Received request to fetch user profile.");
        try {
            UserResponse userResponse = userService.getUser();
            log.info("Successfully retrieved user profile for user: {}", userResponse.getUsername());
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("Error occurred while fetching user profile: {}", e.getMessage(), e);
            return createErrorResponse(e);
        }
    }

    /**
     * Test endpoint to verify admin access.
     *
     * @return A ResponseEntity containing a test message.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/test/admin")
    public ResponseEntity<String> test() {
        log.info("Testing admin access...");
        log.info("Admin access verified successfully.");
        return ResponseEntity.ok("Welcome!");
    }

    /**
     * Test endpoint to verify role access.
     *
     * @return A ResponseEntity containing a test message.
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/test/role")
    public ResponseEntity<String> testUser() {
        log.info("Testing role access...");
        log.info("Role access verified successfully.");
        return ResponseEntity.ok("Welcome!");
    }

    /**
     * Test endpoint to verify role access.
     *
     * @return A ResponseEntity containing a test message.
     */
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    @GetMapping("/test/view")
    public ResponseEntity<String> testView() {
        log.info("Testing view access...");
        log.info("View access verified successfully.");
        return ResponseEntity.ok("Welcome!");
    }

    /**
     * Creates a standardized error response for exceptions.
     *
     * @param e The exception to handle.
     * @return A ResponseEntity containing the error response.
     */
    private ResponseEntity<UserResponse> createErrorResponse(Exception e) {
        String message = e.getMessage();
        log.error("Creating error response: {}", message, e);
        ErrorResponse errorResponse = new ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserResponse.builder().errorResponse(errorResponse).build());
    }
}
