package com.inventory.app.server.entity.collection;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Entity(name = "collection_details")
@Table(name = "collection_details")
@Data
public class CollectionDetails extends BaseCollection {
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    @Column(name = "modified_by")
    private String modifiedBy;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Collection collection;

    @Override
    public String toString() {
        return "CollectionDetails{" +
                "createdBy='" + createdBy + '\'' +
                ", createdOn=" + createdOn +
                ", modifiedOn=" + modifiedOn +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
