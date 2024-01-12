package com.inventory.app.server.entity.collection;

import com.fasterxml.jackson.databind.ser.Serializers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
@Entity(name = "Collection_Details")
@Table(name = "collection_details")
public class CollectionDetails extends BaseCollection {
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    @Column(name = "modified_by")
    private String modifiedBy;
    @Column(name = "description")
    private String description;
}
