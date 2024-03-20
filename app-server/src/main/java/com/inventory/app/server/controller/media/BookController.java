package com.inventory.app.server.controller;

import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.BookMapper;
import com.inventory.app.server.service.media.BookService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/books")
@Log4j2
public class BookController {

    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    ResponseEntity<List<MediaResponse>> findAllBooks() {
        List<MediaResponse> responseList = bookService.getAll().stream()
                .map(b -> BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(b))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{collectionTitle}")
    ResponseEntity<List<MediaResponse>> findByCollectionTitle(@PathVariable("collectionTitle") final String collectionTitle) {
        if (collectionTitle.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Collection title cannot be empty.");
        }
        List<Book> booksByCollection = bookService.getAllBooksByCollectionTitle(collectionTitle);
        List<MediaResponse> responseList = booksByCollection.stream()
                .map(b -> BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(b))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{authors}")
    ResponseEntity<List<MediaResponse>> findByAuthor(@PathVariable("authors") final List<String> authors) {
        if (authors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Authors cannot be empty.");
        }
        List<Book> booksByAuthor = bookService.getAllBooksByAuthor(authors);
        List<MediaResponse> responseList = booksByAuthor.stream()
                .map(b -> BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(b))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{genre}")
    ResponseEntity<List<MediaResponse>> findByGenre(@PathVariable("genre") final String genre) {
        if (genre.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Authors cannot be empty.");
        }
        List<Book> booksByGenre = bookService.getAllBooksByGenre(genre);
        List<MediaResponse> responseList = booksByGenre.stream()
                .map(b -> BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(b))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createBook(@Valid @RequestBody final MediaRequest bookRequest) {
        log.info("Received request to create resource: " + bookRequest);
        Book book = BookMapper.INSTANCE.mapMediaRequestToBook(bookRequest);
        MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.create(book));
        log.info("Created new book: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateBook(@Valid @RequestBody final MediaRequest bookRequest) {
        log.info("received request to update resource: " + bookRequest);
        Book updatedBook = BookMapper.INSTANCE.mapMediaRequestToBook(bookRequest);
        MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.update(updatedBook));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteBook(@PathVariable("id") final Long id){
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.deleteById(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
