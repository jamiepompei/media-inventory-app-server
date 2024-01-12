package com.inventory.app.server.service;

import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.repository.media.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class BookService {
    private BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    @Transactional
    public List<Book> getAllBooksByCollectionTitle(String collectionTitle) {
      return  getAllBooksByCollectionTitle(collectionTitle);
    }

    @Transactional
    public List<Book> getAllBooksByAuthor(String author){
        return bookRepository.findByAuthor(author);
    }

    @Transactional
    public List<Book> getAllBooksByGenre(String genre){
        return bookRepository.findByGenre(genre);
    }
}
