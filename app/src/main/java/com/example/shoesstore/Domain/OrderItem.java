package com.example.shoesstore.Domain;

public class OrderItem {
    private String orderId;
    private String productId;
    private double productPrice;
    private int quantity;
    private String size;

    // Constructor
    public OrderItem(String orderId, String productId, double productPrice, int quantity, String size) {
        this.orderId = orderId;
        this.productId = productId;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.size = size;
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
