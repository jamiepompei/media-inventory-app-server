package com.inventory.app.server.repository.collection;

import com.inventory.app.server.entity.collection.Collection;
import com.inventory.app.server.entity.collection.CollectionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionDetailsRepository extends JpaRepository<CollectionDetails, Long> {
    CollectionDetails findByTitle(String title);
    List<CollectionDetails> findByCreatedBy(String createdBy);
    List<CollectionDetails> findByTag(String tag);


}
