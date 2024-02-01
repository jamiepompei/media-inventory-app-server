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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        try {
            log.info("Received a request to get all books");
            List<MediaResponse> responseList = bookService.getAllBooks().stream()
                    .map(b -> BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(b))
                    .collect(Collectors.toList());
            if (responseList.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No book data exists.");
            }
            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error " + e);
        }
    }

    @GetMapping(value = "/{authors}")
    ResponseEntity<List<MediaResponse>> findByAuthor(@Valid @PathVariable("authors") final List<String> authors) {
        try {
            if (authors.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Authors cannot be empty.");
            }
            List<Book> booksByAuthor = bookService.getAllBooksByAuthor(authors);
            if (booksByAuthor.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for " + authors);
            }
            List<MediaResponse> responseList = booksByAuthor.stream()
                    .map(b -> BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(b))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createBook(@Valid @RequestBody final MediaRequest bookRequest) {
        try {
            log.info("Received request to create resource: " + bookRequest);
            Book book = BookMapper.INSTANCE.mapMediaRequestToBook(bookRequest);
            MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.create(book));
            log.info("Created new book: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + e);
        }
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateBook(@Valid @RequestBody final MediaRequest bookRequest) {
        try {
            log.info("received request to update resource: " + bookRequest);
            Book updatedBook = BookMapper.INSTANCE.mapMediaRequestToBook(bookRequest);
            MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.update(updatedBook));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteBook(@PathVariable("id") final Long id){
        try{
            if (id == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
            }
            Book book = bookService.deleteById(id);
            MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(book);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
