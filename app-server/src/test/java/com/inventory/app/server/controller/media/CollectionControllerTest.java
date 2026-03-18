package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.collection.Collection;
import com.inventory.app.server.entity.collection.CollectionMedia;
import com.inventory.app.server.entity.payload.request.SearchCollectionRequest;
import com.inventory.app.server.service.collection.CollectionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CollectionControllerTest {
    @Mock
    private CollectionService collectionService;

    @InjectMocks
    private CollectionController collectionController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchCollections_Success() {
        // Arrange
        SearchCollectionRequest searchRequest = SearchCollectionRequest.builder().build();
        Collection collection = new Collection();
        when(collectionService.searchCollections(any())).thenReturn(Collections.singletonList(collection));

        // Act
        ResponseEntity<List<Collection>> response = collectionController.searchCollections(searchRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(collectionService, times(1)).searchCollections(any());
    }

    @Test
    public void testSearchCollections_Exception() {
        // Arrange
        SearchCollectionRequest searchRequest = SearchCollectionRequest.builder().build();
        when(collectionService.searchCollections(any())).thenThrow(new RuntimeException("Search failed"));

        // Act & Assert
        try {
            collectionController.searchCollections(searchRequest);
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatusCode());
            assertEquals("Error occurred while searching collections.", e.getReason());
        }
        verify(collectionService, times(1)).searchCollections(any());
    }

    @Test
    public void testCreateCollection_Success() {
        // Arrange
        CollectionMedia resource = new CollectionMedia();
        Collection collection = new Collection();
        collection.setId(1L);
        when(collectionService.create(any())).thenReturn(collection);

        // Act
        ResponseEntity<Collection> response = collectionController.createCollection(resource);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(collectionService, times(1)).create(any());
    }

    @Test
    public void testCreateCollection_Exception() {
        // Arrange
        CollectionMedia resource = new CollectionMedia();
        when(collectionService.create(any())).thenThrow(new NullPointerException("Invalid resource"));

        // Act & Assert
        try {
            collectionController.createCollection(resource);
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals("Bad request resource: " + resource, e.getReason());
        }
        verify(collectionService, times(1)).create(any());
    }

    @Test
    public void testUpdateCollection_Success() {
        // Arrange
        CollectionMedia resource = new CollectionMedia();
        Collection collection = new Collection();
        collection.setId(1L);
        when(collectionService.update(any())).thenReturn(collection);

        // Act
        ResponseEntity<Collection> response = collectionController.updateCollection(resource);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(collectionService, times(1)).update(any());
    }

    @Test
    public void testUpdateCollection_Exception() {
        // Arrange
        CollectionMedia resource = new CollectionMedia();
        when(collectionService.update(any())).thenThrow(new NullPointerException("Invalid resource"));

        // Act & Assert
        try {
            collectionController.updateCollection(resource);
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals("Bad request resource: " + resource, e.getReason());
        }
        verify(collectionService, times(1)).update(any());
    }
}