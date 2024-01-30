package com.inventory.app.server.service.media;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.repository.IBaseDao;
import com.inventory.app.server.utility.RestPreConditions;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
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
        return dao.findByField("collection_name", collectionTitle);
    }

    public List<Book> getAllBooksByAuthor(List<String> author) {
        return dao.findByField(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey(), author);
    }

    public List<Book> getAllBooksByGenre(String genre) {
        return dao.findByField("genre", genre);
    }

    public List<Book> getAllBooks() {
        return dao.findAll();
    }

    public Book getBookById(Long id) {
        return dao.findOne(id);
    }

    public Book create(Book book) {
        // validations before performing create
        RestPreConditions.checkAlreadyExists(bookAlreadyExists(book), book);

        Book bookToSave = cloneBook(book);
        bookToSave.setId(null);
        bookToSave.setVersion(1);

        return dao.createOrUpdate(bookToSave);
    }

    public Book update(Book updatedBook) {
        //validations before performing the update
        Book existingBook = RestPreConditions.checkFound(getBookById(updatedBook.getId()));
        RestPreConditions.checkEquals(existingBook, updatedBook);

        updatedBook = cloneBook(existingBook, updatedBook);
        updatedBook.setVersion(existingBook.getVersion() + 1);

        return dao.createOrUpdate(updatedBook);
    }

    public Book deleteById(Long id){
        Book book = RestPreConditions.checkFound(getBookById(id));
        dao.deleteById(id);
        return book;
    }

    private Book cloneBook(Book book) {
        Book clonedBook = new Book();
        BeanUtils.copyProperties(book, clonedBook);
        return clonedBook;
    }

    private Book cloneBook(Book existingBook, Book updatedBook) {
        BeanUtils.copyProperties(updatedBook, existingBook);
        return existingBook;
    }

    private boolean bookAlreadyExists(Book book) {
        return getAllBooksByAuthor(book.getAuthors())
                .stream()
                .anyMatch(b -> book.getTitle().equals(b.getTitle()));
    }
}
