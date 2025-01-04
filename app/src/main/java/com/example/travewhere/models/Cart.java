package com.example.travewhere.models;

import java.util.List;

public class Cart {
    private List<String> roomIdList;
    private Coupon coupon;

    public Cart() {}

    public Cart(List<String> roomList, Coupon coupon) {
        this.roomIdList = roomList;
        this.coupon = coupon;
    }

    public List<String> getRoomList() {
        return roomIdList;
    }

    public void setRoomList(List<String> roomList) {
        this.roomIdList = roomList;
    }

    public Coupon getCoupon() {
        return coupon;
    }
}
