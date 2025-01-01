package com.example.travewhere.models;

import java.util.List;

public class Cart {
    private List<Room> roomList;
    private Coupon coupon;

    public Cart() {}

    public Cart(List<Room> roomList, Coupon coupon) {
        this.roomList = roomList;
        this.coupon = coupon;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    public Coupon getCoupon() {
        return coupon;
    }
}
