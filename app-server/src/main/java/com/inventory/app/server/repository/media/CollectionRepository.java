package com.inventory.app.server.repository.media;

import com.inventory.app.server.entity.collection.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
}
