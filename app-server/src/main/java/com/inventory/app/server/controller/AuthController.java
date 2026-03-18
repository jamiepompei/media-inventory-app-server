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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody UserRequest userRequest) {
        log.info("Received signup request for user: {}", userRequest.getUsername());
        try {
            UserInfo userInfo = UserMapper.INSTANCE.mapUserRequestToUserInfo(userRequest);
            UserInfo userInfoResponse = userService.saveUser(userInfo);
            UserResponse userResponse = UserMapper.INSTANCE.mapUserInfoToUserResponse(userInfoResponse);

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


    /**
     * This method is responsible for handle refreshing an access token. It first checks that the refresh token
     * exists in the DB, then verifies the validity of the refresh token by checking for expiration. Then the UserInfo
     * details are used to generate a new access token. Finally, a successful refresh results in a response with the new
     * access token and a new refresh token to rotate the tokens.
     * @param refreshTokenRequestDTO
     * @return
     */
    @PostMapping("/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> {
                    String newAccessToken = jwtService.generateToken(refreshToken.getUserInfo().getUsername());
                    RefreshToken newRefreshToken = refreshTokenService.generateNewRefreshToken(refreshToken.getUserInfo());

                    return JwtResponseDTO.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(newRefreshToken.getToken())
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Invalid refresh token."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        refreshTokenService.deleteRefreshToken(logoutRequestDTO.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }

    private ResponseEntity<UserResponse> createErrorResponse(Exception e) {
        String message = e.getMessage();
        ErrorResponse errorResponse = new com.inventory.app.server.error.ErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserResponse.builder().errorResponse(errorResponse).build());
    }
}
