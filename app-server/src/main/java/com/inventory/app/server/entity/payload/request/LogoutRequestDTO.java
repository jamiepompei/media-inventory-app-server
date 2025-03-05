package com.inventory.app.server.entity.payload.request;

import lombok.*;

@Getter
@Setter
public class LogoutRequestDTO {
    private String refreshToken;
}
