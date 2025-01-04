package com.example.travewhere.models;

public class Room {
    private String id;
    private String roomType;
    private Double pricePerNight;
    private String hotelId;
    private boolean isAvailable;

    public Room() {
    }

    public Room(String id, String roomType, Double pricePerNight, String hotelId) {
        this.id = id;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.hotelId = hotelId;
        this.isAvailable = true;
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

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
