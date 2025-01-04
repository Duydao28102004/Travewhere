package com.example.travewhere.models;

public class Coupon {
    private String id;
    private String code;
    private double discount;

    public Coupon() {}

    public Coupon(String id, String code, double discount) {
        this.id = id;
        this.code = code;
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public double getDiscount() {
        return discount;
    }
}
