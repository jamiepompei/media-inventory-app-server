package com.inventory.app.server.controller.media;

import com.inventory.app.server.entity.payload.request.DeleteMediaRequest;
import com.inventory.app.server.entity.payload.request.SearchMediaRequest;
import com.inventory.app.server.entity.payload.request.UpdateCreateMediaRequest;
import com.inventory.app.server.entity.payload.response.MediaResponse;
import com.inventory.app.server.factory.ServiceFactory;
import com.inventory.app.server.service.media.BaseService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MediaControllerTest {
    @Mock
    private ServiceFactory serviceFactory;

    @Mock
    private BaseService<Object> baseService;

    @InjectMocks
    private MediaController<Object> underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(serviceFactory.getService(any())).thenReturn(baseService);
    }

    @Test
    public void testSearch_Success() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        SearchMediaRequest searchRequest = SearchMediaRequest.builder().build();
        MediaResponse mediaResponse = new MediaResponse();
        when(baseService.search(any())).thenReturn(Collections.singletonList(mediaResponse));

        // Act
        ResponseEntity<List<MediaResponse>> response = underTest.search(userDetails, searchRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(baseService, times(1)).search(any());
    }

    @Test
    public void testCreateMedia_Success() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        UpdateCreateMediaRequest createRequest = UpdateCreateMediaRequest.builder().build();
        MediaResponse mediaResponse = new MediaResponse();
        mediaResponse.setId(1L);
        when(baseService.create(any())).thenReturn(mediaResponse);

        // Act
        ResponseEntity<MediaResponse> response = underTest.createMedia(userDetails, createRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(baseService, times(1)).create(any());
    }

    @Test
    public void testUpdateMedia_Success() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        UpdateCreateMediaRequest updateRequest = UpdateCreateMediaRequest.builder().build();
        MediaResponse mediaResponse = new MediaResponse();
        mediaResponse.setId(1L);
        when(baseService.update(any())).thenReturn(mediaResponse);

        // Act
        ResponseEntity<MediaResponse> response = underTest.updateMedia(userDetails, updateRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(baseService, times(1)).update(any());
    }

    @Test
    public void testDeleteMedia_Success() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        DeleteMediaRequest deleteRequest = new DeleteMediaRequest();
        deleteRequest.setId(1L);
        when(baseService.deleteById(anyLong(), anyString())).thenReturn(1L);

        // Act
        ResponseEntity<Long> response = underTest.deleteMedia(userDetails, deleteRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody());
        verify(baseService, times(1)).deleteById(anyLong(), anyString());
    }

    @Test
    public void testSearch_Exception() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        SearchMediaRequest searchRequest = SearchMediaRequest.builder().build();
        when(baseService.search(any())).thenThrow(new RuntimeException("Search failed"));

        // Act
        ResponseEntity<List<MediaResponse>> response = null;
        try {
            response = underTest.search(userDetails, searchRequest);
        } catch (Exception e) {
            // Assert
            assertEquals("Search failed", e.getMessage());
        }

        verify(baseService, times(1)).search(any());
        assertNull(response);
    }

    @Test
    public void testCreateMedia_Exception() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        UpdateCreateMediaRequest createRequest = UpdateCreateMediaRequest.builder().build();
        when(baseService.create(any())).thenThrow(new RuntimeException("Create failed"));

        // Act
        ResponseEntity<MediaResponse> response = null;
        try {
            response = underTest.createMedia(userDetails, createRequest);
        } catch (Exception e) {
            // Assert
            assertEquals("500 INTERNAL_SERVER_ERROR \"An error occurred while creating the resource.\"", e.getMessage());
        }

        verify(baseService, times(1)).create(any());
        assertNull(response);
    }

    @Test
    public void testUpdateMedia_Exception() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        UpdateCreateMediaRequest updateRequest = UpdateCreateMediaRequest.builder().build();
        when(baseService.update(any())).thenThrow(new RuntimeException("Update failed"));

        // Act
        ResponseEntity<MediaResponse> response = null;
        try {
            response = underTest.updateMedia(userDetails, updateRequest);
        } catch (Exception e) {
            // Assert
            assertEquals("500 INTERNAL_SERVER_ERROR \"An error occurred while updating the resource.\"", e.getMessage());
        }

        verify(baseService, times(1)).update(any());
        assertNull(response);
    }

    @Test
    public void testDeleteMedia_Exception() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        DeleteMediaRequest deleteRequest = new DeleteMediaRequest();
        deleteRequest.setId(1L);
        when(baseService.deleteById(anyLong(), anyString())).thenThrow(new RuntimeException("Delete failed"));

        // Act
        ResponseEntity<Long> response = null;
        try {
            response = underTest.deleteMedia(userDetails, deleteRequest);
        } catch (Exception e) {
            // Assert
            assertEquals("Delete failed", e.getMessage());
        }

        verify(baseService, times(1)).deleteById(anyLong(), anyString());
        assertNull(response);
    }

}