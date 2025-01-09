package com.example.travewhere.models;

import com.example.travewhere.R;

import java.io.Serializable;

public class Customer extends User implements Serializable {
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

    // Return the member status based on the point
    public MemberStatus getMemberStatus() {
        if (point >= 1000) {
            return new MemberStatus("Diamond Member", R.drawable.ic_diamond_user);
        } else if (point >= 500) {
            return new MemberStatus("Gold Member", R.drawable.ic_gold_user);
        } else {
            return new MemberStatus("Bronze Member", R.drawable.ic_bronze_user);
        }
    }
}
