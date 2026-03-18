package com.inventory.app.server.entity.collection;

import com.inventory.app.server.entity.media.Media;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "collection_media")
@Getter
@Setter
public class CollectionMedia {

    @EmbeddedId
    private CollectionMediaId id;

    @ManyToOne
    @MapsId("collectionId")
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @ManyToOne
    @MapsId("mediaId")
    @JoinColumn(name = "media_id")
    private Media media;
}