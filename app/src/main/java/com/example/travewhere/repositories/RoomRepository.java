package com.example.travewhere.repositories;

import com.example.travewhere.models.Room;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RoomRepository {
    private final DatabaseReference databaseReference;

    public RoomRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference("rooms");
    }

    // CREATE: Add a new room
    public Task<Void> addRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            String uniqueId = databaseReference.push().getKey();
            room.setId(uniqueId);
        }
        return databaseReference.child(room.getId()).setValue(room);
    }

    // READ: Get all rooms for a specific hotel
    public DatabaseReference getRoomsByHotel(String hotelId) {
        return databaseReference.orderByChild("hotelId").equalTo(hotelId).getRef();
    }

    // UPDATE: Update room details
    public Task<Void> updateRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be null or empty");
        }
        return databaseReference.child(room.getId()).setValue(room);
    }

    // DELETE: Delete a room by ID
    public Task<Void> deleteRoom(String roomId) {
        if (roomId == null || roomId.isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be null or empty");
        }
        return databaseReference.child(roomId).removeValue();
    }
}
