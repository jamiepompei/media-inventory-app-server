package com.inventory.app.server.repository.media;

import com.inventory.app.server.entity.media.TelevisionShow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelevisionShowRepository extends JpaRepository<TelevisionShow, Long> {
    List<TelevisionShow> findByGenre(String genre);
    List<TelevisionShow> findByTitle(String title);
}
