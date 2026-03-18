package com.inventory.app.server.controller;

import com.inventory.app.server.entity.payload.response.UserResponse;
import com.inventory.app.server.entity.user.UserInfo;
import com.inventory.app.server.service.authorization.JwtService;
import com.inventory.app.server.service.user.UserDAOService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    @Mock
    private UserDAOService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserController userController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllUsers_HappyPath() {
        // Arrange
        UserInfo user1 = new UserInfo(1L, "John", "Doe", "johndoe", null);
        UserInfo user2 = new UserInfo(2L, "Jane", "Doe", "janedoe", null);
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // Act
        ResponseEntity<?> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserResponse> userResponses = (List<UserResponse>) response.getBody();
        assertEquals(2, userResponses.size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void testGetAllUsers_Exception() {
        // Arrange
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Failed to fetch users"));

        // Act
        ResponseEntity<?> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        UserResponse errorResponse = (UserResponse) response.getBody();
        assertEquals("Failed to fetch users", errorResponse.getErrorResponse().getErrorMessage());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void testGetUserProfile_HappyPath() {
        // Arrange
        UserResponse userResponse = UserResponse.builder().username("johndoe").build();
        when(userService.getUser()).thenReturn(userResponse);

        // Act
        ResponseEntity<?> response = userController.getUserProfile();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponse responseBody = (UserResponse) response.getBody();
        assertEquals("johndoe", responseBody.getUsername());
        verify(userService, times(1)).getUser();
    }

    @Test
    public void testGetUserProfile_Exception() {
        // Arrange
        when(userService.getUser()).thenThrow(new RuntimeException("Failed to fetch user profile"));

        // Act
        ResponseEntity<?> response = userController.getUserProfile();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        UserResponse errorResponse = (UserResponse) response.getBody();
        assertEquals("Failed to fetch user profile", errorResponse.getErrorResponse().getErrorMessage());
        verify(userService, times(1)).getUser();
    }

    @Test
    public void testAdminAccess_HappyPath() {
        // Act
        ResponseEntity<String> response = userController.test();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Welcome!", response.getBody());
    }
    @Test
    public void testRoleAccess_HappyPath() {
        // Act
        ResponseEntity<String> response = userController.testUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Welcome!", response.getBody());
    }

    @Test
    public void testViewAccess_HappyPath() {
        // Act
        ResponseEntity<String> response = userController.testView();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Welcome!", response.getBody());
    }


}