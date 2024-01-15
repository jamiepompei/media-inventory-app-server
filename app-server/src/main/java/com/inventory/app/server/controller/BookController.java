package com.inventory.app.server.controller;

import com.google.common.base.Preconditions;
import com.inventory.app.server.entity.Book;
import com.inventory.app.server.service.media.BookService;
import com.inventory.app.server.utility.RestPreConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    List<Book> findAllBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping(value = "/{author}")
    List<Book> findByAuthor(@PathVariable("author") final String author){
       return RestPreConditions.checkFound(bookService.getAllBooksByAuthor(author));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestBody Book resource){
        Preconditions.checkNotNull(resource);
        return bookService.create(resource);
    }
}
