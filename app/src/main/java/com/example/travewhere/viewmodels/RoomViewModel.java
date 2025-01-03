package com.example.travewhere.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travewhere.models.Room;
import com.example.travewhere.repositories.RoomRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoomViewModel extends ViewModel {
    private final RoomRepository roomRepository;
    private final MutableLiveData<List<Room>> roomListLiveData = new MutableLiveData<>();
    private List<Room> roomList = new ArrayList<>();

    public RoomViewModel() {
        roomRepository = new RoomRepository();
    }

    public LiveData<List<Room>> getRoomList(String hotelId) {
        loadRooms(hotelId); // Load rooms whenever this method is called
        return roomListLiveData;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    private void loadRooms(String hotelId) {
        roomRepository.getRoomsByHotel(hotelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Room> rooms = new ArrayList<>();
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);
                    if (room != null) {
                        rooms.add(room);
                    }
                }
                roomListLiveData.postValue(rooms);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle database error
            }
        });
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
