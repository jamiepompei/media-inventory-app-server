package com.inventory.app.server.entity.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMediaRequest {

    private String collectionTitle;
    private String genre;
    private String format;
    private String title;
    @NotBlank(message = "Username is mandatory.")
    private String username;
    /**
     * This map contains additional media attributes that correspond to a specific media bean.
     * The enum class MediaInventoryAdditionalAttributes defines the keys that could be
     * available in this map.
     */
    @NotNull(message= "Additional Attributes map cannot be null.")
    private ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
}
