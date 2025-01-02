package com.example.travewhere.models;

import java.util.List;

public class Cart {
    private List<String> roomList;
    private Coupon coupon;

    public Cart() {}

    public Cart(List<String> roomList, Coupon coupon) {
        this.roomList = roomList;
        this.coupon = coupon;
    }

    public List<String> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<String> roomList) {
        this.roomList = roomList;
    }

    public Coupon getCoupon() {
        return coupon;
    }
}
