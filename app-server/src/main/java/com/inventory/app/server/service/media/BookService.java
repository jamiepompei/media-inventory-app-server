package com.inventory.app.server.service.media;

import com.google.common.base.Preconditions;
import com.inventory.app.server.entity.Book;
import com.inventory.app.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class BookService {

    private IBaseDao<Book, Long> dao;

    public BookService(IBaseDao<Book, Long> dao) {
        this.dao = dao;
    }

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<Book, Long> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Book.class);
    }

    @Transactional
    public List<Book> getAllBooksByCollectionTitle(String collectionTitle) {
     return  dao.findAll();
    }

    @Transactional
    public List<Book> getAllBooksByAuthor(String author){
        Preconditions.checkNotNull(author);
        return null;
      //  return dao.findByAttributeContainsText("author", author);
    }

    @Transactional
    public List<Book> getAllBooksByGenre(String genre){
        Preconditions.checkNotNull(genre);
        return null;
      //  return dao.findByAttributeContainsText("genre", genre);
    }

    public List<Book> getAllBooks() {
        return dao.findAll();
    }

    public Book create(Book book){
        Preconditions.checkNotNull(book);
        return  dao.create(book);
    }
}
