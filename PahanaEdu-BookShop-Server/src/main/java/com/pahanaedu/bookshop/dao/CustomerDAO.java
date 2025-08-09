package com.pahanaedu.bookshop.dao;

import com.pahanaedu.bookshop.model.Customer;
import com.pahanaedu.bookshop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

     public List<Customer> getAllCustomers() throws SQLException {
         List<Customer> customers = new ArrayList<>();
         String sql = "SELECT * FROM customers";
         try (Connection conn = DBConnection.getInstance().getConnection();
              Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery(sql)) {

             while (rs.next()) {
                 customers.add(mapResultSetToCustomer(rs));
             }
         }
         return customers;
     }
     
     public Customer getCustomerById(int customerId) throws SQLException {
         String sql = "SELECT * FROM customers WHERE customer_id = ?";
         try (Connection conn = DBConnection.getInstance().getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
             pstmt.setInt(1, customerId);
             try (ResultSet rs = pstmt.executeQuery()) {
                 if (rs.next()) {
                     return mapResultSetToCustomer(rs);
                 }
             }
         }
         return null;
     }

    public Customer addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (account_number, name, address, telephone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getAccountNumber());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getTelephone());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                    return customer; // Return the customer with the new ID
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
    }

    public Customer updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET name = ?, address = ?, telephone = ? WHERE customer_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getAddress());
            pstmt.setString(3, customer.getTelephone());
            pstmt.setInt(4, customer.getCustomerId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return customer; // Return the updated customer object
            } else {
                return null; 
            }
        }
    }

    public boolean deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setAccountNumber(rs.getString("account_number"));
        customer.setName(rs.getString("name"));
        customer.setAddress(rs.getString("address"));
        customer.setTelephone(rs.getString("telephone"));
        customer.setUnitsConsumed(rs.getBigDecimal("units_consumed"));
        customer.setCreatedAt(rs.getTimestamp("created_at"));
        customer.setUpdatedAt(rs.getTimestamp("updated_at"));
        return customer;
    }
}