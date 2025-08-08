package com.pahanaedu.bookshop.model;

import java.sql.Timestamp;

public class Return {

    private int returnId;
    private int invoiceItemId;
    private int quantityReturned;
    private String reason;
    private int processedByUserId;

    public Return() {
    }

    public int getReturnId() {
        return returnId;
    }

    public void setReturnId(int returnId) {
        this.returnId = returnId;
    }

    public int getInvoiceItemId() {
        return invoiceItemId;
    }

    public void setInvoiceItemId(int invoiceItemId) {
        this.invoiceItemId = invoiceItemId;
    }

    public int getQuantityReturned() {
        return quantityReturned;
    }

    public void setQuantityReturned(int quantityReturned) {
        this.quantityReturned = quantityReturned;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getProcessedByUserId() {
        return processedByUserId;
    }

    public void setProcessedByUserId(int processedByUserId) {
        this.processedByUserId = processedByUserId;
    }
}
