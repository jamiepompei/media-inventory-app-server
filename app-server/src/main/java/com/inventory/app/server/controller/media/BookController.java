package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.BookMapper;
import com.inventory.app.server.service.media.BookService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER, 'ROLE_VIEW')")
    @GetMapping
    ResponseEntity<List<MediaResponse>> searchBooks(@AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody final SearchMediaRequest searchMediaRequest) {
        List<MediaResponse> responseList = bookService.searchBooks(searchMediaRequest).stream()
                .map(b -> BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(b))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MediaResponse> createBook(@AuthenticationPrincipal UserDetails userDetails,
                                                    @Valid @RequestBody final UpdateCreateMediaRequest bookRequest) {
        log.info("Received request to create resource: " + bookRequest);
        Book book = BookMapper.INSTANCE.mapMediaRequestToBook(bookRequest);
        MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.create(book));
        log.info("Created new book: " + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateBook(@Valid @RequestBody final UpdateCreateMediaRequest bookRequest) {
        log.info("received request to update resource: " + bookRequest);
        Book updatedBook = BookMapper.INSTANCE.mapMediaRequestToBook(bookRequest);
        MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.update(updatedBook));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> deleteBook(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable("id") final Long id){
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Id cannot be null or empty.");
        }
        //TODO fix
        MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.deleteById(id, "username"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
