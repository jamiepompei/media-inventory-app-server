package com.inventory.app.server.controller;

import com.inventory.app.server.entity.payload.request.AuthRequestDTO;
import com.inventory.app.server.entity.payload.request.LogoutRequestDTO;
import com.inventory.app.server.entity.payload.request.RefreshTokenRequestDTO;
import com.inventory.app.server.entity.payload.request.UserRequest;
import com.inventory.app.server.entity.payload.response.JwtResponseDTO;
import com.inventory.app.server.entity.payload.response.UserResponse;
import com.inventory.app.server.entity.user.RefreshToken;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserDAOService userService;
    @Autowired
    private AuthenticationManager authenticationManager;


    /**
     * Handles user signup requests. Maps the incoming user request to a user entity,
     * saves the user in the database, and returns the created user details.
     *
     * @param userRequest The user request containing signup details.
     * @return A ResponseEntity containing the created user details.
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody UserRequest userRequest) {
        log.info("Received signup request for user: {}", userRequest.getUsername());
        try {
            UserInfo userInfo = UserMapper.INSTANCE.mapUserRequestToUserInfo(userRequest);
            UserInfo userInfoResponse = userService.saveUser(userInfo);
            UserResponse userResponse = UserMapper.INSTANCE.mapUserInfoToUserResponse(userInfoResponse);
            log.info("User signed up successfully: {}", userResponse.getUsername());
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("Error during signup for user: {}. Error: {}", userRequest.getUsername(), e.getMessage(), e);
            return createErrorResponse(e);
        }
    }

    /**
     * Authenticates a user and generates access and refresh tokens if the credentials are valid.
     *
     * @param authRequestDTO The authentication request containing username and password.
     * @return A ResponseEntity containing the generated access and refresh tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        log.info("Received login request for user: {}", authRequestDTO.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
            if (authentication.isAuthenticated()) {
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
                log.info("User authenticated successfully: {}", authRequestDTO.getUsername());
                return ResponseEntity.ok(JwtResponseDTO.builder()
                        .accessToken(jwtService.generateToken(authRequestDTO.getUsername()))
                        .refreshToken(refreshToken.getToken())
                        .build());
            } else {
                log.warn("Authentication failed for user: {}", authRequestDTO.getUsername());
                throw new UsernameNotFoundException("Invalid user credentials.");
            }
        } catch (Exception e) {
            log.error("Error during login for user: {}: {}", authRequestDTO.getUsername(), e.getMessage(), e);
            return createErrorResponse(e);
        }
    }


    /**
     * Handles refresh token requests. Verifies the validity of the provided refresh token,
     * generates a new access token, and rotates the refresh token.
     *
     * @param refreshTokenRequestDTO The request containing the refresh token.
     * @return A ResponseEntity containing the new access and refresh tokens.
     */
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        log.info("Received refresh token request.");
        try {
            JwtResponseDTO jwtResponseDTO = refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                    .map(refreshTokenService::verifyExpiration)
                    .map(refreshToken -> {
                        String newAccessToken = jwtService.generateToken(refreshToken.getUserInfo().getUsername());
                        RefreshToken newRefreshToken = refreshTokenService.generateNewRefreshToken(refreshToken.getUserInfo());
                        log.info("Refresh token processed successfully for user: {}", refreshToken.getUserInfo().getUsername());
                        return JwtResponseDTO.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(newRefreshToken.getToken())
                                .build();
                    })
                    .orElseThrow(() -> {
                        log.warn("Invalid refresh token provided.");
                        return new RuntimeException("Invalid refresh token!");
                    });
            return ResponseEntity.ok(jwtResponseDTO);
        } catch (Exception e) {
            log.error("Error during refresh token processing: {}", e.getMessage(), e);
            return createErrorResponse(e);
        }
    }

    /**
     * Handles user logout requests. Deletes the provided refresh token from the database
     * to invalidate the session.
     *
     * @param logoutRequestDTO The request containing the refresh token to be deleted.
     * @return A ResponseEntity confirming the logout operation.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        log.info("Received logout request.");
        try {
            refreshTokenService.deleteRefreshToken(logoutRequestDTO.getRefreshToken());
            log.info("User logged out successfully.");
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            return createErrorResponse(e);
        }
    }

    /**
     * Creates a standardized error response for exceptions. Wraps the exception message
     * in an ErrorResponse object and returns it with an HTTP 500 status.
     *
     * @param e The exception to handle.
     * @return A ResponseEntity containing the error response.
     */
    private ResponseEntity<UserResponse> createErrorResponse(Exception e) {
        String message = e.getMessage();
        ErrorResponse errorResponse = new com.inventory.app.server.error.ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserResponse.builder().errorResponse(errorResponse).build());
    }

    /**
     * Handles unhandled exceptions globally. Logs the exception and returns a standardized
     * error response with an HTTP 500 status.
     *
     * @param e The unhandled exception.
     * @return A ResponseEntity containing the error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
