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
    private BookService underTest;

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
    public void setDao() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    public void getAllBooksByCollectionTitle() {
        // GIVEN
        String collectionTitle = "Collection1";
        List<Book> expectedBooks = Arrays.asList(new Book(), new Book());
        String username = "jpompei";
        // WHEN
        when(daoMock.findByField("collection_name", collectionTitle, username)).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.getAllBooksByCollectionTitle(collectionTitle, username);
        // THEN
        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findByField("collection_name", collectionTitle, username);
    }

    @Test
    public void getAllBooksByAuthor() {
        // GIVEN
        List<String> authors = Arrays.asList("Author1", "Author2");
        List<Book> expectedBooks = Arrays.asList(new Book(), new Book());
        String username = "jpompei";
        // WHEN
        when(daoMock.findByField("authors", authors, username)).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.getAllBooksByAuthor(authors, username);
        // THEN
        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findByField("authors", authors, username);
    }

    @Test
    public void getAllBooksByGenre() {
        // GIVEN
        String genre = "Fiction";
        List<Book> expectedBooks = Arrays.asList(new Book(), new Book());
        String username = "jpompei";
        // WHEN
        when(daoMock.findByField("genre", genre, username)).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.getAllBooksByGenre(genre, username);
        // THEN
        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findByField("genre", genre, username);
    }

    @Test
    public void getAllBooks() {
        // GIVEN
        List<Book> expectedBooks = Arrays.asList(new Book(), new Book());
        String username = "jpompei";
        // WHEN
        when(daoMock.findAllByUsername(username)).thenReturn(expectedBooks);
        List<Book> actualBooks = underTest.getAllByUsername(username);
        // THEN
        assertEquals(expectedBooks, actualBooks);
        verify(daoMock, times(1)).findAllByUsername(username);
    }

    @Test
    public void getBookById() {
        // GIVEN
        Long bookId = 1L;
        Book expectedBook = new Book();
        String username = "jpompei";
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
        Book inputBook = new Book();
        inputBook.setAuthors(Arrays.asList("Author1", "Author2"));
        inputBook.setTitle("Sample Book");
        String username = "jpompei";
        // WHEN
        when(daoMock.findByField(any(), any(), any())).thenReturn(Collections.emptyList());
        Book savedBook = underTest.create(inputBook, username);
        // THEN
        assertNotNull(savedBook);
        assertEquals(inputBook.getTitle(), savedBook.getTitle());
        verify(daoMock, times(1)).createOrUpdate(any(Book.class));
    }

    @Test
    public void update() {
        // GIVEN
        Integer expectedVersion = 2;
        Book existingBook = createBook(1L, 1,Arrays.asList("Author1", "Author2"), "Sample Book");
        Book updatedBook = createBook(1L, 1, Arrays.asList("Author1", "Author2"), "Updated Book");
        String username = "jpompei";
        // WHEN
        when(daoMock.findOne(updatedBook.getId(), username)).thenReturn(existingBook);
        Book result = underTest.update(updatedBook, username);
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
        Book bookToDelete = new Book();
        String username = "jpompei";
        // WHEN
        when(daoMock.findOne(bookId, username)).thenReturn(bookToDelete);
        Book result = underTest.deleteById(bookId, username);
        // THEN
        assertNotNull(result);
        verify(daoMock, times(1)).deleteById(bookId, username);
    }

    private Book createBook(Long id, Integer version, List<String> authors, String title){
        Book book = new Book();
        book.setId(id);
        book.setVersion(version);
        book.setAuthors(authors);
        book.setTitle(title);
        return book;
    }
}