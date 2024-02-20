package com.inventory.app.server.service.media;

import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.repository.IBaseDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;


public class BookServiceTest {
    @Mock
    private IBaseDao<Book> daoMock;

    @InjectMocks
    private BookService bookService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() {
        // Reset mocks and clear any invocations
        reset(daoMock);
    }

    @Test
    public void getAllBooksByCollectionTitle() {
        String collectionTitle = "Collection1";
        List<Book> expectedBooks = Arrays.asList(new Book(), new Book());

        when(daoMock.findByField("collection_name", collectionTitle)).thenReturn(expectedBooks);

        List<Book> actualBooks = bookService.getAllBooksByCollectionTitle(collectionTitle);

        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findByField("collection_name", collectionTitle);
    }

    @Test
    public void getAllBooksByAuthor() {
        List<String> authors = Arrays.asList("Author1", "Author2");
        List<Book> expectedBooks = Arrays.asList(new Book(), new Book());

        when(daoMock.findByField("authors", authors)).thenReturn(expectedBooks);

        List<Book> actualBooks = bookService.getAllBooksByAuthor(authors);

        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findByField("authors", authors);
    }

    @Test
    public void getAllBooksByGenre() {
        String genre = "Fiction";
        List<Book> expectedBooks = Arrays.asList(new Book(), new Book());

        when(daoMock.findByField("genre", genre)).thenReturn(expectedBooks);

        List<Book> actualBooks = bookService.getAllBooksByGenre(genre);

        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findByField("genre", genre);
    }

    @Test
    public void getAllBooks() {
        List<Book> expectedBooks = Arrays.asList(new Book(), new Book());

        when(daoMock.findAll()).thenReturn(expectedBooks);

        List<Book> actualBooks = bookService.getAllBooks();

        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findAll();
    }

    @Test
    public void getBookById() {
        Long bookId = 1L;
        Book expectedBook = new Book();

        when(daoMock.findOne(bookId)).thenReturn(expectedBook);

        Book actualBook = bookService.getBookById(bookId);

        assertEquals(expectedBook, actualBook);
        verify(daoMock, times(1)).findOne(bookId);
    }

    @Test
    public void create() {
        Book inputBook = new Book();
        inputBook.setAuthors(Arrays.asList("Author1", "Author2"));
        inputBook.setTitle("Sample Book");

        when(daoMock.findByField(any(), any())).thenReturn(Collections.emptyList());

        Book savedBook = bookService.create(inputBook);

        assertNotNull(savedBook);
        assertEquals(inputBook.getTitle(), savedBook.getTitle());

        verify(daoMock, times(1)).createOrUpdate(any(Book.class));
    }

    @Test
    public void update() {
        Integer expectedVersion = 2;
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setVersion(1);
        existingBook.setAuthors(Arrays.asList("Author1", "Author2"));
        existingBook.setTitle("Sample Book");

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setVersion(1);
        updatedBook.setAuthors(Arrays.asList("Author1", "Author2"));
        updatedBook.setTitle("Updated Book");

        when(daoMock.findOne(updatedBook.getId())).thenReturn(existingBook);
        Book result = bookService.update(updatedBook);

        assertNotNull(result);
        assertEquals(updatedBook.getTitle(), result.getTitle());
        assertEquals(expectedVersion, result.getVersion());

        verify(daoMock, times(1)).createOrUpdate(updatedBook);
    }

    @Test
    public void deleteById() {
        Long bookId = 1L;
        Book bookToDelete = new Book();

        when(daoMock.findOne(bookId)).thenReturn(bookToDelete);

        Book result = bookService.deleteById(bookId);

        assertNotNull(result);
        verify(daoMock, times(1)).deleteById(bookId);
    }
}