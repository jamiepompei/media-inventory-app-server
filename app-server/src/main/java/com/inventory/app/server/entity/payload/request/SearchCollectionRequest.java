package com.inventory.app.server.entity.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchCollectionRequest {
    @NotBlank(message = "Username is mandatory.")
    private String username;
    private String collectionTitle;
}
