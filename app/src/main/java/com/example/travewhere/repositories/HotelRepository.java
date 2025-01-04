package com.example.travewhere.repositories;

import android.util.Log;

import com.example.travewhere.models.Hotel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HotelRepository {
    private final CollectionReference hotelsCollection;
    private static final String TAG = "HotelRepository";

    public HotelRepository() {
        hotelsCollection = FirebaseFirestore.getInstance().collection("hotels");
        Log.d(TAG, "HotelRepository initialized with Firestore");
    }

    public String getUID() {
        return hotelsCollection.document().getId();
    }

    // CREATE: Add a new hotel
    public Task<Void> addHotel(Hotel hotel) {
        Log.d(TAG, "addHotel() called");
        if (hotel.getId() == null || hotel.getId().isEmpty()) {
            String uniqueId = hotelsCollection.document().getId(); // Generate a unique ID
            hotel.setId(uniqueId);
            Log.d(TAG, "Generated unique ID for hotel: " + uniqueId);
        }
        return hotelsCollection.document(hotel.getId()).set(hotel)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Hotel added successfully: " + hotel.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add hotel: " + e.getMessage()));
    }

    // READ: Fetch a specific hotel by ID
    public Task<Hotel> getHotelById(String hotelId) {
        Log.d(TAG, "getHotelById() called with ID: " + hotelId);
        if (hotelId == null || hotelId.isEmpty()) {
            throw new IllegalArgumentException("Hotel ID cannot be null or empty");
        }
        return hotelsCollection.document(hotelId).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return task.getResult().toObject(Hotel.class);
                    } else {
                        throw new Exception("Hotel not found: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    // READ: Fetch all hotels
    public Task<List<Hotel>> getAllHotels() {
        Log.d(TAG, "getAllHotels() called");
        return hotelsCollection.get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Hotel> hotels = new ArrayList<>();
                        task.getResult().forEach(document -> {
                            Hotel hotel = document.toObject(Hotel.class);
                            hotels.add(hotel);
                        });
                        return hotels;
                    } else {
                        throw new Exception("Failed to fetch hotels: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    // UPDATE: Update an existing hotel
    public Task<Void> updateHotel(Hotel hotel) {
        Log.d(TAG, "updateHotel() called for ID: " + hotel.getId());
        if (hotel.getId() == null || hotel.getId().isEmpty()) {
            throw new IllegalArgumentException("Hotel ID cannot be null or empty");
        }
        return hotelsCollection.document(hotel.getId()).set(hotel)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Hotel updated successfully: " + hotel.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update hotel: " + e.getMessage()));
    }

    // DELETE: Remove a hotel by ID
    public Task<Void> deleteHotel(String hotelId) {
        Log.d(TAG, "deleteHotel() called for ID: " + hotelId);
        if (hotelId == null || hotelId.isEmpty()) {
            throw new IllegalArgumentException("Hotel ID cannot be null or empty");
        }
        return hotelsCollection.document(hotelId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Hotel deleted successfully: " + hotelId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete hotel: " + e.getMessage()));
    }
}
