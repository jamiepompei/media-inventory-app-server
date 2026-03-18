package com.inventory.app.server.controller;

import com.inventory.app.server.entity.payload.request.AuthRequestDTO;
import com.inventory.app.server.entity.payload.request.LogoutRequestDTO;
import com.inventory.app.server.entity.payload.request.RefreshTokenRequestDTO;
import com.inventory.app.server.entity.payload.request.UserRequest;
import com.inventory.app.server.entity.payload.response.JwtResponseDTO;
import com.inventory.app.server.entity.payload.response.UserResponse;
import com.inventory.app.server.entity.user.RefreshToken;
import com.inventory.app.server.entity.user.UserInfo;
import com.inventory.app.server.service.RefreshTokenService;
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
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthControllerTest {
    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserDAOService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSignup_HappyPath() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("testUser");
        when(userService.saveUser(any())).thenReturn(userInfo);

        // Act
        ResponseEntity<UserResponse> response = underTest.signup(userRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testUser", response.getBody().getUsername());
        verify(userService, times(1)).saveUser(any());
    }

    @Test
    public void testSignup_Exception() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        when(userService.saveUser(any())).thenThrow(new RuntimeException("Signup failed"));

        // Act
        ResponseEntity<UserResponse> response = underTest.signup(userRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        UserResponse userResponse = response.getBody();
        assertEquals("Signup failed", userResponse.getErrorResponse().getErrorMessage());
        verify(userService, times(1)).saveUser(any());
    }

    @Test
    public void testLogin_HappyPath() {
        // Arrange
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("testUser");
        authRequestDTO.setPassword("password");
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(anyString())).thenReturn("accessToken");
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(RefreshToken.builder().token("refreshToken").build());

        // Act
        ResponseEntity<?> response = underTest.authenticateAndGetToken(authRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JwtResponseDTO jwtResponse = (JwtResponseDTO) response.getBody();
        assertEquals("accessToken", jwtResponse.getAccessToken());
        assertEquals("refreshToken", jwtResponse.getRefreshToken());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void testLogin_Unauthenticated() {
        // Arrange
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("testUser");
        authRequestDTO.setPassword("password");
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(anyString())).thenReturn("accessToken");
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(RefreshToken.builder().token("refreshToken").build());

        // Act
        ResponseEntity<?> response = underTest.authenticateAndGetToken(authRequestDTO);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        UserResponse userResponse = (UserResponse) response.getBody();
        assertEquals("Invalid user credentials.", userResponse.getErrorResponse().getErrorMessage());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, never()).generateToken(anyString());
        verify(refreshTokenService, never()).createRefreshToken(anyString());
    }

    @Test
    public void testLogin_Exception() {
        // Arrange
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("testUser");
        authRequestDTO.setPassword("password");
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Login failed"));

        // Act
        ResponseEntity<?> response = underTest.authenticateAndGetToken(authRequestDTO);
        UserResponse userResponse = (UserResponse) response.getBody();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Login failed", userResponse.getErrorResponse().getErrorMessage());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void testRefreshToken_HappyPath() {
        // Arrange
        RefreshTokenRequestDTO refreshTokenRequestDTO = new RefreshTokenRequestDTO();
        refreshTokenRequestDTO.setToken("validToken");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("validToken");
        refreshToken.setUserInfo(new UserInfo(12L, "testUser", "testlastname", "testemail", LocalDateTime.now()));

        when(refreshTokenService.findByToken(anyString())).thenReturn(java.util.Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(any())).thenReturn(refreshToken);
        when(jwtService.generateToken(anyString())).thenReturn(RefreshToken.builder().token("newAccessToken").build().getToken());
        when(refreshTokenService.generateNewRefreshToken(any())).thenReturn(RefreshToken.builder().token("newRefreshToken").build());

        // Act
        ResponseEntity<?> response = underTest.refreshToken(refreshTokenRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JwtResponseDTO jwtResponse = (JwtResponseDTO) response.getBody();
        System.out.println(jwtResponse.toString());
        assertEquals("newAccessToken", jwtResponse.getAccessToken());
        assertEquals("newRefreshToken", jwtResponse.getRefreshToken());
        verify(refreshTokenService, times(1)).findByToken(anyString());
    }

    @Test
    public void testRefreshToken_errorOnRefreshTokenGeneration() {
        // Arrange
        RefreshTokenRequestDTO refreshTokenRequestDTO = new RefreshTokenRequestDTO();
        refreshTokenRequestDTO.setToken("validToken");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("validToken");
        refreshToken.setUserInfo(new UserInfo(12L, "testUser", "testlastname", "testemail", LocalDateTime.now()));

        when(refreshTokenService.findByToken(anyString())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = underTest.refreshToken(refreshTokenRequestDTO);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        UserResponse userResponse = (UserResponse) response.getBody();
        assertEquals("Invalid refresh token!", userResponse.getErrorResponse().getErrorMessage());
        verify(jwtService, never()).generateToken(anyString());
        verify(refreshTokenService, never()).verifyExpiration(any());
        verify(refreshTokenService, never()).generateNewRefreshToken(any());
        verify(refreshTokenService, times(1)).findByToken(anyString());
    }

    @Test
    public void testRefreshToken_Exception() {
        // Arrange
        RefreshTokenRequestDTO refreshTokenRequestDTO = new RefreshTokenRequestDTO();
        refreshTokenRequestDTO.setToken("invalidToken");
        when(refreshTokenService.findByToken(anyString())).thenThrow(new RuntimeException("Invalid refresh token"));

        // Act
        ResponseEntity<?> response = underTest.refreshToken(refreshTokenRequestDTO);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        UserResponse userResponse = (UserResponse) response.getBody();
        assertEquals("Invalid refresh token", userResponse.getErrorResponse().getErrorMessage());
        verify(refreshTokenService, times(1)).findByToken(anyString());
    }

    @Test
    public void testLogout_HappyPath() {
        // Arrange
        LogoutRequestDTO logoutRequestDTO = new LogoutRequestDTO();
        logoutRequestDTO.setRefreshToken("validToken");
        doNothing().when(refreshTokenService).deleteRefreshToken(anyString());

        // Act
        ResponseEntity<?> response = underTest.logout(logoutRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully", response.getBody());
        verify(refreshTokenService, times(1)).deleteRefreshToken(anyString());
    }

    @Test
    public void testLogout_Exception() {
        // Arrange
        LogoutRequestDTO logoutRequestDTO = new LogoutRequestDTO();
        logoutRequestDTO.setRefreshToken("invalidToken");
        doThrow(new RuntimeException("Logout failed")).when(refreshTokenService).deleteRefreshToken(anyString());

        // Act
        ResponseEntity<?> response = underTest.logout(logoutRequestDTO);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        UserResponse userResponse = (UserResponse) response.getBody();
        assertEquals("Logout failed", userResponse.getErrorResponse().getErrorMessage());
        verify(refreshTokenService, times(1)).deleteRefreshToken(anyString());
    }
}