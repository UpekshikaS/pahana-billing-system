package com.pahanaedu.bookshop.model;

import java.math.BigDecimal;

public class Payment {

    private String paymentMethod;
    private BigDecimal amountPaid;

    public Payment() {
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }
}
