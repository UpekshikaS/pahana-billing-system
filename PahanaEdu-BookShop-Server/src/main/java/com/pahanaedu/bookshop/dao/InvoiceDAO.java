package com.pahanaedu.bookshop.dao;

import com.pahanaedu.bookshop.dto.InvoiceItemDetailsDTO;
import com.pahanaedu.bookshop.model.Invoice;
import com.pahanaedu.bookshop.model.InvoiceItem;
import com.pahanaedu.bookshop.util.DBConnection;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;

public class InvoiceDAO {

    public int createInvoice(Invoice invoice) throws SQLException {
    Connection conn = null;
    int generatedInvoiceId = -1;

    String insertInvoiceSQL = "INSERT INTO invoices (invoice_number, customer_id, user_id, total_amount, discount_amount, net_amount, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    String insertInvoiceItemSQL = "INSERT INTO invoice_items (invoice_id, product_id, quantity, unit_price, item_total) VALUES (?, ?, ?, ?, ?)";
    String updateStockSQL = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?";
    String updateUnitsConsumedSQL = "UPDATE customers SET units_consumed = units_consumed + ? WHERE customer_id = ?"; // NEW

    try {
        conn = DBConnection.getInstance().getConnection();
        conn.setAutoCommit(false);

        // 1. Insert Invoice and get generated key
        try (PreparedStatement pstmtInvoice = conn.prepareStatement(insertInvoiceSQL, Statement.RETURN_GENERATED_KEYS)) {
            // ... (existing code to set invoice parameters) ...
            pstmtInvoice.setString(1, invoice.getInvoiceNumber());
            if (invoice.getCustomerId() != null) {
                pstmtInvoice.setInt(2, invoice.getCustomerId());
            } else {
                pstmtInvoice.setNull(2, java.sql.Types.INTEGER);
            }
            pstmtInvoice.setInt(3, invoice.getUserId());
            pstmtInvoice.setBigDecimal(4, invoice.getTotalAmount());
            pstmtInvoice.setBigDecimal(5, invoice.getDiscountAmount());
            pstmtInvoice.setBigDecimal(6, invoice.getNetAmount());
            pstmtInvoice.setString(7, invoice.getPaymentStatus());
            pstmtInvoice.executeUpdate();

            try (ResultSet generatedKeys = pstmtInvoice.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedInvoiceId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating invoice failed, no ID obtained.");
                }
            }
        }

        // 2. Insert Invoice Items and update product stock
        try (PreparedStatement pstmtItem = conn.prepareStatement(insertInvoiceItemSQL);
             PreparedStatement pstmtStock = conn.prepareStatement(updateStockSQL)) {

            for (InvoiceItem item : invoice.getInvoiceItems()) {
                pstmtStock.setInt(1, item.getQuantity());
                pstmtStock.setInt(2, item.getProductId());
                pstmtStock.setInt(3, item.getQuantity());
                int rowsAffected = pstmtStock.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Insufficient stock for product ID: " + item.getProductId());
                }

                pstmtItem.setInt(1, generatedInvoiceId);
                pstmtItem.setInt(2, item.getProductId());
                pstmtItem.setInt(3, item.getQuantity());
                pstmtItem.setBigDecimal(4, item.getUnitPrice());
                pstmtItem.setBigDecimal(5, item.getItemTotal());
                pstmtItem.addBatch();
            }
            pstmtItem.executeBatch();
        }

        // 3. Update units consumed for the customer (NEW LOGIC)
        if (invoice.getCustomerId() != null) {
            int totalUnitsConsumed = 0;
            for (InvoiceItem item : invoice.getInvoiceItems()) {
                totalUnitsConsumed += item.getQuantity();
            }

            try (PreparedStatement pstmtUnits = conn.prepareStatement(updateUnitsConsumedSQL)) {
                pstmtUnits.setInt(1, totalUnitsConsumed);
                pstmtUnits.setInt(2, invoice.getCustomerId());
                pstmtUnits.executeUpdate();
            }
        }

        // 4. Commit all changes if everything was successful
        conn.commit();

    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        throw e;
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    return generatedInvoiceId;
}

    public List<InvoiceItemDetailsDTO> findInvoiceItemsByInvoiceNumber(String invoiceNumber) throws SQLException {
        List<InvoiceItemDetailsDTO> items = new ArrayList<>();
        String sql = "SELECT ii.invoice_item_id, i.invoice_number, p.name as product_name, ii.quantity, ii.unit_price "
                + "FROM invoice_items ii "
                + "JOIN invoices i ON ii.invoice_id = i.invoice_id "
                + "JOIN products p ON ii.product_id = p.product_id "
                + "WHERE i.invoice_number = ?";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, invoiceNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    InvoiceItemDetailsDTO dto = new InvoiceItemDetailsDTO();
                    dto.setInvoiceItemId(rs.getInt("invoice_item_id"));
                    dto.setInvoiceNumber(rs.getString("invoice_number"));
                    dto.setProductName(rs.getString("product_name"));
                    dto.setQuantityPurchased(rs.getInt("quantity"));
                    dto.setUnitPrice(rs.getBigDecimal("unit_price"));
                    items.add(dto);
                }
            }
        }
        return items;
    }

    public List<Invoice> getAllInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT invoice_id, invoice_number, customer_id, user_id, invoice_date, total_amount, discount_amount, net_amount, payment_status FROM invoices ORDER BY invoice_date DESC";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setInvoiceNumber(rs.getString("invoice_number"));
                invoice.setCustomerId(rs.getInt("customer_id"));
                invoice.setUserId(rs.getInt("user_id"));
                invoice.setInvoiceDate(rs.getTimestamp("invoice_date"));
                invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
                invoice.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                invoice.setNetAmount(rs.getBigDecimal("net_amount"));
                invoice.setPaymentStatus(rs.getString("payment_status"));
                invoices.add(invoice);
            }
        }
        return invoices;
    }

}
