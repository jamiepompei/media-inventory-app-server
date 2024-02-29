package com.inventory.app.server.service.media;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.persistence.EntityNotFoundException;
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
        List<Book> bookList = dao.findByField("collection_name", collectionTitle);
        if (bookList.isEmpty()){
            throw new ResourceNotFoundException("No book results found with collection title " + collectionTitle);
        }
        return bookList;
    }

    public List<Book> getAllBooksByAuthor(List<String> author) {
        List<Book> bookList = dao.findByField(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey(), author);
        if (bookList.isEmpty()) {
            throw new ResourceNotFoundException("No book results found by author " + author);
        }
        return bookList;
    }

    public List<Book> getAllBooksByGenre(String genre) {
        List<Book> bookList = dao.findByField("genre", genre);
        if (bookList.isEmpty()) {
            throw new ResourceNotFoundException("No book data exists for genre " + genre);
        }
        return  bookList;
    }

    public List<Book> getAll() {
        List<Book> bookList = dao.findAll();
        if (bookList.isEmpty()) {
            throw new ResourceNotFoundException("No book data exists.");
        }
        return bookList;
    }

    public Book getById(Long id) {
        try {
            return dao.findOne(id);
        } catch(Exception e) {
            if ( e.getClass().isInstance(EntityNotFoundException.class)) {
                throw new ResourceNotFoundException("No book exists with id: " + id);
            } else {
                throw e;
            }
        }
    }

    public Book create(Book book) {
        if (bookAlreadyExists(book)) {
            throw new ResourceAlreadyExistsException("Cannot create book because book already exists: " + book);
        }
        Book bookToSave = cloneBook(book);
        bookToSave.setId(null);
        bookToSave.setVersion(1);
        return dao.createOrUpdate(bookToSave);
    }

    public Book update(Book updatedBook) {
        if (!bookAlreadyExists(updatedBook)) {
            throw new ResourceNotFoundException("Cannot update book because book does not exist: " + updatedBook);
        }
        Book existingBook = getById(updatedBook.getId());
        if (verifyIfBookUpdated(existingBook, updatedBook)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Book: " + existingBook + "Updated Book: " + updatedBook);
        }
        updatedBook = cloneBook(existingBook, updatedBook);
        updatedBook.setVersion(existingBook.getVersion() + 1);
        return dao.createOrUpdate(updatedBook);
    }

    public Book deleteById(Long id){
        Book book = getById(id);
        if (book == null) {
            throw new ResourceNotFoundException("Cannot delete book because book does not exist.");
        }
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

    private boolean verifyIfBookUpdated(Book existingBook, Book updatedBook){
        return existingBook.equals(updatedBook);
    }
}
