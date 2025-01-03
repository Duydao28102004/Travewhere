package com.example.travewhere.repositories;

import com.example.travewhere.models.Hotel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HotelRepository {
    private final DatabaseReference databaseReference;

    public HotelRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference("hotels");
    }

    // CREATE: Add a new hotel
    public Task<Void> addHotel(Hotel hotel) {
        if (hotel.getId() == null || hotel.getId().isEmpty()) {
            String uniqueId = databaseReference.push().getKey();
            hotel.setId(uniqueId);
        }
        return databaseReference.child(hotel.getId()).setValue(hotel);
    }

    // READ: Fetch a specific hotel by ID
    public DatabaseReference getHotelById(String hotelId) {
        if (hotelId == null || hotelId.isEmpty()) {
            throw new IllegalArgumentException("Hotel ID cannot be null or empty");
        }
        return databaseReference.child(hotelId);
    }

    // READ: Fetch all hotels
    public DatabaseReference getAllHotels() {
        return databaseReference;
    }

    // UPDATE: Update an existing hotel
    public Task<Void> updateHotel(Hotel hotel) {
        if (hotel.getId() == null || hotel.getId().isEmpty()) {
            throw new IllegalArgumentException("Hotel ID cannot be null or empty");
        }
        return databaseReference.child(hotel.getId()).setValue(hotel);
    }

    // DELETE: Remove a hotel by ID
    public Task<Void> deleteHotel(String hotelId) {
        if (hotelId == null || hotelId.isEmpty()) {
            throw new IllegalArgumentException("Hotel ID cannot be null or empty");
        }
        return databaseReference.child(hotelId).removeValue();
    }
}
