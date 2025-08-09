package com.pahanaedu.bookshop.dao;

import com.pahanaedu.bookshop.dto.DailySalesDTO;
import com.pahanaedu.bookshop.dto.StockItemDTO;
import com.pahanaedu.bookshop.model.report.SalesSummaryDTO;
import com.pahanaedu.bookshop.model.report.TopCustomerDTO;
import com.pahanaedu.bookshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public SalesSummaryDTO getSalesSummary(Date startDate, Date endDate) throws SQLException {
        SalesSummaryDTO summary = new SalesSummaryDTO();
        String summarySql = "SELECT SUM(net_amount) AS totalRevenue, COUNT(invoice_id) AS totalInvoices "
                + "FROM invoices WHERE invoice_date BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(summarySql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    summary.setTotalRevenue(rs.getBigDecimal("totalRevenue"));
                    summary.setTotalInvoices(rs.getLong("totalInvoices"));
                }
            }
        }
        summary.setBestSellingItemName(getBestSellingItemName(startDate, endDate));
        summary.setSalesOverTime(getDailySales(startDate, endDate));
        return summary;
    }

    private String getBestSellingItemName(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT p.name FROM invoice_items ii "
                + "JOIN products p ON ii.product_id = p.product_id "
                + "JOIN invoices i ON ii.invoice_id = i.invoice_id "
                + "WHERE i.invoice_date BETWEEN ? AND ?"
                + "GROUP BY p.name ORDER BY SUM(ii.quantity) DESC LIMIT 1";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("name") : "N/A";
            }
        }
    }

    public List<TopCustomerDTO> getTopCustomers(Date startDate, Date endDate) throws SQLException {
        List<TopCustomerDTO> topCustomers = new ArrayList<>();
        String sql = "SELECT c.account_number, c.name, SUM(i.net_amount) as totalSpent, COUNT(i.invoice_id) as totalInvoices "
                + "FROM invoices i "
                + "JOIN customers c ON i.customer_id = c.customer_id "
                + "WHERE i.invoice_date BETWEEN ? AND ?"
                + "GROUP BY c.customer_id, c.name "
                + "ORDER BY totalSpent DESC LIMIT 10"; // Get top 10 customers

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TopCustomerDTO dto = new TopCustomerDTO();
                    dto.setAccountNumber(rs.getString("account_number"));
                    dto.setName(rs.getString("name"));
                    dto.setTotalSpent(rs.getBigDecimal("totalSpent"));
                    dto.setTotalInvoices(rs.getLong("totalInvoices"));
                    topCustomers.add(dto);
                }
            }
        }
        return topCustomers;
    }

    public List<StockItemDTO> getStockSummary() throws SQLException {
        List<StockItemDTO> stockList = new ArrayList<>();
        String sql = "SELECT product_id, name, stock_quantity FROM products ORDER BY name";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                StockItemDTO item = new StockItemDTO();
                item.setItemId(rs.getString("product_id"));
                item.setName(rs.getString("name"));
                item.setStock(rs.getInt("stock_quantity"));
                stockList.add(item);
            }
        }

        return stockList;
    }

    private List<DailySalesDTO> getDailySales(Date startDate, Date endDate) throws SQLException {
        List<DailySalesDTO> dailySales = new ArrayList<>();
        String sql = "SELECT invoice_date, SUM(net_amount) AS revenue "
                + "FROM invoices "
                + "WHERE invoice_date BETWEEN ? AND ?"
                + "GROUP BY invoice_date "
                + "ORDER BY invoice_date ASC";

        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DailySalesDTO dto = new DailySalesDTO();
                    dto.setDate(rs.getDate("invoice_date"));
                    dto.setRevenue(rs.getBigDecimal("revenue"));
                    dailySales.add(dto);
                }
            }
        }
        return dailySales;
    }

}
