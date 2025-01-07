package com.example.travewhere.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travewhere.models.Room;
import com.example.travewhere.repositories.RoomRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomViewModel extends ViewModel {
    private final RoomRepository roomRepository;
    private final MutableLiveData<List<Room>> roomListLiveData = new MutableLiveData<>();
    private List<Room> roomList = new ArrayList<>();

    public RoomViewModel() {
        roomRepository = new RoomRepository();
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public LiveData<List<Room>> getRoomsByHotel(String hotelId) {
        roomRepository.getRoomsByHotel(hotelId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Room> rooms = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) { // Explicit type instead of var
                        Room room = document.toObject(Room.class); // Ensure Room class is properly defined
                        if (room != null) { // Check for null before adding to the list
                            rooms.add(room);
                        }
                    }
                    roomListLiveData.postValue(rooms); // Update LiveData
                })
                .addOnFailureListener(e -> {
                    Log.e("RoomViewModel", "Failed to fetch rooms: " + e.getMessage());
                    // Optionally handle error (e.g., notify UI)
                });
        return roomListLiveData;
    }

    public LiveData<List<Room>> getAllRooms() {
        MutableLiveData<List<Room>> roomListLiveData = new MutableLiveData<>();
        roomRepository.getAllRooms().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                roomListLiveData.postValue(task.getResult());
            } else {
                Log.e("RoomViewModel", "Failed to fetch rooms: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
        return roomListLiveData;
    }


    public String getUID() {
        return roomRepository.getUID();
    }

    public void addRoom(Room room) {
        roomRepository.addRoom(room);
    }

    public void updateRoom(Room room) {
        roomRepository.updateRoom(room);
    }

    public void deleteRoom(String roomId) {
        roomRepository.deleteRoom(roomId);
    }
}
