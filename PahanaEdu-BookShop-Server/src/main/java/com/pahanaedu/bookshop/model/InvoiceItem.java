package com.pahanaedu.bookshop.model;

import java.math.BigDecimal;

public class InvoiceItem {
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal itemTotal;

    public InvoiceItem() {}

    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getItemTotal() {
        return itemTotal;
    }
    public void setItemTotal(BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }
}
