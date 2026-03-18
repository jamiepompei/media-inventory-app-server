package com.inventory.app.server.entity.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteMediaRequest implements UsernameAware {
    private Long id;
    private String username;
    @NotBlank(message = "Entity type is mandatory")
    private String entityType;
}
