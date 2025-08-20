package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.ProductDAO;
import com.pahanaedu.bookshop.model.Product;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductResourceTest {

    private ProductDAO productDAOMock;
    private ProductResource productResource;

    @BeforeEach
    void setUp() {
        productDAOMock = mock(ProductDAO.class);

        // Inject mock into ProductResource via reflection
        productResource = new ProductResource() {
            {
                try {
                    java.lang.reflect.Field field = ProductResource.class.getDeclaredField("productDAO");
                    field.setAccessible(true);
                    field.set(this, productDAOMock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testGetAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        Product p = new Product();
        p.setProductId(1);
        p.setItemId("ITEM123");
        products.add(p);

        when(productDAOMock.getAllProducts()).thenReturn(products);

        Response response = productResource.getAllProducts();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(products, response.getEntity());
        verify(productDAOMock, times(1)).getAllProducts();
    }

    @Test
    void testGetAllProducts_SQLException() throws SQLException {
        when(productDAOMock.getAllProducts()).thenThrow(new SQLException("DB error"));

        Response response = productResource.getAllProducts();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Database error fetching products"));
        verify(productDAOMock, times(1)).getAllProducts();
    }

    @Test
    void testGetProductByItemId() throws SQLException {
        Product p = new Product();
        p.setItemId("ITEM123");
        when(productDAOMock.getProductByItemId("ITEM123")).thenReturn(p);

        Response response = productResource.getProductByItemId("ITEM123");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(p, response.getEntity());
        verify(productDAOMock, times(1)).getProductByItemId("ITEM123");
    }

    @Test
    void testGetProductByItemId_NotFound() throws SQLException {
        when(productDAOMock.getProductByItemId("ITEM123")).thenReturn(null);

        Response response = productResource.getProductByItemId("ITEM123");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Product not found"));
        verify(productDAOMock, times(1)).getProductByItemId("ITEM123");
    }

    @Test
    void testGetProductByItemId_SQLException() throws SQLException {
        when(productDAOMock.getProductByItemId("ITEM123")).thenThrow(new SQLException("DB error"));

        Response response = productResource.getProductByItemId("ITEM123");

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Database error fetching product"));
        verify(productDAOMock, times(1)).getProductByItemId("ITEM123");
    }

    @Test
    void testAddProduct() throws SQLException {
        Product p = new Product();
        p.setItemId("ITEM123");

        doNothing().when(productDAOMock).addProduct(p);

        Response response = productResource.addProduct(p);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(p, response.getEntity());
        verify(productDAOMock, times(1)).addProduct(p);
    }

    @Test
    void testAddProduct_SQLException() throws SQLException {
        Product p = new Product();
        p.setItemId("ITEM123");

        doThrow(new SQLException("DB error")).when(productDAOMock).addProduct(p);

        Response response = productResource.addProduct(p);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Failed to add product"));
        verify(productDAOMock, times(1)).addProduct(p);
    }

    @Test
    void testUpdateProduct() throws SQLException {
        Product p = new Product();
        p.setItemId("ITEM123");

        doNothing().when(productDAOMock).updateProduct(p);

        Response response = productResource.updateProduct(1, p);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(p, response.getEntity());
        assertEquals(1, p.getProductId());
        verify(productDAOMock, times(1)).updateProduct(p);
    }

    @Test
    void testUpdateProduct_SQLException() throws SQLException {
        Product p = new Product();
        p.setItemId("ITEM123");

        doThrow(new SQLException("DB error")).when(productDAOMock).updateProduct(p);

        Response response = productResource.updateProduct(1, p);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Failed to update product"));
        verify(productDAOMock, times(1)).updateProduct(p);
    }

    @Test
    void testDeleteProduct() throws SQLException {
        doNothing().when(productDAOMock).deleteProduct(1);

        Response response = productResource.deleteProduct(1);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(productDAOMock, times(1)).deleteProduct(1);
    }

    @Test
    void testDeleteProduct_SQLException() throws SQLException {
        doThrow(new SQLException("DB error")).when(productDAOMock).deleteProduct(1);

        Response response = productResource.deleteProduct(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Failed to delete product"));
        verify(productDAOMock, times(1)).deleteProduct(1);
    }
}
