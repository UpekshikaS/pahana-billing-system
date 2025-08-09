package com.pahanaedu.bookshop.dao;

import com.pahanaedu.bookshop.model.Return;
import com.pahanaedu.bookshop.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReturnDAO {

    public String processReturn(Return aReturn) throws SQLException {
        Connection conn = null;
        String notificationMessage = null;

        String insertReturnSql = "INSERT INTO returns (invoice_item_id, quantity_returned, reason, processed_by_user_id) VALUES (?, ?, ?, ?)";
        String updateStockSql = "UPDATE products p "
                + "JOIN invoice_items ii ON p.product_id = ii.product_id "
                + "SET p.stock_quantity = p.stock_quantity + ? "
                + "WHERE ii.invoice_item_id = ?";
        String checkQtySql = "SELECT ii.quantity AS quantity_purchased, "
                + "IFNULL(SUM(r.quantity_returned), 0) AS total_returned "
                + "FROM invoice_items ii "
                + "LEFT JOIN returns r ON ii.invoice_item_id = r.invoice_item_id "
                + "WHERE ii.invoice_item_id = ? "
                + "GROUP BY ii.invoice_item_id, ii.quantity";

        String getNotificationDetailsSql = "SELECT ii.product_id, i.invoice_number, i.user_id FROM invoice_items ii "
                + "JOIN invoices i ON ii.invoice_id = i.invoice_id "
                + "WHERE ii.invoice_item_id = ?";

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            int purchasedQty = 0;
            int totalReturnedQty = 0;

            try (PreparedStatement pstmtCheck = conn.prepareStatement(checkQtySql)) {
                pstmtCheck.setInt(1, aReturn.getInvoiceItemId());
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        purchasedQty = rs.getInt("quantity_purchased");
                        totalReturnedQty = rs.getInt("total_returned");
                    } else {
                        throw new SQLException("Invoice item not found.");
                    }
                }
            }

            int remainingQty = purchasedQty - totalReturnedQty;
            if (aReturn.getQuantityReturned() <= 0 || aReturn.getQuantityReturned() > remainingQty) {
                throw new IllegalArgumentException("Invalid return quantity. Cannot exceed remaining returnable quantity.");
            }

            try (PreparedStatement pstmtStock = conn.prepareStatement(updateStockSql)) {
                pstmtStock.setInt(1, aReturn.getQuantityReturned());
                pstmtStock.setInt(2, aReturn.getInvoiceItemId());
                if (pstmtStock.executeUpdate() == 0) {
                    throw new SQLException("Failed to update stock. Invoice item or product may not exist.");
                }
            }

            try (PreparedStatement pstmtReturn = conn.prepareStatement(insertReturnSql)) {
                pstmtReturn.setInt(1, aReturn.getInvoiceItemId());
                pstmtReturn.setInt(2, aReturn.getQuantityReturned());
                pstmtReturn.setString(3, aReturn.getReason());
                pstmtReturn.setInt(4, aReturn.getProcessedByUserId());
                pstmtReturn.executeUpdate();
            }

            try (PreparedStatement pstmtDetails = conn.prepareStatement(getNotificationDetailsSql)) {
                pstmtDetails.setInt(1, aReturn.getInvoiceItemId());
                try (ResultSet rs = pstmtDetails.executeQuery()) {
                    if (rs.next()) {
                        String productID = rs.getString("product_id");
                        String invoiceNumber = rs.getString("invoice_number");
                        int UserID = rs.getInt("user_id");

                        notificationMessage = String.format(
                                "Returned item '%s' (Qty: %d) from invoice '%s' has been processed. The amount will be credited to your account.",
                                productID,
                                aReturn.getQuantityReturned(),
                                invoiceNumber
                        );
                        aReturn.setProcessedByUserId(UserID);
                    }
                }
            }

            conn.commit();
            return notificationMessage;

        } catch (SQLException | IllegalArgumentException e) {
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
