package com.example.travewhere.models;

import java.util.ArrayList;
import java.util.List;

public class Manager extends User {
    private List<Hotel> hotelList;

    public Manager() {
    }

    public Manager(String id, String name, String email, String phoneNumber) {
        super(id, name, email, phoneNumber);
        this.hotelList = new ArrayList<>();
    }

    public List<Hotel> getHotelList() {
        return hotelList;
    }

    public void setHotelList(List<Hotel> hotelList) {
        this.hotelList = hotelList;
    }
}
