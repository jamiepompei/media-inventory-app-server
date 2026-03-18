package com.inventory.app.server.entity.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
public class SearchMediaRequest implements UsernameAware {

    private String collectionTitle;
    private String genre;
    private String format;
    private String title;
    private String username;
    @NotBlank(message = "Entity type is mandatory")
    private String entityType;
    /**
     * This map contains additional media attributes that correspond to a specific media bean.
     * The enum class MediaInventoryAdditionalAttributes defines the keys that could be
     * available in this map.
     */
    @NotNull(message= "Additional Attributes map cannot be null.")
    private ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();


}
