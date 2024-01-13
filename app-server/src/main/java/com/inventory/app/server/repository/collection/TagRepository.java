package com.inventory.app.server.repository.collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagRepository, Long> {
    TagRepository findByTag(String tag);
}
