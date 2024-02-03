package com.inventory.app.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.app.server.config.MediaInventoryAdditionalAttributes;
import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.entity.payload.request.MediaRequest;
import com.inventory.app.server.service.media.BookService;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    @MockBean
    private BookService bookService;

    @Autowired
    private BookController underTest;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenBookControllerInjected_thenNotNull() {assertThat(underTest).isNotNull();}

    @Test
    public void whenPostRequestToBookAndValidBook_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Book mockBook = createBook("Test", "hardcover", "Comedy", 1, Arrays.asList("Jon Snow"), "Jamie's Stuff", 2023 );

        // Mock the behavior of bookService.create
        when(bookService.create(any())).thenReturn(mockBook);

        ConcurrentHashMap<String, Object> additionalBookAttributes = new ConcurrentHashMap<>();
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey(), mockBook.getAuthors());
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.COPYRIGHT_YEAR.getJsonKey(), mockBook.getCopyrightYear());
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.EDITION.getJsonKey(), mockBook.getEdition());
        MediaRequest mediaRequest = MediaRequest.builder()
                .title(mockBook.getTitle())
                .genre(mockBook.getGenre())
                .format(mockBook.getFormat())
                .collectionName(mockBook.getCollectionName())
                .additionalAttributes(additionalBookAttributes)
                .build();

        // Convert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String book = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .content(book)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(mockBook.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(mockBook.getVersion()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockBook.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockBook.getGenre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockBook.getFormat()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockBook.getCollectionName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.authors").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.edition").value(mockBook.getEdition()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.copyright_year").value(mockBook.getCopyrightYear()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPostRequestToBookAndInvalidBook_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        MediaRequest mediaRequest = new MediaRequest();

        // Convert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String book = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .content(book)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format", Is.is("Format is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre", Is.is("Genre is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName", Is.is("Collection Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes", Is.is("Additional attributes are mandatory.")))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPutRequestToBookAndValidBook_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Book mockBook = createBook(1L, 1, "Title", "hardcover", "Comedy", 1, Arrays.asList("Jon Snow"), "Jamie's Stuff", 2023);

        // Mock the behavior of bookService.update
        when(bookService.update(any())).thenReturn(mockBook);

        ConcurrentHashMap<String, Object> additionalBookAttributes = new ConcurrentHashMap<>();
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.AUTHORS.getJsonKey(), mockBook.getAuthors());
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.COPYRIGHT_YEAR.getJsonKey(), mockBook.getCopyrightYear());
        additionalBookAttributes.put(MediaInventoryAdditionalAttributes.EDITION.getJsonKey(), mockBook.getEdition());
        MediaRequest mediaRequest = MediaRequest.builder()
                .id(mockBook.getId())
                .version(mockBook.getVersion())
                .title(mockBook.getTitle())
                .genre(mockBook.getGenre())
                .format(mockBook.getFormat())
                .collectionName(mockBook.getCollectionName())
                .additionalAttributes(additionalBookAttributes)
                .build();

        // Convert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String book = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/books/{id}", mediaRequest.getId())
                        .content(book)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(mockBook.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value(mockBook.getVersion()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(mockBook.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value(mockBook.getGenre()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format").value(mockBook.getFormat()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName").value(mockBook.getCollectionName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.authors").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.edition").value(mockBook.getEdition()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes.copyright_year").value(mockBook.getCopyrightYear()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenPutRequestToBookAndInvalidBook_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        MediaRequest mediaRequest = new MediaRequest();
        mediaRequest.setId(1L);

        // Convert MediaRequest to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String book = objectMapper.writeValueAsString(mediaRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/books/{id}", mediaRequest.getId())
                        .content(book)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Is.is("Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.format", Is.is("Format is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre", Is.is("Genre is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.collectionName", Is.is("Collection Title is mandatory.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.additionalAttributes", Is.is("Additional attributes are mandatory.")))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenGetByAuthorsRequestValid_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        List<String> authors = Arrays.asList("Jessie Pinkman", "Jon Snow");
        Book book = new Book();
        when(bookService.getAllBooksByAuthor(authors)).thenReturn(Arrays.asList(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/books/{authors}",  String.join(",", authors))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenGetAllBoooksRequestValid_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Book book = new Book();
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }

    @Test
    public void whenDeleteByIdRequestValid_thenCorrectResponse() throws Exception {
        MediaType jsonMediaType = new MediaType(MediaType.APPLICATION_JSON);

        Book book = new Book();
        book.setId(1L);
        when(bookService.deleteById(1L)).thenReturn(book);

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(book.getId()))
                .andExpect(MockMvcResultMatchers.content().contentType(jsonMediaType));
    }
    private Book createBook(Long id, Integer version, String title, String format, String genre, Integer edition, List<String> authors, String collectionName, Integer copyrightYear) {
        Book book = new Book();
        book.setId(id);
        book.setVersion(version);
        book.setTitle(title);
        book.setFormat(format);
        book.setGenre(genre);
        book.setEdition(edition);
        book.setAuthors(authors);
        book.setCollectionName(collectionName);
        book.setCopyrightYear(copyrightYear);
        return book;
    }

    private Book createBook(String title, String format, String genre, Integer edition, List<String> authors, String collectionName, Integer copyrightYear) {
        Book book = new Book();
        book.setTitle(title);
        book.setFormat(format);
        book.setGenre(genre);
        book.setEdition(edition);
        book.setAuthors(authors);
        book.setCollectionName(collectionName);
        book.setCopyrightYear(copyrightYear);
        return book;
    }

}