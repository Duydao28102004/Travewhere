package com.example.travewhere.models;

public class Coupon {
    private String id;
    private String code;
    private double discount;
    private double minSpend;

    public Coupon() {}

    public Coupon(String id, String code, double discount, double minSpend) {
        this.id = id;
        this.code = code;
        this.discount = discount;
        this.minSpend = minSpend;
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

    public double getMinSpend() {
        return minSpend;
    }

    public void setMinSpend(double minSpend) {
        this.minSpend = minSpend;
    }
}
