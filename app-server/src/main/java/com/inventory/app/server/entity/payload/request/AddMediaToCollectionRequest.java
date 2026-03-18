package com.inventory.app.server.entity.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class AddMediaToCollectionRequest {
    @NotBlank(message = "Username is mandatory.")
    private String username;
    @NotBlank(message = "Collection Title is mandatory.")
    private String collectionTitle;
    @NotBlank(message = "Media is mandatory.")
    private List<Long> mediaIds;
}
