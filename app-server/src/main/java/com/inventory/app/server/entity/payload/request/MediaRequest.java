package com.inventory.app.server.entity.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaRequest {

    private Long id;
    private Integer version;
    @NotBlank(message = "Title is mandatory.")
    private String title;
    @NotBlank(message = "Format is mandatory.")
    private String format;
    @NotBlank(message = "Genre is mandatory.")
    private String genre;
    @NotBlank(message = "Collection Title is mandatory.")
    private String collectionName;
    /**
     * This map contains additional media attributes that correspond to a specific media bean.
     * The enum class MediaInventoryAdditionalAttributes defines the keys that could be
     * available in this map.
     */
    @NotEmpty(message = "Additional attributes are mandatory.")
    private ConcurrentHashMap<String, Object> additionalAttributes;
}


