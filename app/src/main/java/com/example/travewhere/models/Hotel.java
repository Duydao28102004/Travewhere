package com.example.travewhere.models;

import java.util.List;

public class Hotel {
    private String id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String phoneNumber;
    private String email;
    private Manager manager;
    private List<String> roomIdList;

    public Hotel() {
    }

    public Hotel(String id, String name, String address, String phoneNumber, String email, Manager manager, List<String> roomIdList) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.manager = manager;
        this.roomIdList = roomIdList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getRoomList() {
        return roomIdList;
    }

    public void setRoomList(List<String> roomList) {
        this.roomIdList = roomIdList;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
