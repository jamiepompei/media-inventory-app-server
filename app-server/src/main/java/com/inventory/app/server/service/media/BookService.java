package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.inventory.app.server.config.MediaInventoryAdditionalAttributes.*;

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

   public List<Book> searchBooks(SearchMediaRequest searchRequest) {
       Optional<Predicate<Book>> searchPredicate = buildSearchPredicate(searchRequest);
       return searchPredicate.map(bookPredicate -> dao.findAll().stream()
               .filter(bookPredicate)
               .collect(Collectors.toList())).orElse(Collections.emptyList());
   }

    private Optional<Predicate<Book>> buildSearchPredicate(SearchMediaRequest searchMediaRequest) {
        Predicate<Book> predicate = book -> true; // Default Predicate
        if (searchMediaRequest.getCollectionTitle() != null && !searchMediaRequest.getCollectionTitle().isEmpty()) {
            predicate = predicate.and((book -> book.getCollectionTitle().equals(searchMediaRequest.getCollectionTitle())));
        }
        if (searchMediaRequest.getGenre() != null && !searchMediaRequest.getGenre().isEmpty()) {
            predicate = predicate.and((book -> book.getGenre().equals(searchMediaRequest.getGenre())));
        }
        if (searchMediaRequest.getFormat() != null && !searchMediaRequest.getFormat().isEmpty()) {
            predicate = predicate.and((book -> book.getFormat().equals(searchMediaRequest.getFormat())));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(AUTHORS) != null && !searchMediaRequest.getAdditionalAttributes().get(AUTHORS).toString().isEmpty()) {
            predicate = predicate.and((book -> book.getAuthors().equals(searchMediaRequest.getAdditionalAttributes().get(AUTHORS))));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(COPYRIGHT_YEAR) != null) {
            predicate = predicate.and((book -> book.getCopyrightYear().equals(searchMediaRequest.getAdditionalAttributes().get(COPYRIGHT_YEAR))));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(EDITION) != null) {
            predicate = predicate.and((book -> book.getEdition().equals(searchMediaRequest.getAdditionalAttributes().get(EDITION))));
        }
        if (searchMediaRequest.getUsername() != null && !searchMediaRequest.getUsername().isEmpty()) {
            predicate = predicate.and((book -> book.getCreatedBy().equals(searchMediaRequest.getUsername())));
        }
        return Optional.of(predicate);
    }

    //TODO consider returning an optional here, that way methods that use this method can throw their own exceptions
    public Book getById(Long id, String username) {
        try {
            return dao.findOne(id, username);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("No book exists with id: " + id);
        }
    }

    public Book create(Book book) {
        if (bookAlreadyExists(book)) {
            throw new ResourceAlreadyExistsException("Cannot create book because book already exists: " + book);
        }
        return dao.createOrUpdate(book);
    }

    public Book update(Book updatedBook) {
        Book existingBook = getById(updatedBook.getId(), updatedBook.getCreatedBy());

        if (verifyIfBookUpdated(existingBook, updatedBook)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Book: " + existingBook + "Updated Book: " + updatedBook);
        }
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

    //TODO review this method - is this the best way to do this?
    private boolean bookAlreadyExists(Book book) {
        SearchMediaRequest searchMediaRequest = new SearchMediaRequest();
        searchMediaRequest.setTitle(book.getTitle());
        searchMediaRequest.setGenre(book.getGenre());
        searchMediaRequest.setFormat(book.getFormat());
        searchMediaRequest.setUsername(book.getCreatedBy());
        return !searchBooks(searchMediaRequest).isEmpty();
    }

    private boolean verifyIfBookUpdated(Book existingBook, Book updatedBook){
        return existingBook.equals(updatedBook);
    }
}
