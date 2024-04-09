package com.inventory.app.server.controller;

import com.inventory.app.server.entity.payload.request.RefreshTokenRequestDTO;
import com.inventory.app.server.entity.payload.response.JwtResponseDTO;
import com.inventory.app.server.entity.user.RefreshToken;
import com.inventory.app.server.service.RefreshTokenService;
import com.inventory.app.server.service.authorization.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;


    /**
     * This method is responsible for handle refreshing an access token. It first checks that the refresh token
     * exists in the DB, then verifies the validity of the refresh token by checking for expiration. Then the UserInfo
     * details are used to generate a new access token. Finally, a successful refresh results in a response with the new
     * access token and the original refresh token.
     * @param refreshTokenRequestDTO
     * @return
     */
    @PostMapping("/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .stream()
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getUsername());
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequestDTO.getToken()).build();
                }).findFirst().orElseThrow(() -> new RuntimeException("Refresh Token is not in DB."));
    }
}
