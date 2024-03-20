package com.inventory.app.server.error;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Error {
    private String errorMessage;
    private Integer status;
}
