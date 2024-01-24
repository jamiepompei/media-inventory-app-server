package com.inventory.app.server.entity.payload.response;

import com.inventory.app.server.entity.payload.request.MediaId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaResponse {

    private MediaId mediaId;
    /**
     * This map contains additional media attributes that correspond to a specific media bean.
     * The enum class MediaInventoryAdditionalAttributes defines the keys that could be
     * available in this map.
     */
    private ConcurrentHashMap<String, Object> additionalAttributes;
}
