package com.example.travewhere.models;

public class Customer extends User {
    private Cart cart;
    private int point;

    public Customer() {}

    public Customer(String id, String name, String email, String phoneNumber) {
        super(id, name, email, phoneNumber);
        this.cart = null;
        this.point = 0;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
