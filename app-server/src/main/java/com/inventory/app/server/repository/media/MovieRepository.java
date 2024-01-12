package com.inventory.app.server.repository.media;

import com.inventory.app.server.entity.media.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
