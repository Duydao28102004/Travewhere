package com.example.travewhere.repositories;

import android.util.Log;

import com.example.travewhere.models.Room;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class RoomRepository {
    private final CollectionReference roomsCollection;
    private static final String TAG = "RoomRepository";

    public RoomRepository() {
        roomsCollection = FirebaseFirestore.getInstance().collection("rooms");
        Log.d(TAG, "RoomRepository initialized with Firestore");
    }

    public String getUID() {
        return roomsCollection.document().getId();
    }

    // CREATE: Add a new room
    public Task<Void> addRoom(Room room) {
        Log.d(TAG, "addRoom() called");
        if (room.getId() == null || room.getId().isEmpty()) {
            String uniqueId = roomsCollection.document().getId(); // Generate unique ID for the room
            room.setId(uniqueId);
            Log.d(TAG, "Generated unique ID for room: " + uniqueId);
        }
        return roomsCollection.document(room.getId()).set(room)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Room added successfully: " + room.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add room: " + e.getMessage()));
    }

    // READ: Get all rooms for a specific hotel
    public Query getRoomsByHotel(String hotelId) {
        Log.d(TAG, "getRoomsByHotel() called for hotelId: " + hotelId);
        return roomsCollection.whereEqualTo("hotelId", hotelId);
    }

    // UPDATE: Update room details
    public Task<Void> updateRoom(Room room) {
        Log.d(TAG, "updateRoom() called for ID: " + room.getId());
        if (room.getId() == null || room.getId().isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be null or empty");
        }
        return roomsCollection.document(room.getId()).set(room)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Room updated successfully: " + room.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update room: " + e.getMessage()));
    }

    // READ: Get all rooms
    public Task<List<Room>> getAllRooms() {
        Log.d(TAG, "getAllRooms() called");
        return roomsCollection.get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Room> roomList = task.getResult().toObjects(Room.class);
                        Log.d(TAG, "Fetched " + roomList.size() + " rooms from Firestore");
                        return roomList;
                    } else {
                        Log.e(TAG, "Failed to fetch rooms: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                        return null;
                    }
                });
    }


    // DELETE: Delete a room by ID
    public Task<Void> deleteRoom(String roomId) {
        Log.d(TAG, "deleteRoom() called for ID: " + roomId);
        if (roomId == null || roomId.isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be null or empty");
        }
        return roomsCollection.document(roomId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Room deleted successfully: " + roomId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete room: " + e.getMessage()));
    }
}
