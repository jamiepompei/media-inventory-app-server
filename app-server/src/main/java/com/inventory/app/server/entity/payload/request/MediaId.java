package com.inventory.app.server.entity.payload.request;

import lombok.Builder;
import lombok.Data;
    @Builder
    @Data
    public class MediaId {
        private Long id;
        //TODO is this really needed?
        private Integer version;
        private String title;
        private String format;
        private String genre;
        private String collectionName;
}
