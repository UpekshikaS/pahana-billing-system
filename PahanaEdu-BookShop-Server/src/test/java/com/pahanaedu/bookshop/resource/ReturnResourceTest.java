package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.NotificationDAO;
import com.pahanaedu.bookshop.dao.ReturnDAO;
import com.pahanaedu.bookshop.model.Return;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReturnResourceTest {

    private ReturnDAO returnDAOMock;
    private NotificationDAO notificationDAOMock;
    private ReturnResource returnResource;

    @BeforeEach
    void setUp() {
        // Mock the DAOs
        returnDAOMock = mock(ReturnDAO.class);
        notificationDAOMock = mock(NotificationDAO.class);

        // Inject mocks into ReturnResource using a constructor
        returnResource = new ReturnResource() {
            {
                // override private DAOs via anonymous subclass
                try {
                    java.lang.reflect.Field returnDAOField = ReturnResource.class.getDeclaredField("returnDAO");
                    returnDAOField.setAccessible(true);
                    returnDAOField.set(this, returnDAOMock);

                    java.lang.reflect.Field notificationDAOField = ReturnResource.class.getDeclaredField("notificationDAO");
                    notificationDAOField.setAccessible(true);
                    notificationDAOField.set(this, notificationDAOMock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testCreateReturn() throws Exception {
        // Arrange
        Return aReturn = new Return();
        aReturn.setInvoiceItemId(101);
        aReturn.setQuantityReturned(2);
        aReturn.setReason("Damaged");
        aReturn.setProcessedByUserId(10);

        String notificationMessage = "Returned item 'ProductX' (Qty: 2) processed.";
        when(returnDAOMock.processReturn(aReturn)).thenReturn(notificationMessage);

        // Act
        Response response = returnResource.createReturn(aReturn);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(aReturn, response.getEntity());
        verify(returnDAOMock, times(1)).processReturn(aReturn);
        verify(notificationDAOMock, times(1)).createNotification(notificationMessage, aReturn.getProcessedByUserId());
    }

    @Test
    void testCreateReturn_InvalidQuantity() throws Exception {
        // Arrange
        Return aReturn = new Return();
        aReturn.setInvoiceItemId(101);
        aReturn.setQuantityReturned(-1); // invalid
        aReturn.setReason("Invalid");
        aReturn.setProcessedByUserId(10);

        when(returnDAOMock.processReturn(aReturn))
                .thenThrow(new IllegalArgumentException("Invalid return quantity. Cannot exceed remaining returnable quantity."));

        // Act
        Response response = returnResource.createReturn(aReturn);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        String json = (String) response.getEntity();
        assertTrue(json.contains("Invalid return quantity"));
        verify(returnDAOMock, times(1)).processReturn(aReturn);
        verifyNoInteractions(notificationDAOMock);
    }

    @Test
    void testCreateReturn_DatabaseError() throws Exception {
        // Arrange
        Return aReturn = new Return();
        aReturn.setInvoiceItemId(101);
        aReturn.setQuantityReturned(2);
        aReturn.setReason("Damaged");
        aReturn.setProcessedByUserId(10);

        when(returnDAOMock.processReturn(aReturn)).thenThrow(new SQLException("DB connection failed"));

        // Act
        Response response = returnResource.createReturn(aReturn);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        String json = (String) response.getEntity();
        assertTrue(json.contains("Failed to process return"));
        verify(returnDAOMock, times(1)).processReturn(aReturn);
        verifyNoInteractions(notificationDAOMock);
    }

    @Test
    void testCreateReturn_UnexpectedException() throws Exception {
        // Arrange
        Return aReturn = new Return();
        aReturn.setInvoiceItemId(101);
        aReturn.setQuantityReturned(2);
        aReturn.setReason("Damaged");
        aReturn.setProcessedByUserId(10);

        when(returnDAOMock.processReturn(aReturn)).thenThrow(new RuntimeException("Unexpected"));

        // Act
        Response response = returnResource.createReturn(aReturn);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        String json = (String) response.getEntity();
        assertTrue(json.contains("Unexpected error occurred"));
        verify(returnDAOMock, times(1)).processReturn(aReturn);
        verifyNoInteractions(notificationDAOMock);
    }
}
