package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.UserDAO;
import com.pahanaedu.bookshop.model.ProfileUpdateRequest;
import com.pahanaedu.bookshop.model.User;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserResourceTest {

    private UserDAO mockUserDAO;
    private UserResource userResource;

    @BeforeEach
    public void setUp() {
        mockUserDAO = mock(UserDAO.class);
        userResource = new UserResource(mockUserDAO); // inject mock DAO
    }

    @Test
    public void testGetAllUsers() throws SQLException {
        User user1 = new User();
        user1.setUserId(1);
        user1.setUsername("john");

        User user2 = new User();
        user2.setUserId(2);
        user2.setUsername("alice");

        List<User> users = Arrays.asList(user1, user2);

        when(mockUserDAO.getAllUsers()).thenReturn(users);

        Response response = userResource.getAllUsers();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(users, response.getEntity());
    }

    @Test
    public void testAddUser_Success() throws SQLException {
        User user = new User();
        user.setUsername("newuser");
        user.setPasswordHash("password123");

        Response response = userResource.addUser(user);

        verify(mockUserDAO, times(1)).addUser(user);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testAddUser_MissingPassword() throws SQLException {
        User user = new User();
        user.setUsername("nopassword");

        Response response = userResource.addUser(user);

        verify(mockUserDAO, never()).addUser(user);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetUserById_Found() throws SQLException {
        User user = new User();
        user.setUserId(1);
        user.setUsername("john");

        when(mockUserDAO.getUserById(1)).thenReturn(user);

        Response response = userResource.getUserById(1);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(user, response.getEntity());
    }

    @Test
    public void testGetUserById_NotFound() throws SQLException {
        when(mockUserDAO.getUserById(99)).thenReturn(null);

        Response response = userResource.getUserById(99);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateUser() throws SQLException {
        User user = new User();
        user.setUsername("updateuser");
        user.setRole("admin");

        Response response = userResource.updateUser(1, user);

        verify(mockUserDAO, times(1)).updateUser(user);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(user, response.getEntity());
    }

    @Test
    public void testDeleteUser() throws SQLException {
        Response response = userResource.deleteUser(1);

        verify(mockUserDAO, times(1)).deleteUser(1);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateUserProfile() throws SQLException {
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setName("Updated Name");
        request.setEmail("updated@example.com");
        request.setPhone("1234567890");
        request.setAddress("New Address");
        request.setNewPassword("newpass123");

        Response response = userResource.updateUserProfile(1, request);

        verify(mockUserDAO, times(1)).updateUserProfile(1, request);
        verify(mockUserDAO, times(1)).changeUserPassword(1, "newpass123");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
