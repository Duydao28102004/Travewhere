package com.example.travewhere.models;

public class Customer extends User {
    private Cart cart;

    public Customer() {}

    public Customer(String id, String name, String email, String phoneNumber) {
        super(id, name, email, phoneNumber);
        this.cart = null;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
