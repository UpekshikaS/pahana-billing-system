package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.ReportDAO;
import com.pahanaedu.bookshop.model.report.SalesSummaryDTO;
import com.pahanaedu.bookshop.model.report.TopCustomerDTO;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportResourceTest {

    private ReportDAO reportDAOMock;
    private ReportResource reportResource;

    @BeforeEach
    void setUp() {
        reportDAOMock = mock(ReportDAO.class);

        // Inject mock into ReportResource via reflection
        reportResource = new ReportResource() {
            {
                try {
                    java.lang.reflect.Field field = ReportResource.class.getDeclaredField("reportDAO");
                    field.setAccessible(true);
                    field.set(this, reportDAOMock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testGetSalesSummary_HappyPath() throws SQLException {
        // Arrange
        Date startDate = Date.valueOf("2025-01-01");
        Date endDate = Date.valueOf("2025-01-31");
        SalesSummaryDTO summaryDTO = new SalesSummaryDTO();
        summaryDTO.setTotalRevenue(new BigDecimal("1000.00"));
        summaryDTO.setTotalInvoices(5L);
        summaryDTO.setBestSellingItemName("ItemA");
        summaryDTO.setSalesOverTime(new ArrayList<>());

        when(reportDAOMock.getSalesSummary(startDate, endDate)).thenReturn(summaryDTO);

        // Act
        Response response = reportResource.getSalesSummary("2025-01-01", "2025-01-31");

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(summaryDTO, response.getEntity());
        verify(reportDAOMock, times(1)).getSalesSummary(startDate, endDate);
    }

    @Test
    void testGetSalesSummary_InvalidDateFormat() {
        // Act
        Response response = reportResource.getSalesSummary("invalid-date", "2025-01-31");

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        String json = (String) response.getEntity();
        assertTrue(json.contains("Invalid date format"));
        verifyNoInteractions(reportDAOMock);
    }

    @Test
    void testGetSalesSummary_SQLException() throws SQLException {
        Date startDate = Date.valueOf("2025-01-01");
        Date endDate = Date.valueOf("2025-01-31");

        when(reportDAOMock.getSalesSummary(startDate, endDate)).thenThrow(new SQLException("DB error"));

        Response response = reportResource.getSalesSummary("2025-01-01", "2025-01-31");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        String json = (String) response.getEntity();
        assertTrue(json.contains("Database error fetching sales summary"));
        verify(reportDAOMock, times(1)).getSalesSummary(startDate, endDate);
    }

    @Test
    void testGetTopCustomers_HappyPath() throws SQLException {
        Date startDate = Date.valueOf("2025-01-01");
        Date endDate = Date.valueOf("2025-01-31");

        List<TopCustomerDTO> customers = new ArrayList<>();
        TopCustomerDTO customer = new TopCustomerDTO();
        customer.setName("John Doe");
        customer.setAccountNumber("ACC123");
        customer.setTotalSpent(new BigDecimal("500.00"));
        customer.setTotalInvoices(2L);
        customers.add(customer);

        when(reportDAOMock.getTopCustomers(startDate, endDate)).thenReturn(customers);

        Response response = reportResource.getTopCustomers("2025-01-01", "2025-01-31");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(customers, response.getEntity());
        verify(reportDAOMock, times(1)).getTopCustomers(startDate, endDate);
    }

    @Test
    void testGetTopCustomers_InvalidDateFormat() {
        Response response = reportResource.getTopCustomers("2025-01-01", "invalid-date");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        String json = (String) response.getEntity();
        assertTrue(json.contains("Invalid date format"));
        verifyNoInteractions(reportDAOMock);
    }

    @Test
    void testGetTopCustomers_SQLException() throws SQLException {
        Date startDate = Date.valueOf("2025-01-01");
        Date endDate = Date.valueOf("2025-01-31");

        when(reportDAOMock.getTopCustomers(startDate, endDate)).thenThrow(new SQLException("DB error"));

        Response response = reportResource.getTopCustomers("2025-01-01", "2025-01-31");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        String json = (String) response.getEntity();
        assertTrue(json.contains("Database error fetching top customers"));
        verify(reportDAOMock, times(1)).getTopCustomers(startDate, endDate);
    }    
    
}
