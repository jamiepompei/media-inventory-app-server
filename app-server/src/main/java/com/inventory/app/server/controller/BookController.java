package com.inventory.app.server.controller;

import com.google.common.base.Preconditions;
import com.inventory.app.server.entity.Book;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.service.media.BookService;
import com.inventory.app.server.utility.RestPreConditions;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.logging.log4j.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.boot.logging.LogLevel.*;

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
    public ResponseEntity<Book> createBook(@RequestBody Book resource){
        try{
       // Preconditions.checkNotNull(resource);
        log.info("received request to create resource: " + resource);
       } catch (NullPointerException e){
            throw new ResponseStatusException((HttpStatus.BAD_REQUEST), "Bad request resource: " + resource);
        }
        log.info("Created new book: " + resource);
           return new ResponseEntity<>(bookService.create(resource), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Book> updateBook(@RequestBody Book resource, @PathVariable Long id){
        try{
            log.info("received request to update resource: " + resource);
           // Preconditions.checkNotNull(resource);
        } catch (NullPointerException e){
            throw new ResponseStatusException((HttpStatus.BAD_REQUEST), "Bad request resource: " + resource);
        }
        Book existingBook = bookService.getBookById(id);
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
