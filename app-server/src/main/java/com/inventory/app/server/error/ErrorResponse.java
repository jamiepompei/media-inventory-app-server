package com.inventory.app.server.error;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorResponse {
    private String errorMessage;
    private Integer status;
}
