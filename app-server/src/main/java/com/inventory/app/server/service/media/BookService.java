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

    public List<Book> getAllBooksByCollectionTitle(String collectionTitle, String username) {
        List<Book> bookList = dao.findByField("collection_name", collectionTitle, username);
        if (bookList.isEmpty()){
            throw new ResourceNotFoundException("No book results found with collection title " + collectionTitle);
        }
        return bookList;
    }

    public List<Book> getAllBooksByAuthor(List<String> author, String username) {
        List<Book> bookList = dao.findByField(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey(), author, username);
        if (bookList.isEmpty()) {
            throw new ResourceNotFoundException("No book results found by author " + author);
        }
        return bookList;
    }

    public List<Book> getAllBooksByGenre(String genre, String username) {
        List<Book> bookList = dao.findByField("genre", genre, username);
        if (bookList.isEmpty()) {
            throw new ResourceNotFoundException("No book data exists for genre " + genre);
        }
        return  bookList;
    }

    public List<Book> getAllByUsername(String username) {
        List<Book> bookList = dao.findAllByUsername(username);
        if (bookList.isEmpty()) {
            throw new ResourceNotFoundException("No book data exists.");
        }
        return bookList;
    }

    public Book getById(Long id, String username) {
        try {
            return dao.findOne(id, username);
        } catch(Exception e) {
            if ( e.getClass().isInstance(EntityNotFoundException.class)) {
                throw new ResourceNotFoundException("No book exists with id: " + id);
            } else {
                throw e;
            }
        }
    }

    public Book create(Book book, String username) {
        if (bookAlreadyExists(book, username)) {
            throw new ResourceAlreadyExistsException("Cannot create book because book already exists: " + book);
        }
        Book bookToSave = cloneBook(book);
        bookToSave.setId(null);
        bookToSave.setVersion(1);
        return dao.createOrUpdate(bookToSave);
    }

    public Book update(Book updatedBook, String username) {
        if (!bookAlreadyExists(updatedBook, username)) {
            throw new ResourceNotFoundException("Cannot update book because book does not exist: " + updatedBook);
        }
        Book existingBook = getById(updatedBook.getId(), username);
        if (verifyIfBookUpdated(existingBook, updatedBook)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Book: " + existingBook + "Updated Book: " + updatedBook);
        }
        updatedBook = cloneBook(updatedBook);
        updatedBook.setId(existingBook.getId());
        updatedBook.setVersion(existingBook.getVersion() + 1);
        return dao.createOrUpdate(updatedBook);
    }

    public Book deleteById(Long id, String username){
        Book book = getById(id, username);
        if (book == null) {
            throw new ResourceNotFoundException("Cannot delete book because book does not exist.");
        }
        dao.deleteById(id, username);
        return book;
    }

    private Book cloneBook(Book updatedBook) {
        Book clonedBook = new Book();
        BeanUtils.copyProperties(updatedBook, clonedBook);
        return clonedBook;
    }

    private boolean bookAlreadyExists(Book book, String username) {
        return getAllBooksByAuthor(book.getAuthors(), username)
                .stream()
                .anyMatch(b -> book.getTitle().equals(b.getTitle()));
    }

    private boolean verifyIfBookUpdated(Book existingBook, Book updatedBook){
        return existingBook.equals(updatedBook);
    }
}
