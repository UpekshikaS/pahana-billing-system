package com.pahanaedu.bookshop.dao;

import com.pahanaedu.bookshop.model.Discount;
import com.pahanaedu.bookshop.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountDAO {

    public List<Discount> getAllDiscounts() throws SQLException {
        List<Discount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                discounts.add(mapRow(rs));
            }
        }
        return discounts;
    }

    public Discount getByCode(String code) throws SQLException {
        String sql = "SELECT * FROM discounts WHERE code = ? AND is_active = 1";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public void insertDiscount(Discount discount) throws SQLException {
        String sql = "INSERT INTO discounts (name, code, type, value, min_amount, start_date, end_date, is_active, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, discount.getName());
            stmt.setString(2, discount.getCode());
            stmt.setString(3, discount.getType());
            stmt.setBigDecimal(4, discount.getValue());
            stmt.setBigDecimal(5, discount.getMinAmount());
            stmt.setDate(6, discount.getStartDate());
            stmt.setDate(7, discount.getEndDate());
            stmt.setBoolean(8, discount.isActive());
            stmt.executeUpdate();
        }
    }

    private Discount mapRow(ResultSet rs) throws SQLException {
        Discount d = new Discount();
        d.setDiscountId(rs.getInt("discount_id"));
        d.setName(rs.getString("name"));
        d.setCode(rs.getString("code"));
        d.setType(rs.getString("type"));
        d.setValue(rs.getBigDecimal("value"));
        d.setMinAmount(rs.getBigDecimal("min_amount"));
        d.setStartDate(rs.getDate("start_date"));
        d.setEndDate(rs.getDate("end_date"));
        d.setActive(rs.getBoolean("is_active"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        d.setUpdatedAt(rs.getTimestamp("updated_at"));
        return d;
    }
}
