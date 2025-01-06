package com.example.travewhere.models;

import com.google.firebase.Timestamp;

public class Review {
    private String id;
    private String hotelId;
    private String userId;
    private String content;
    private float rating;
    private Timestamp timestamp;

    public Review() {
    }

    public Review(String id, String hotelId, String userId, String content, float rating, Timestamp timestamp) {
        this.id = id;
        this.hotelId = hotelId;
        this.userId = userId;
        this.content = content;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
