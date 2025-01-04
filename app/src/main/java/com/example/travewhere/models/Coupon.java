package com.example.travewhere.models;

public class Coupon {
    private String code;
    private double discount;

    public Coupon() {}

    public Coupon(String code, double discount) {
        this.code = code;
        this.discount = discount;
    }

    public String getCode() {
        return code;
    }

    public double getDiscount() {
        return discount;
    }
}
