package com.inventory.app.server.service;

import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.repository.IBaseDao;
import com.inventory.app.server.service.media.BookService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.inventory.app.server.config.MediaInventoryAdditionalAttributes.AUTHORS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class BookServiceTest {
    @Mock
    private IBaseDao<Book> daoMock;

    @InjectMocks
    private BookService underTest;
    private SearchMediaRequest searchMediaRequest;
    private String username;
    private Book book;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        username = "jpompei";
        searchMediaRequest = new SearchMediaRequest();
        searchMediaRequest.setAdditionalAttributes(new ConcurrentHashMap<>());
        searchMediaRequest.setUsername(username);
        book = createBook(1L, 1, Collections.emptyList(), null, username);
    }

    @After
    public void tearDown() {
        // Reset mocks and clear any invocations
        reset(daoMock);
        searchMediaRequest = null;
    }

    @Test
    public void setDao() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void searchBooksByCollectionTitle() {
        // GIVEN
        String collectionTitle = "Collection1";
        book.setCollectionTitle(collectionTitle);
        List<Book> expectedBooks = Arrays.asList(book, book);
        searchMediaRequest.setCollectionTitle(collectionTitle);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.searchBooks(searchMediaRequest);
        // THEN
        assertEquals(expectedBooks, actualBooks);
    }

    @Test
    public void searchBooksByAuthor() {
        // GIVEN
        List<String> authors = Arrays.asList("Author1", "Author2");
        book.setAuthors(authors);
        List<Book> expectedBooks = Arrays.asList(book, book);
        searchMediaRequest.getAdditionalAttributes().put(AUTHORS.getJsonKey(), authors);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.searchBooks(searchMediaRequest);
        // THEN
        assertEquals(expectedBooks, actualBooks);
    }

    @Test
    public void searchBooksByGenre() {
        // GIVEN
        String genre = "Fiction";
        book.setGenre(genre);
        List<Book> expectedBooks = Arrays.asList(book, book);
        searchMediaRequest.setGenre(genre);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.searchBooks(searchMediaRequest);
        // THEN
        assertEquals(expectedBooks, actualBooks);
    }

    @Test
    public void searchBooksByTitle() {
        // GIVEN
        String title = "BookTitle";
        book.setTitle(title);
        List<Book> expectedBooks = Arrays.asList(book);
        searchMediaRequest.setTitle(title);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.searchBooks(searchMediaRequest);
        // THEN
        assertEquals(expectedBooks, actualBooks);
    }

    @Test
    public void getAllBooks() {
        // GIVEN
        List<Book> expectedBooks = Arrays.asList(book, book);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.searchBooks(searchMediaRequest);
        // THEN
        assertEquals(expectedBooks, actualBooks);
    }

    @Test
    public void getBookById() {
        // GIVEN
        Long bookId = 1L;

        // WHEN
        when(daoMock.findOne(bookId, username)).thenReturn(book);
        Book actualBook = underTest.getById(bookId, username).get();
        // THEN
        assertEquals(book, actualBook);
    }

    @Test
    public void create() {
        // GIVEN
        Book inputBook = book;
        inputBook.setAuthors(Arrays.asList("Author1", "Author2"));
        inputBook.setTitle("Sample Book");
        // WHEN
        when(daoMock.findAll()).thenReturn(Collections.emptyList());
        when(daoMock.createOrUpdate(inputBook)).thenReturn(inputBook);
        Book savedBook = underTest.create(inputBook);
        // THEN
        assertNotNull(savedBook);
        assertEquals(inputBook.getTitle(), savedBook.getTitle());
    }

    @Test
    public void update() {
        // GIVEN
        Integer expectedVersion = 2;
        Book existingBook = createBook(1L, 1,Arrays.asList("Author1", "Author2"), "Sample Book", username);
        Book updatedBook = createBook(1L, 2, Arrays.asList("Author1", "Author2"), "Updated Book", username);
        // WHEN
        when(daoMock.findOne(updatedBook.getId(), username)).thenReturn(existingBook);
        when(daoMock.createOrUpdate(updatedBook)).thenReturn(updatedBook);
        Book result = underTest.update(updatedBook);
        // THEN
        assertNotNull(result);
        assertEquals(updatedBook.getTitle(), result.getTitle());
        assertEquals(expectedVersion, result.getVersion());
    }

    @Test
    public void deleteById() {
        // GIVEN
        Long bookId = 1L;
        // WHEN
        when(daoMock.findOne(bookId, username)).thenReturn(book);
        Book result = underTest.deleteById(bookId, username);
        // THEN
        assertNotNull(result);
    }

    private Book createBook(Long id, Integer version, List<String> authors, String title, String createdBy){
        Book book = new Book();
        book.setId(id);
        book.setVersion(version);
        book.setAuthors(authors);
        book.setTitle(title);
        book.setCreatedBy(createdBy);
        return book;
    }
}