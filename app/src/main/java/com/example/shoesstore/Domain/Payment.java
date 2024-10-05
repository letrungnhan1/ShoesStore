package com.example.shoesstore.Domain;

public class Payment {
    private String orderId;
    private String idPayment;
    private String paymentMethod;
    private String paymentDate;

    // Constructor
    public Payment(String orderId, String idPayment, String paymentMethod, String paymentDate) {
        this.orderId = orderId;
        this.idPayment = idPayment;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getIdPayment() {
        return idPayment;
    }

    public void setIdPayment(String idPayment) {
        this.idPayment = idPayment;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
}
