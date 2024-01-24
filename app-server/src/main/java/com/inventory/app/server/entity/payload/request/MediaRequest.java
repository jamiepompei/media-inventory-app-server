package com.inventory.app.server.entity.payload.request;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Builder
@Data
public class MediaRequest {

    private MediaId mediaId;
    /**
     * This map contains additional media attributes that correspond to a specific media bean.
     * The enum class MediaInventoryAdditionalAttributes defines the keys that could be
     * available in this map.
     */
    private ConcurrentHashMap<String, Object> additionalAttributes;


    @Builder
    @Data
    public class MediaId {
        private Long id;
        private String title;
        private String format;
        private String genre;
        private String collectionName;
    }
}


