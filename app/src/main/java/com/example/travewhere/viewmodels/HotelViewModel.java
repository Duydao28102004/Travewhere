package com.example.travewhere.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travewhere.models.Hotel;
import com.example.travewhere.repositories.HotelRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HotelViewModel extends ViewModel {
    private final HotelRepository hotelRepository;
    private final MutableLiveData<List<Hotel>> hotelListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Hotel> hotelLiveData = new MutableLiveData<>();

    public HotelViewModel() {
        hotelRepository = new HotelRepository();
    }

    // Fetch a list of all hotels
    public LiveData<List<Hotel>> getAllHotels() {
        loadAllHotels();
        return hotelListLiveData;
    }

    private void loadAllHotels() {
        hotelRepository.getAllHotels().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Hotel> hotels = new ArrayList<>();
                for (DataSnapshot hotelSnapshot : snapshot.getChildren()) {
                    Hotel hotel = hotelSnapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        hotels.add(hotel);
                    }
                }
                hotelListLiveData.postValue(hotels);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle database error
            }
        });
    }

    // Fetch a specific hotel by ID
    public LiveData<Hotel> getHotelById(String hotelId) {
        loadHotelById(hotelId);
        return hotelLiveData;
    }

    private void loadHotelById(String hotelId) {
        hotelRepository.getHotelById(hotelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Hotel hotel = snapshot.getValue(Hotel.class);
                hotelLiveData.postValue(hotel);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle database error
            }
        });
    }

    // Add a hotel
    public void addHotel(Hotel hotel) {
        hotelRepository.addHotel(hotel);
    }

    // Update a hotel
    public void updateHotel(Hotel hotel) {
        hotelRepository.updateHotel(hotel);
    }

    // Delete a hotel
    public void deleteHotel(String hotelId) {
        hotelRepository.deleteHotel(hotelId);
    }
}
