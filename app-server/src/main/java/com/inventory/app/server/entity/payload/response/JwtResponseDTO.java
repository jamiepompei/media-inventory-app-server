package com.inventory.app.server.entity.payload.response;

import lombok.*;

@Getter
@Setter
@Builder
public class JwtResponseDTO {
    private String accessToken;
    private String refreshToken;
}
