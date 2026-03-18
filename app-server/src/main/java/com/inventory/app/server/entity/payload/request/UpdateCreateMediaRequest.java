package com.inventory.app.server.entity.payload.request;

import com.inventory.app.server.entity.collection.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Builder
@Data
public class UpdateCreateMediaRequest implements UsernameAware {

    private Long id;
    private Integer version;
    @NotBlank(message = "Title is mandatory.")
    private String title;
    @NotBlank(message = "Format is mandatory.")
    private String format;
    @NotBlank(message = "Genre is mandatory.")
    private String genre;
    private String username;
    private boolean completed;
    private boolean onLoan;
    private Set<Tag> tags;
    private Integer reviewRating;
    private String reviewDescription;
    @NotBlank(message = "Entity type is mandatory")
    private String entityType;
    /**
     * This map contains additional media attributes that correspond to a specific media bean.
     * The enum class MediaInventoryAdditionalAttributes defines the keys that could be
     * available in this map.
     */
    @NotEmpty(message = "Additional attributes are mandatory.")
    private ConcurrentHashMap<String, Object> additionalAttributes;
}


