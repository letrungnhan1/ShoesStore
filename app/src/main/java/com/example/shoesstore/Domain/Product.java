package com.example.shoesstore.Domain;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String id;
    private String title;
    private double price;
    private double oldPrice;
    private String description;
    private String categoryId;
    private List<String> sizes;
    private String picUrl;
    private boolean isHot;

    // Constructor
    public Product(String id, String title, double price, double oldPrice, String description, String categoryId, List<String> sizes, String picUrl, boolean isHot) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.oldPrice = oldPrice;
        this.description = description;
        this.categoryId = categoryId;
        this.sizes = sizes;
        this.picUrl = picUrl;
        this.isHot = isHot;
    }

    public Product() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }
}

