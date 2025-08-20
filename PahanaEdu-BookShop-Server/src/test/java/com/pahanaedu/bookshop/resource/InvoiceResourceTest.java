package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.InvoiceDAO;
import com.pahanaedu.bookshop.dto.InvoiceItemDetailsDTO;
import com.pahanaedu.bookshop.model.Invoice;
import com.pahanaedu.bookshop.model.InvoiceItem;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceResourceTest {

    private InvoiceDAO invoiceDAOMock;
    private InvoiceResource invoiceResource;

    @BeforeEach
    void setUp() {
        invoiceDAOMock = mock(InvoiceDAO.class);
        invoiceResource = new InvoiceResource(invoiceDAOMock); // inject mock
    }

    @Test
    void testCreateInvoice_Success() throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setUserId(1);
        invoice.setCustomerId(1);
        invoice.setTotalAmount(BigDecimal.valueOf(100));
        invoice.setDiscountAmount(BigDecimal.valueOf(10));
        invoice.setNetAmount(BigDecimal.valueOf(90));
        invoice.setPaymentStatus("PAID");
        invoice.setInvoiceItems(new ArrayList<>());

        when(invoiceDAOMock.createInvoice(ArgumentMatchers.any(Invoice.class))).thenReturn(123);

        Response response = invoiceResource.createInvoice(invoice);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Invoice responseInvoice = (Invoice) response.getEntity();
        assertEquals(123, responseInvoice.getInvoiceId());
        assertNotNull(responseInvoice.getInvoiceNumber());
        verify(invoiceDAOMock, times(1)).createInvoice(invoice);
    }

    @Test
    void testCreateInvoice_InsufficientStock() throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setUserId(1);
        invoice.setCustomerId(1);
        invoice.setInvoiceItems(new ArrayList<>());

        when(invoiceDAOMock.createInvoice(ArgumentMatchers.any(Invoice.class)))
                .thenThrow(new SQLException("Insufficient stock for product ID: 5"));

        Response response = invoiceResource.createInvoice(invoice);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        String body = (String) response.getEntity();
        assertTrue(body.contains("Insufficient stock"));
    }

    @Test
    void testSearchInvoiceItems_Found() throws SQLException {
        List<InvoiceItemDetailsDTO> items = new ArrayList<>();
        InvoiceItemDetailsDTO dto = new InvoiceItemDetailsDTO();
        dto.setInvoiceItemId(1);
        dto.setInvoiceNumber("INV-123");
        dto.setProductName("Product A");
        dto.setQuantityPurchased(2);
        dto.setUnitPrice(BigDecimal.valueOf(50));
        items.add(dto);

        when(invoiceDAOMock.findInvoiceItemsByInvoiceNumber("INV-123")).thenReturn(items);

        Response response = invoiceResource.searchInvoiceItems("INV-123");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<InvoiceItemDetailsDTO> responseItems = (List<InvoiceItemDetailsDTO>) response.getEntity();
        assertEquals(1, responseItems.size());
        assertEquals("Product A", responseItems.get(0).getProductName());
    }

    @Test
    void testSearchInvoiceItems_NotFound() throws SQLException {
        when(invoiceDAOMock.findInvoiceItemsByInvoiceNumber("INV-999")).thenReturn(new ArrayList<>());

        Response response = invoiceResource.searchInvoiceItems("INV-999");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAllInvoices_Success() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(1);
        invoice.setInvoiceNumber("INV-001");
        invoices.add(invoice);

        when(invoiceDAOMock.getAllInvoices()).thenReturn(invoices);

        Response response = invoiceResource.getAllInvoices();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Invoice> responseInvoices = (List<Invoice>) response.getEntity();
        assertEquals(1, responseInvoices.size());
        assertEquals("INV-001", responseInvoices.get(0).getInvoiceNumber());
    }

    @Test
    void testGetAllInvoices_Failure() throws SQLException {
        when(invoiceDAOMock.getAllInvoices()).thenThrow(new SQLException("DB Error"));

        Response response = invoiceResource.getAllInvoices();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        String body = (String) response.getEntity();
        assertTrue(body.contains("Failed to retrieve invoices"));
    }
}
