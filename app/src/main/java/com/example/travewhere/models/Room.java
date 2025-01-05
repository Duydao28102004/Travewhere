package com.example.travewhere.models;

public class Room {
    private String id;
    private String roomType;
    private Double pricePerNight;
    private String hotelId;
    private int capacity;

    public Room() {
    }

    public Room(String id, String roomType, Double pricePerNight, String hotelId, int capacity) {
        this.id = id;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.hotelId = hotelId;
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
