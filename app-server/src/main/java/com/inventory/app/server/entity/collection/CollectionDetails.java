package com.inventory.app.server.entity.collection;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
@Entity(name = "Collection_Details")
@Table(name = "collection_details")
@Getter
@Setter
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
