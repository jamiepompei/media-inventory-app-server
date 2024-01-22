package com.inventory.app.server.service.media;

import com.google.common.base.Preconditions;
import com.inventory.app.server.entity.Book;
import com.inventory.app.server.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
public class BookService {

    private IBaseDao<Book> dao;

    public BookService(IBaseDao<Book> dao) {
        this.dao = dao;
    }

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<Book> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Book.class);
    }


    public List<Book> getAllBooksByCollectionTitle(String collectionTitle) {
     return  dao.findAll();
    }


    public List<Book> getAllBooksByAuthor(String author){
        Preconditions.checkNotNull(author);
        return dao.findByField("author", author);
    }


    public List<Book> getAllBooksByGenre(String genre){
        Preconditions.checkNotNull(genre);
        return dao.findByField("genre", genre);
    }


    public List<Book> getAllBooks() {
        return dao.findAll();
    }

    public Book getBookById(Long id){
        return dao.findOne(id);
    }


    public Book create(Book book){
       // Preconditions.checkNotNull(book);
        Book bookToSave = new Book();
        bookToSave.setAuthors(book.getAuthors());
        bookToSave.setTitle(book.getTitle());
        bookToSave.setEdition(book.getEdition());
        bookToSave.setFormat(book.getFormat());
        bookToSave.setCopyrightYear(book.getCopyrightYear());
        bookToSave.setCollectionName(book.getCollectionName());
        bookToSave.setVersion(1);
        bookToSave.setGenre(book.getGenre());
        bookToSave.setId(null);
        return  dao.createOrUpdate(bookToSave);
    }

    public Book update(Book book) {
        Preconditions.checkNotNull(book);
        return dao.createOrUpdate(book);
    }
}
