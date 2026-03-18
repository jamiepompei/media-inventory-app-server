package com.inventory.app.server.service;

import com.inventory.app.server.entity.media.Book;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.error.NoChangesToUpdateException;
import com.inventory.app.server.error.ResourceAlreadyExistsException;
import com.inventory.app.server.error.ResourceNotFoundException;
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

import static org.junit.Assert.*;
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
        searchMediaRequest = SearchMediaRequest.builder()
                .username(username)
                .additionalAttributes(new ConcurrentHashMap<>())
                .entityType("Book")
                .build();

        book = createBook(1L, Collections.emptyList(), null, username);
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
    public void createBook_AlreadyExists() {
        // GIVEN
        Book existingBook = createBook(1L,  Arrays.asList("Author1"), "Existing Book", username);
        existingBook.setGenre("Fiction");
        existingBook.setFormat("Hardcover");
        existingBook.setModifiedBy(username);
        Book newBook = createBook(1L, Arrays.asList("Author1"), "Existing Book", username);
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put("authors", newBook.getAuthors());
        UpdateCreateMediaRequest request = UpdateCreateMediaRequest.builder()
                .title(newBook.getTitle())
                .username(username)
                .id(newBook.getId())
                .genre("Fiction")
                .format("Hardcover")
                .additionalAttributes(additionalAttributes)
                .build();
        // WHEN
        when(daoMock.findOne(newBook.getId(), username)).thenReturn(existingBook);
        // THEN
        assertThrows(ResourceAlreadyExistsException.class, () -> underTest.create(request));
        verify(daoMock, never()).createOrUpdate(any());
    }

    @Test
    public void updateBook_NoChanges() {
        // GIVEN
        Book existingBook = createBook(1L, Arrays.asList("Author1"), "Sample Book", username);
        existingBook.setFormat("Hardcover");
        existingBook.setGenre("Fiction");
        existingBook.setOnLoan(false);
        existingBook.setCompleted(false);
        existingBook.setModifiedBy("jpompei");
        Book updatedBook = createBook(1L, Arrays.asList("Author1"), "Sample Book", username);
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put("authors", updatedBook.getAuthors());
        UpdateCreateMediaRequest request = UpdateCreateMediaRequest.builder()
                .username(username)
                .title(updatedBook.getTitle())
                .id(updatedBook.getId())
                .format("Hardcover")
                .genre("Fiction")
                .additionalAttributes(additionalAttributes)
                .build();
        // WHEN
        when(daoMock.findOne(updatedBook.getId(), username)).thenReturn(existingBook);
        // THEN
        assertThrows(NoChangesToUpdateException.class, () -> underTest.update(request));
        verify(daoMock, never()).createOrUpdate(any());
    }

    @Test
    public void updateBook_NotFound() {
        // GIVEN
        Book updatedBook = createBook(1L, Arrays.asList("Author1"), "Updated Book", username);
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put("authors", updatedBook.getAuthors());
        UpdateCreateMediaRequest request = UpdateCreateMediaRequest.builder()
                .title(updatedBook.getTitle())
                .id(updatedBook.getId())
                .format("Hardcover")
                .genre("Fiction")
                .additionalAttributes(additionalAttributes)
                .build();
        // WHEN
        when(daoMock.findOne(updatedBook.getId(), username)).thenReturn(null);
        // THEN
        assertThrows(ResourceNotFoundException.class, () -> underTest.update(request));
        verify(daoMock, never()).createOrUpdate(any());
    }

    @Test
    public void deleteBook_NotFound() {
        // GIVEN
        Long bookId = 1L;
        // WHEN
        when(daoMock.findOne(bookId, username)).thenReturn(null);
        // THEN
        assertThrows(ResourceNotFoundException.class, () -> underTest.deleteById(bookId, username));
        verify(daoMock, never()).deleteById(anyLong(), anyString());
    }

    @Test
    public void createBook_HappyPath() {
        // GIVEN
        Book newBook = createBook(1L, Arrays.asList("Author1"), "New Book", username);
        newBook.setGenre("Fiction");
        newBook.setFormat("Hardcover");
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put("authors", newBook.getAuthors());
        UpdateCreateMediaRequest request = UpdateCreateMediaRequest.builder()
                .title(newBook.getTitle())
                .username(username)
                .genre("Fiction")
                .format("Hardcover")
                .additionalAttributes(additionalAttributes)
                .build();

        // WHEN
        when(daoMock.createOrUpdate(any(Book.class))).thenReturn(newBook);

        // THEN
        MediaResponse response = underTest.create(request);
        assertNotNull(response);
        assertEquals(newBook.getTitle(), response.getTitle());
        verify(daoMock, times(1)).createOrUpdate(any(Book.class));
    }

    @Test
    public void updateBook_HappyPath() {
        // GIVEN
        Book existingBook = createBook(1L, Arrays.asList("Author1"), "Existing Book", username);
        existingBook.setGenre("Fiction");
        existingBook.setFormat("Hardcover");
        Book updatedBook = createBook(1L, Arrays.asList("Author1"), "Updated Book", username);
        updatedBook.setGenre("Fiction");
        updatedBook.setFormat("Hardcover");
        ConcurrentHashMap<String, Object> additionalAttributes = new ConcurrentHashMap<>();
        additionalAttributes.put("authors", updatedBook.getAuthors());
        UpdateCreateMediaRequest request = UpdateCreateMediaRequest.builder()
                .id(updatedBook.getId())
                .title(updatedBook.getTitle())
                .username(username)
                .genre("Fiction")
                .format("Hardcover")
                .additionalAttributes(additionalAttributes)
                .build();

        // WHEN
        when(daoMock.findOne(updatedBook.getId(), username)).thenReturn(existingBook);
        when(daoMock.createOrUpdate(any(Book.class))).thenReturn(updatedBook);

        // THEN
        MediaResponse response = underTest.update(request);
        assertNotNull(response);
        assertEquals(updatedBook.getTitle(), response.getTitle());
        verify(daoMock, times(1)).createOrUpdate(any(Book.class));
    }

    private Book createBook(Long id, List<String> authors, String title, String createdBy) {
        Book book = new Book();
        book.setId(id);
        book.setAuthors(authors);
        book.setTitle(title);
        book.setCreatedBy(createdBy);
        return book;
    }
}