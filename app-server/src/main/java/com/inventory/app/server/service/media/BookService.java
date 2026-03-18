package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.mapper.BookMapper;
import com.inventory.app.server.repository.IBaseDao;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BookService implements BaseService<Book> {

    private IBaseDao<Book> dao;

    @Autowired
    public void setDao(@Qualifier("genericDaoImpl") IBaseDao<Book> daoToSet) {
        dao = daoToSet;
        dao.setClazz(Book.class);
    }

   public List<MediaResponse> search(SearchMediaRequest searchRequest) {
       Optional<Predicate<Book>> searchPredicate = buildSearchPredicate(searchRequest);
       return searchPredicate.map(bookPredicate -> dao.findAll().stream()
               .filter(bookPredicate)
               .map(BookMapper.INSTANCE::mapBookToMediaResponse)
               .collect(Collectors.toList()))
               .orElse(Collections.emptyList());
   }

    private Optional<Predicate<Book>> buildSearchPredicate(SearchMediaRequest searchMediaRequest) {
        Predicate<Book> predicate = book -> true; // Default Predicate

        if (searchMediaRequest.getTitle() != null && !searchMediaRequest.getTitle().isEmpty()) {
            predicate = predicate.and(book -> book.getTitle().equals(searchMediaRequest.getTitle()));
        }
        if (searchMediaRequest.getGenre() != null && !searchMediaRequest.getGenre().isEmpty()) {
            predicate = predicate.and(book -> book.getGenre().equals(searchMediaRequest.getGenre()));
        }
        if (searchMediaRequest.getFormat() != null && !searchMediaRequest.getFormat().isEmpty()) {
            predicate = predicate.and(book -> book.getFormat().equals(searchMediaRequest.getFormat()));
        }
        if (searchMediaRequest.getUsername() != null && !searchMediaRequest.getUsername().isEmpty()) {
            predicate = predicate.and(book -> book.getCreatedBy().equals(searchMediaRequest.getUsername()));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(AUTHORS.getJsonKey()) != null && !searchMediaRequest.getAdditionalAttributes().get(AUTHORS.getJsonKey()).toString().isEmpty()) {
            predicate = predicate.and(book -> book.getAuthors().equals(searchMediaRequest.getAdditionalAttributes().get(AUTHORS.getJsonKey())));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(COPYRIGHT_YEAR.getJsonKey()) != null) {
            predicate = predicate.and(book -> book.getCopyrightYear().equals(searchMediaRequest.getAdditionalAttributes().get(COPYRIGHT_YEAR.getJsonKey())));
        }
        if (searchMediaRequest.getAdditionalAttributes().get(EDITION.getJsonKey()) != null) {
            predicate = predicate.and(book -> book.getEdition().equals(searchMediaRequest.getAdditionalAttributes().get(EDITION.getJsonKey())));
        }
        if (searchMediaRequest.getUsername() != null && !searchMediaRequest.getUsername().isEmpty()) {
            predicate = predicate.and(book -> book.getCreatedBy().equals(searchMediaRequest.getUsername()));
        }
        return Optional.of(predicate);
    }

    public Optional<Book> getById(Long id, String username) {
        Book book = dao.findOne(id, username);
        return book == null ? Optional.empty() : Optional.of(book);
    }

    public MediaResponse create(UpdateCreateMediaRequest updateCreateMediaRequest) {
        Book book = BookMapper.INSTANCE.mapMediaRequestToBook(updateCreateMediaRequest);
        if (book.getId() != null) {
            Optional<Book> existingBook = getById(book.getId(), book.getCreatedBy());
            if (existingBook.isPresent()) {
                throw new ResourceAlreadyExistsException("Cannot create book because book already exists: " + book);
            }
        }
        log.info("Initiating book POST request. Book to be created: " + book);
        return BookMapper.INSTANCE.mapBookToMediaResponse(dao.createOrUpdate(book));
    }

    public MediaResponse update(UpdateCreateMediaRequest updateCreateMediaRequest) {
        Book updatedBook = BookMapper.INSTANCE.mapMediaRequestToBook(updateCreateMediaRequest);
        Optional<Book> existingBook = getById(updatedBook.getId(), updatedBook.getCreatedBy());

        if (existingBook.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update book because no book exists " + updatedBook);
        }
        if (verifyIfBookUpdated(existingBook.get(), updatedBook)) {
            throw new NoChangesToUpdateException("No updates in book to save. Will not proceed with update. Existing Book: " + existingBook + "Updated Book: " + updatedBook);
        }
        return BookMapper.INSTANCE.mapBookToMediaResponse(dao.createOrUpdate(updatedBook));
    }

    public Long deleteById(Long id, String username){
        Optional<Book> book = getById(id, username);
        if (book.isEmpty()) {
            throw new ResourceNotFoundException("Cannot delete book because book does not exist.");
        }
        dao.deleteById(id, username);
        return book.get().getId();
    }

    private boolean verifyIfBookUpdated(Book existingBook, Book updatedBook){
        return existingBook.equals(updatedBook);
    }
}
