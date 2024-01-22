package com.inventory.app.server.controller;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.Book;
import com.inventory.app.server.entity.payload.MediaRequest;
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
    ResponseEntity<List<Book>> findAllBooks(){
        log.info("Received a request to get all books");
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks());
    }

    @GetMapping(value = "/{author}")
    ResponseEntity<List<Book>> findByAuthor(@PathVariable("author") final String author){
       List<Book> booksByAuthor = RestPreConditions.checkFound(bookService.getAllBooksByAuthor(author));
        if(booksByAuthor.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found for " + author);
        } else return ResponseEntity.status(HttpStatus.OK).body(booksByAuthor);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //add validation check that authors is not empty
    public ResponseEntity<Book> createBook(@RequestBody MediaRequest bookRequest){
        try {
            // Input validation
            if (bookRequest.getMediaId() == null ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Media request must contain a valid id.");
            }
            if (bookRequest.getAdditionalAttributes().containsKey(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey())){
                Object authorsObject = bookRequest.getAdditionalAttributes().get(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey());

                if (authorsObject instanceof List<?>) {
                    List<String> authorsList = (List<String>) authorsObject;
                    if (authorsList.isEmpty()){
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Request must contain an author.");
                    }
                }
            }

            log.info("Received request to create resource: " + bookRequest);

            // Map using MapStruct -todo ensure this resolves to the implementation of the mapper
            Book book = BookMapper.INSTANCE.mapMediaRequestToBook(bookRequest);
            log.info("Created new book: " + book);
            return new ResponseEntity<>(bookService.create(book), HttpStatus.CREATED);
        } catch (Exception e) {
            // Handle other exceptions if needed
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Book> updateBook(@RequestBody Book resource, @PathVariable Long id){
        try{
            log.info("received request to update resource: " + resource);
        } catch (NullPointerException e){
            throw new ResponseStatusException((HttpStatus.BAD_REQUEST), "Bad request resource: " + resource);
        }
        Book existingBook = bookService.getBookById(id);
        //TODO add check for ifExists
        //TODO clean up mapping logic
        existingBook.setVersion(existingBook.getVersion() + 1);
        existingBook.setAuthors(resource.getAuthors());
        existingBook.setTitle(resource.getTitle());
        existingBook.setGenre(resource.getGenre());
        existingBook.setFormat(resource.getFormat());
        existingBook.setEdition(resource.getEdition());
        existingBook.setCopyrightYear(resource.getCopyrightYear());
        existingBook.setCollectionName(resource.getCollectionName());
        return ResponseEntity.status(HttpStatus.OK).body(bookService.update(existingBook));
    }
}
