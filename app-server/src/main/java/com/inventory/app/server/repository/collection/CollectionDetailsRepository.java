package com.inventory.app.server.repository.collection;

import com.inventory.app.server.entity.collection.CollectionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionDetailsRepository extends JpaRepository<CollectionDetails, Long> {
}