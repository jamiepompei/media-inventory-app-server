package com.inventory.app.server.entity.collection;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.Set;
@Entity(name = "Collection")
@Table(name = "collection")
@Getter
@Setter
public class Collection extends BaseCollection {
    @ManyToMany
            @JoinTable(
                    name = "collection_tag",
                    joinColumns = @JoinColumn(name = "collection_id"),
                    inverseJoinColumns = @JoinColumn(name = "tag_id")
            )
    private Set<Tag> tags;
    @OneToOne(
            mappedBy = "collection",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch =  FetchType.LAZY
    )
    private CollectionDetails collectionDetails;
    //todo how to implement this? saving the collection name from collection details on the media table. join on names?
    //private List<Media> media;

}
