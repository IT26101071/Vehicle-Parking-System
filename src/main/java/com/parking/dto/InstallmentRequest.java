package com.parking.dto;

import java.math.BigDecimal;

public class InstallmentRequest {
    private BigDecimal additionalAmount;
    private String paymentMethod;
    private String notes;

    public BigDecimal getAdditionalAmount() {
        return additionalAmount;
    }

    public void setAdditionalAmount(BigDecimal additionalAmount) {
        this.additionalAmount = additionalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
