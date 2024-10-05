package com.example.shoesstore.Activity;

public class Coupon {
    private String code;
    private String startDate;
    private String endDate;

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;
    private String discount;

    public Coupon() {}

    public Coupon(String code, String startDate, String endDate, String status, String discount) {
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.discount = discount;
    }

    public String getCode() {
        return code;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public String getDiscount() {
        return discount;
    }
}
