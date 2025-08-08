package com.pahanaedu.bookshop.dao;

import com.pahanaedu.bookshop.model.Product;
import com.pahanaedu.bookshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductDAO {

    public Product getProductByItemId(String itemId) throws SQLException {
        String sql = "SELECT * FROM products WHERE item_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        return null; // Product not found
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = DBConnection.getInstance().getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }

    public void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (item_id, name, description, price, stock_quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getItemId());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getDescription());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setInt(5, product.getStockQuantity());

            pstmt.executeUpdate();
        }
    }

    public void updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock_quantity = ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setBigDecimal(3, product.getPrice());
            pstmt.setInt(4, product.getStockQuantity());
            pstmt.setInt(5, product.getProductId());

            pstmt.executeUpdate();
        }
    }

    public void deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            pstmt.executeUpdate();
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setItemId(rs.getString("item_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setUpdatedAt(rs.getTimestamp("updated_at"));
        return product;
    }

    public void bulkUpsertProducts(List<Product> productsToUpload) throws SQLException {
        Connection conn = null;
        // Get all existing products and map them by their item_id for quick lookup
        Map<String, Product> existingProductsMap = getAllProducts().stream()
                .collect(Collectors.toMap(Product::getItemId, product -> product));

        String insertSql = "INSERT INTO products (item_id, name, description, price, stock_quantity) VALUES (?, ?, ?, ?, ?)";
        String updateSql = "UPDATE products SET name = ?, description = ?, price = ?, stock_quantity = ? WHERE item_id = ?";

        try {
            conn = DBConnection.getInstance().getConnection();
            // --- Start Transaction ---
            conn.setAutoCommit(false);

            try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql); PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {

                for (Product product : productsToUpload) {
                    if (existingProductsMap.containsKey(product.getItemId())) {
                        updatePstmt.setString(1, product.getName());
                        updatePstmt.setString(2, product.getDescription());
                        updatePstmt.setBigDecimal(3, product.getPrice());
                        updatePstmt.setInt(4, product.getStockQuantity());
                        updatePstmt.setString(5, product.getItemId());
                        updatePstmt.addBatch();
                    } else {
                        insertPstmt.setString(1, product.getItemId());
                        insertPstmt.setString(2, product.getName());
                        insertPstmt.setString(3, product.getDescription());
                        insertPstmt.setBigDecimal(4, product.getPrice());
                        insertPstmt.setInt(5, product.getStockQuantity());
                        insertPstmt.addBatch();
                    }
                }
                insertPstmt.executeBatch();
                updatePstmt.executeBatch();
            }
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }

}
