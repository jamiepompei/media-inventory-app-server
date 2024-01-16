package com.inventory.app.server.controller;

import com.google.common.base.Preconditions;
import com.inventory.app.server.entity.Book;
import com.inventory.app.server.error.ResourceNotFoundException;
import com.inventory.app.server.service.media.BookService;
import com.inventory.app.server.utility.RestPreConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/books")
public class BookController {
    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    ResponseEntity<List<Book>> findAllBooks(){
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
        Preconditions.checkNotNull(resource);
       } catch (NullPointerException e){
            throw new ResponseStatusException((HttpStatus.BAD_REQUEST), "Bad request resource: " + resource);
        }
           return new ResponseEntity<>(bookService.create(resource), HttpStatus.CREATED);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Book> updateBook(@RequestBody Book resource){
        try{
            Preconditions.checkNotNull(resource);
        } catch (NullPointerException e){
            throw new ResponseStatusException((HttpStatus.BAD_REQUEST), "Bad request resource: " + resource);
        }
        return ResponseEntity.status(HttpStatus.OK).body(bookService.update(resource));
    }
}
