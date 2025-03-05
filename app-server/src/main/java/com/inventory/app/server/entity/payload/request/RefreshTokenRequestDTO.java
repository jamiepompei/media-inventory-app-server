package com.inventory.app.server.entity.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequestDTO {
    private String token;
}
