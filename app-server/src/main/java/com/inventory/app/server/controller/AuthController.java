package com.inventory.app.server.controller;

import com.inventory.app.server.entity.payload.request.LogoutRequestDTO;
import com.inventory.app.server.entity.payload.request.RefreshTokenRequestDTO;
import com.inventory.app.server.entity.payload.response.JwtResponseDTO;
import com.inventory.app.server.entity.user.RefreshToken;
import com.inventory.app.server.service.RefreshTokenService;
import com.inventory.app.server.service.authorization.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenService refreshTokenService;


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
}
