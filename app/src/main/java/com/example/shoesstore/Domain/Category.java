package com.example.shoesstore.Domain;

public class Category {
    private String id;
    private String title;
    private String picUrl;

    // Constructor mới với ba tham số
    public Category(String id, String title, String picUrl) {
        this.id = id;
        this.title = title;
        this.picUrl = picUrl;
    }

    // Constructor cũ với hai tham số
    public Category(String title, String picUrl) {
        this.title = title;
        this.picUrl = picUrl;
    }

    // Constructor mặc định
    public Category() {
    }

    // Getter và Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

