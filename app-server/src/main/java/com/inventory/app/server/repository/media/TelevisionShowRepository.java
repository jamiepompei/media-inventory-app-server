package com.inventory.app.server.repository.media;

import com.inventory.app.server.entity.media.TelevisionShow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TelevisionShowRepository extends JpaRepository<TelevisionShow, Long> {
    List<TelevisionShow> findByGenre(String genre);
    List<TelevisionShow> findByTitle(String title);
}
