package com.inventory.app.server.controller;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.Book;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.mapper.BookMapper;
import com.inventory.app.server.service.media.BookService;
import com.inventory.app.server.utility.RestPreConditions;
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
    ResponseEntity<List<MediaResponse>> findAllBooks(){
        log.info("Received a request to get all books");
        List<MediaResponse> responseList = bookService.getAllBooks().stream()
                .map(b -> BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(b))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping(value = "/{author}")
    ResponseEntity<List<MediaResponse>> findByAuthor(@PathVariable("author") final String author){
       List<Book> booksByAuthor = RestPreConditions.checkFound(bookService.getAllBooksByAuthor(author));
        if(booksByAuthor.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for " + author);
        }
        List<MediaResponse> responseList = booksByAuthor.stream()
                .map(b -> BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(b))
                .collect(Collectors.toList());

       return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //add validation check that authors is not empty
    public ResponseEntity<MediaResponse> createBook(@RequestBody MediaRequest bookRequest){
        try {
            // Input validation
            if (bookRequest.getMediaId() == null ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Media request must contain a valid id.");
            }
            if (bookRequest.getAdditionalAttributes().containsKey(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey())){
                Object authorsObject = bookRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey());

                if (authorsObject instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<String> authorsList = (List<String>) authorsObject;
                    if (authorsList.isEmpty()){
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Request must contain an author.");
                    }
                }
            }

            log.info("Received request to create resource: " + bookRequest);
            Book book = BookMapper.INSTANCE.mapMediaRequestToBook(bookRequest);
            MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.create(book));
            log.info("Created new book: " + response);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // Handle other exceptions if needed
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MediaResponse> updateBook(@RequestBody Book resource, @PathVariable Long id){
        try{
            log.info("received request to update resource: " + resource);
        } catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request resource: " + resource);
        }
        Book existingBook = bookService.getBookById(id);

        if (existingBook == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found, could not update " + resource);
        }
        //TODO there has to be a more centralized place to do the version increment
        existingBook.setVersion(existingBook.getVersion() + 1);
        MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.update(existingBook));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
