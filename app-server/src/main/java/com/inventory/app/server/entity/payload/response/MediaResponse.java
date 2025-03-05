package com.inventory.app.server.entity.payload.response;

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
    @NotBlank(message = "CreatedAsOf is mandatory.")
    private LocalDateTime createdAsOf;
    @NotBlank(message = "ModifiedBy is mandatory.")
    private String modifiedBy;
    @NotBlank(message = "ModifiedAsOf is mandatory.")
    private LocalDateTime modifiedAsOf;
    @NotBlank(message = "Completed is mandatory.")
    private boolean completed;
    @NotBlank(message = "OnLoan is mandatory.")
    private boolean onLoan;
    @NotBlank(message = "Tags are mandatory.")
    private Set<String> tags;
    private Integer reviewRating;
    private String reviewDescription;
    /**
     * This map contains additional media attributes that correspond to a specific media bean.
     * The enum class MediaInventoryAdditionalAttributes defines the keys that could be
     * available in this map.
     */
    private ConcurrentHashMap<String, Object> additionalAttributes;
}
