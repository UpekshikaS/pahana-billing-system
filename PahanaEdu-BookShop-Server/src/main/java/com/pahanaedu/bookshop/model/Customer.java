package com.pahanaedu.bookshop.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Customer {

    private int customerId;
    private String accountNumber;
    private String name;
    private String address;
    private String telephone;
    private BigDecimal unitsConsumed;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Customer() {
    }

    // Add this constructor for your tests
    public Customer(int customerId, String name, String telephone) {
        this.customerId = customerId;
        this.name = name;
        this.telephone = telephone;
    }

    // Getters and setters...
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public BigDecimal getUnitsConsumed() {
        return unitsConsumed;
    }

    public void setUnitsConsumed(BigDecimal unitsConsumed) {
        this.unitsConsumed = unitsConsumed;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
