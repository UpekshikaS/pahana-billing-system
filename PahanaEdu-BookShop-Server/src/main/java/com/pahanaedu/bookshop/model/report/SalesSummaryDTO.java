package com.pahanaedu.bookshop.model.report;

import com.pahanaedu.bookshop.dto.DailySalesDTO;
import java.math.BigDecimal;
import java.util.List;

public class SalesSummaryDTO {

    private BigDecimal totalRevenue;
    private long totalInvoices;
    private String bestSellingItemName;
    private List<DailySalesDTO> salesOverTime;

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(long totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public String getBestSellingItemName() {
        return bestSellingItemName;
    }

    public void setBestSellingItemName(String bestSellingItemName) {
        this.bestSellingItemName = bestSellingItemName;
    }

    public List<DailySalesDTO> getSalesOverTime() {
        return salesOverTime;
    }

    public void setSalesOverTime(List<DailySalesDTO> salesOverTime) {
        this.salesOverTime = salesOverTime;
    }

}
