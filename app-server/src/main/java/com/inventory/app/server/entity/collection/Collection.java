package com.inventory.app.server.entity.collection;

import com.inventory.app.server.entity.media.Media;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
@Entity(name = "Collection")
@Table(name = "collection")
public class Collection extends BaseCollection {
    @ManyToMany
            @JoinTable(
                    name = "collection_tag",
                    joinColumns = @JoinColumn(name = "collection_id"),
                    inverseJoinColumns = @JoinColumn(name = "tag_id")
            )
    private Set<Tag> tags;
    @Column(name = "title")
    private String title;
    //derived from loading all of the media objects
    private List<Media> mediaList;
    @OneToOne(
            mappedBy = "collection",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch =  FetchType.LAZY
    )
    private CollectionDetails collectionDetails;



}
