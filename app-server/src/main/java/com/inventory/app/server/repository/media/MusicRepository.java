package com.inventory.app.server.repository.media;

import com.inventory.app.server.entity.media.Music;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicRepository extends JpaRepository<Music, Long> {
    List<Music> findByArtist(String artist);
    List<Music> findByGenre(String genre);
    List<Music> findSong (String song);
}
