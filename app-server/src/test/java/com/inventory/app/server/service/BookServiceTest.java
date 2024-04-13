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

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        searchMediaRequest = new SearchMediaRequest();
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
        String username = "jpompei";
        Book book1 = new Book();
        book1.setCollectionTitle(collectionTitle);
        book1.setCreatedBy(username);
        Book book2 = new Book();
        book2.setCollectionTitle(collectionTitle);
        book2.setCreatedBy(username);
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        searchMediaRequest.setUsername(username);
        searchMediaRequest.setCollectionTitle(collectionTitle);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.searchBooks(searchMediaRequest);
        // THEN
        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findAll();
    }

    @Test
    public void searchBooksByAuthor() {
        // GIVEN
        List<String> authors = Arrays.asList("Author1", "Author2");
        String username = "jpompei";
        Book book1 = new Book();
        book1.setAuthors(authors);
        book1.setCreatedBy(username);
        Book book2 = new Book();
        book2.setAuthors(authors);
        book2.setCreatedBy(username);
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        searchMediaRequest.setUsername(username);
        searchMediaRequest.getAdditionalAttributes().put(AUTHORS.getJsonKey(), authors);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.searchBooks(searchMediaRequest);
        // THEN
        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findAll();
    }

    @Test
    public void searchBooksByGenre() {
        // GIVEN
        String genre = "Fiction";
        String username = "jpompei";
        Book book1 = new Book();
        book1.setCreatedBy(username);
        book1.setGenre(genre);
        Book book2 = new Book();
        book2.setCreatedBy(username);
        book2.setGenre(genre);
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        searchMediaRequest.setUsername(username);
        searchMediaRequest.setGenre(genre);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.searchBooks(searchMediaRequest);
        // THEN
        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findAll();
    }

    @Test
    public void searchBooksByTitle() {
        // GIVEN
        String title = "BookTitle";
        String username = "jpompei";
        Book book1 = new Book();
        book1.setCreatedBy(username);
        book1.setTitle(title);
        List<Book> expectedBooks = Arrays.asList(book1);
        searchMediaRequest.setUsername(username);
        searchMediaRequest.setTitle(title);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.searchBooks(searchMediaRequest);
        // THEN
        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findAll();
    }

    @Test
    public void getAllBooks() {
        // GIVEN
        String username = "jpompei";
        Book book1 = new Book();
        book1.setCreatedBy(username);
        Book book2 = new Book();
        book2.setCreatedBy(username);
        List<Book> expectedBooks = Arrays.asList(book1, book2);

        searchMediaRequest.setUsername(username);
        // WHEN
        when(daoMock.findAll()).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.searchBooks(searchMediaRequest);
        // THEN
        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findAll();
    }

    @Test
    public void getBookById() {
        // GIVEN
        Long bookId = 1L;
        String username = "jpompei";
        Book expectedBook = new Book();
        expectedBook.setId(bookId);
        expectedBook.setCreatedBy(username);
        // WHEN
        when(daoMock.findOne(bookId, username)).thenReturn(expectedBook);
        Book actualBook = underTest.getById(bookId,username);
        // THEN
        assertEquals(expectedBook, actualBook);
        verify(daoMock, times(1)).findOne(bookId, username);
    }

    @Test
    public void create() {
        // GIVEN
        String username = "jpompei";
        Book inputBook = new Book();
        inputBook.setAuthors(Arrays.asList("Author1", "Author2"));
        inputBook.setTitle("Sample Book");
        inputBook.setCreatedBy(username);
        // WHEN
        when(daoMock.findAll()).thenReturn(Collections.emptyList());
        when(daoMock.createOrUpdate(inputBook)).thenReturn(inputBook);
        Book savedBook = underTest.create(inputBook);
        // THEN
        assertNotNull(savedBook);
        assertEquals(inputBook.getTitle(), savedBook.getTitle());
        verify(daoMock, times(1)).createOrUpdate(any(Book.class));
    }

    @Test
    public void update() {
        // GIVEN
        String username = "jpompei";
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
        verify(daoMock, times(1)).createOrUpdate(updatedBook);
    }

    @Test
    public void deleteById() {
        // GIVEN
        Long bookId = 1L;
        String username = "jpompei";
        Book bookToDelete = new Book();
        bookToDelete.setCreatedBy(username);
        // WHEN
        when(daoMock.findOne(bookId, username)).thenReturn(bookToDelete);
        Book result = underTest.deleteById(bookId, username);
        // THEN
        assertNotNull(result);
        verify(daoMock, times(1)).deleteById(bookId, username);
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