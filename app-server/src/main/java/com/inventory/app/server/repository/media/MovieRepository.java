package com.inventory.app.server.repository.media;

import com.inventory.app.server.entity.media.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByGenre(String genre);
    List<Movie> findByTitle(String title);
}
