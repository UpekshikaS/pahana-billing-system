package com.pahanaedu.bookshop.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class DailySalesDTO {

    private Date date;
    private BigDecimal revenue;

    public DailySalesDTO() {
    }

    public DailySalesDTO(Date date, BigDecimal revenue) {
        this.date = date;
        this.revenue = revenue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

}
