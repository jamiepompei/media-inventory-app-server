package com.inventory.app.server.entity.payload.response;

import com.inventory.app.server.entity.collection.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaResponse {

    @NotBlank(message = "Id is mandatory.")
    private Long id;
    @NotBlank(message = "Version is mandatory.")
    private Integer version;
    @NotBlank(message = "Title is mandatory.")
    private String title;
    @NotBlank(message = "Format is mandatory.")
    private String format;
    @NotBlank(message = "Genre is mandatory.")
    private String genre;
    @NotBlank(message = "Collection Title is mandatory.")
    private String collectionTitle;
    @NotBlank(message = "Username is mandatory.")
    private String username;
    @NotBlank(message = "CreatedOn is mandatory.")
    private LocalDateTime createdOn;
    @NotBlank(message = "ModifiedBy is mandatory.")
    private String modifiedBy;
    @NotBlank(message = "ModifiedOn is mandatory.")
    private LocalDateTime modifiedOn;
    @NotBlank(message = "Completed is mandatory.")
    private boolean completed;
    @NotBlank(message = "OnLoan is mandatory.")
    private boolean onLoan;
    @NotBlank(message = "Tags are mandatory.")
    private Set<Tag> tags;
    private Integer reviewRating;
    private String reviewDescription;
    /**
     * This map contains additional media attributes that correspond to a specific media bean.
     * The enum class MediaInventoryAdditionalAttributes defines the keys that could be
     * available in this map.
     */
    private ConcurrentHashMap<String, Object> additionalAttributes;
}
