package com.inventory.app.server.repository.media;

import com.inventory.app.server.entity.media.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
