package com.inventory.app.server.entity.collection;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionMediaId implements Serializable {
    private Long collectionId;
    private Long mediaId;
}
