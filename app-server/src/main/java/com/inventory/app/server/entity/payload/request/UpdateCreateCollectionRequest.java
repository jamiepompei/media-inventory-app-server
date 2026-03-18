package com.inventory.app.server.entity.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCreateCollectionRequest {
    @NotBlank(message = "Username is mandatory.")
    private String username;
    @NotBlank(message = "Collection Title is mandatory.")
    private String collectionTitle;
    private String description;
}
