package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.CustomerDAO;
import com.pahanaedu.bookshop.dao.NotificationDAO;
import com.pahanaedu.bookshop.model.Customer;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerResourceTest {

    @Mock
    private CustomerDAO customerDAO;

    @Mock
    private NotificationDAO notificationDAO;

    private CustomerResource customerResource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerResource = new CustomerResource(customerDAO, notificationDAO);
    }

    @Test
    void testGetAllCustomers_Success() throws SQLException {
        List<Customer> mockCustomers = Arrays.asList(new Customer(1, "Alice", "alice@example.com"));
        when(customerDAO.getAllCustomers()).thenReturn(mockCustomers);

        Response response = customerResource.getAllCustomers();

        assertEquals(200, response.getStatus());
        assertEquals(mockCustomers, response.getEntity());
    }

    @Test
    void testGetAllCustomers_DbError() throws SQLException {
        when(customerDAO.getAllCustomers()).thenThrow(new SQLException("DB error"));

        Response response = customerResource.getAllCustomers();

        assertEquals(500, response.getStatus());
        assertTrue(response.getEntity().toString().contains("DB error"));
    }

    @Test
    void testGetCustomerById_Found() throws SQLException {
        Customer mockCustomer = new Customer(1, "Bob", "bob@example.com");
        when(customerDAO.getCustomerById(1)).thenReturn(mockCustomer);

        Response response = customerResource.getCustomerById(1);

        assertEquals(200, response.getStatus());
        assertEquals(mockCustomer, response.getEntity());
    }

    @Test
    void testGetCustomerById_NotFound() throws SQLException {
        when(customerDAO.getCustomerById(99)).thenReturn(null);

        Response response = customerResource.getCustomerById(99);

        assertEquals(404, response.getStatus());
        assertEquals("Customer not found.", response.getEntity());
    }

    @Test
    void testAddCustomer_Success() throws SQLException {
        Customer newCustomer = new Customer(2, "Charlie", "charlie@example.com");

        Response response = customerResource.addCustomer(newCustomer);

        verify(customerDAO, times(1)).addCustomer(newCustomer);
        verify(notificationDAO, times(1))
                .createNotification("A new customer 'Charlie' has been added.", null);

        assertEquals(201, response.getStatus());
        assertEquals(newCustomer, response.getEntity());
    }

    @Test
    void testAddCustomer_DbError() throws SQLException {
        Customer newCustomer = new Customer(2, "Charlie", "charlie@example.com");
        doThrow(new SQLException("Insert failed")).when(customerDAO).addCustomer(newCustomer);

        Response response = customerResource.addCustomer(newCustomer);

        assertEquals(500, response.getStatus());
        assertTrue(response.getEntity().toString().contains("Insert failed"));
    }

    @Test
    void testUpdateCustomer_Found() throws SQLException {
        Customer existing = new Customer(3, "David", "david@example.com");
        Customer updated = new Customer(3, "David Updated", "david.updated@example.com");

        when(customerDAO.getCustomerById(3)).thenReturn(existing);

        Response response = customerResource.updateCustomer(3, updated);

        verify(customerDAO, times(1)).updateCustomer(updated);
        verify(notificationDAO, times(1))
                .createNotification("Customer 'David' has been updated.", null);

        assertEquals(200, response.getStatus());
        assertEquals(updated, response.getEntity());
    }

    @Test
    void testUpdateCustomer_NotFound() throws SQLException {
        when(customerDAO.getCustomerById(5)).thenReturn(null);

        Response response = customerResource.updateCustomer(5, new Customer());

        assertEquals(404, response.getStatus());
        assertEquals("Customer not found.", response.getEntity());
    }

    @Test
    void testDeleteCustomer_Found() throws SQLException {
        Customer mockCustomer = new Customer(4, "Eva", "eva@example.com");
        when(customerDAO.getCustomerById(4)).thenReturn(mockCustomer);

        Response response = customerResource.deleteCustomer(4);

        verify(customerDAO, times(1)).deleteCustomer(4);
        verify(notificationDAO, times(1))
                .createNotification("Customer 'Eva' has been deleted.", null);

        assertEquals(204, response.getStatus());
    }

    @Test
    void testDeleteCustomer_NotFound() throws SQLException {
        when(customerDAO.getCustomerById(10)).thenReturn(null);

        Response response = customerResource.deleteCustomer(10);

        assertEquals(404, response.getStatus());
        assertEquals("Customer not found.", response.getEntity());
    }
}
