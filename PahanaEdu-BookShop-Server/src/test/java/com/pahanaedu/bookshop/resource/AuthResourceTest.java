package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.UserDAO;
import com.pahanaedu.bookshop.model.LoginRequest;
import com.pahanaedu.bookshop.model.User;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthResourceTest {

    private UserDAO userDAO;
    private AuthResource authResource;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        authResource = new AuthResource(userDAO);
    }

    @Test
    void testLoginSuccess() throws SQLException {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("password123");

        User mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUsername("john");
        mockUser.setPasswordHash("hashed_pw");

        when(userDAO.getUserByUsername("john")).thenReturn(mockUser);
        when(userDAO.checkPassword("password123", "hashed_pw")).thenReturn(true);

        Response response = authResource.login(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        User returnedUser = (User) response.getEntity();
        assertEquals("john", returnedUser.getUsername());
        assertNull(returnedUser.getPasswordHash()); 
    }

    @Test
    void testLoginInvalidPassword() throws SQLException {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("wrong");

        User mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUsername("john");
        mockUser.setPasswordHash("hashed_pw");

        when(userDAO.getUserByUsername("john")).thenReturn(mockUser);
        when(userDAO.checkPassword("wrong", "hashed_pw")).thenReturn(false);

        Response response = authResource.login(request);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid username or password"));
    }

    @Test
    void testLoginUserNotFound() throws SQLException {
        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("123");

        when(userDAO.getUserByUsername("unknown")).thenReturn(null);

        Response response = authResource.login(request);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testLoginDatabaseError() throws SQLException {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("password123");

        when(userDAO.getUserByUsername("john")).thenThrow(new SQLException("DB error"));

        Response response = authResource.login(request);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Database error"));
    }
}
