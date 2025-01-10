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
            return new MemberStatus("Diamond Member", R.drawable.ic_diamond_user, "As a Diamond Member, you will receive exclusive 10% discount on all bookings domestically", "1 Special gift + 1 3D-2N trip to Saigon on your birthday month", "When travelling to Europe/America, you will receive 3 free days of trip");
        } else if (point >= 500) {
            return new MemberStatus("Gold Member", R.drawable.ic_gold_user, "As a Gold Member, you will receive exclusive 10% discount on 5 bookings domestically", "1 Special gift on your birthday month", "When travelling to Vietnam's provinces, you will receive 3 free days of trip");
        } else {
            return new MemberStatus("Bronze Member", R.drawable.ic_bronze_user, "As a Bronze Member, you will receive exclusive 5% discount on 1 booking domestically", "1 Free day of trip to RMIT Saigon South Campus", "No special offer available");
        }
    }
}
