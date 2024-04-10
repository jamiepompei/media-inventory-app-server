package com.inventory.app.server.entity.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
public class SearchMediaRequest {

    private String collectionTitle;
    private String genre;
    private String format;
    @NotBlank(message = "Username is mandatory.")
    private String username;
    /**
     * This map contains additional media attributes that correspond to a specific media bean.
     * The enum class MediaInventoryAdditionalAttributes defines the keys that could be
     * available in this map.
     */
    private ConcurrentHashMap<String, Object> additionalAttributes;
}
