package com.inventory.app.server.controller;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.entity.payload.request.MediaId;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.BookMapper;
import com.inventory.app.server.service.media.BookService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
        log.info("Received a request to get all books");
        List<MediaResponse> responseList = bookService.getAllBooks().stream()
                .map(b -> BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(b))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{author}")
    ResponseEntity<List<MediaResponse>> findByAuthor(@PathVariable("author") final List<String> author) {
        List<Book> booksByAuthor = bookService.getAllBooksByAuthor(author);
        if (booksByAuthor.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for " + author);
        }
        List<MediaResponse> responseList = booksByAuthor.stream()
                .map(b -> BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(b))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createBook(@RequestBody MediaRequest bookRequest) {
        try {
            // Validate mediaId input
            validateCreateMediaId(bookRequest.getMediaId());
            // Validate additional attributes
            validatedAdditionalAttributes(bookRequest);
            log.info("Received request to create resource: " + bookRequest);
            Book book = BookMapper.INSTANCE.mapMediaRequestToBook(bookRequest);
            MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.create(book));
            log.info("Created new book: " + response);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // todo fix with custom exception
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateBook(@RequestBody MediaRequest bookRequest) {
        try {
            // Validate MediaId
            validateUpdateMediaId(bookRequest.getMediaId());
            // Validate authors, copyright year, and edition
            validatedAdditionalAttributes(bookRequest);
            log.info("received request to update resource: " + bookRequest);
            Book updatedBook = BookMapper.INSTANCE.mapMediaRequestToBook(bookRequest);
            MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.update(updatedBook));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            //TODo fix controller error handling with custom error messages
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }

    private void validatedAdditionalAttributes(MediaRequest bookRequest) {
        @SuppressWarnings("unchecked")
        List<String> authors = (List<String>) bookRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey());
        Integer copyrightYear = (Integer) bookRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.COPYRIGHT_YEAR.getJsonKey());
        Integer edition = (Integer) bookRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.EDITION.getJsonKey());

        if (authors == null || authors.isEmpty() || copyrightYear == null || edition == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Authors, copyrightYear, and edition must not be null or empty.");
        }
    }

    private void validateUpdateMediaId(MediaId mediaId) {
        if (mediaId == null || mediaId.getId() == null || StringUtils.isEmpty(mediaId.getTitle()) || StringUtils.isEmpty(mediaId.getFormat())
                || StringUtils.isEmpty(mediaId.getGenre()) || StringUtils.isEmpty(mediaId.getCollectionName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. MediaId must have all non-null and non-empty fields.");
        }
    }

    private void validateCreateMediaId(MediaId mediaId) {
        if (mediaId == null || StringUtils.isEmpty(mediaId.getTitle()) || StringUtils.isEmpty(mediaId.getFormat())
                || StringUtils.isEmpty(mediaId.getGenre()) || StringUtils.isEmpty(mediaId.getCollectionName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. MediaId must have all non-null and non-empty fields.");
        }
    }
}
