package com.inventory.app.server.controller;

import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Book;
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
    //TODO input validation for fields in additional attributes
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
            //check if exists already? check by title and author i suppose...
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
    public ResponseEntity<MediaResponse> updateBook(@RequestBody MediaRequest bookRequest, @PathVariable Long id){
        try{
            if (bookRequest.getMediaId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request. Media request must contain a valid id.");
            }
            log.info("received request to update resource: " + bookRequest);
            Book existingBook = bookService.getBookById(id);
            if (existingBook == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found, could not update " + bookRequest);
            }
            //TODO validations on updated book?
            Book updatedBook = BookMapper.INSTANCE.mapMediaRequestToBook(bookRequest);
            //TODO move this to the service layer
            if (!existingBook.equals(updatedBook)){
                existingBook.setAuthors(updatedBook.getAuthors());
                existingBook.setEdition(updatedBook.getEdition());
                existingBook.setFormat(updatedBook.getFormat());
                existingBook.setTitle(updatedBook.getTitle());
                existingBook.setGenre(updatedBook.getGenre());
                existingBook.setCopyrightYear(updatedBook.getCopyrightYear());
                existingBook.setCollectionName(updatedBook.getCollectionName());
                existingBook.setVersion(existingBook.getVersion() + 1);
            }
            MediaResponse response = BookMapper.INSTANCE.mapBookToMediaResponseWithAdditionalAttributes(bookService.update(existingBook));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e);
        }
    }
}
