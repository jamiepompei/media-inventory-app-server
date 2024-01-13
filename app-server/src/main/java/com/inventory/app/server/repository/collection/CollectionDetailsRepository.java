package com.inventory.app.server.repository.collection;


import com.inventory.app.server.entity.collection.CollectionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CollectionDetailsRepository extends JpaRepository<CollectionDetails, Long> {
    CollectionDetails findByTitle(String title);
    List<CollectionDetails> findByCreatedBy(String createdBy);
    List<CollectionDetails> findByTag(String tag);


}
