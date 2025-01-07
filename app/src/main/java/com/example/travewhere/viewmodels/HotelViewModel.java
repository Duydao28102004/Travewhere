package com.example.travewhere.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travewhere.models.Hotel;
import com.example.travewhere.repositories.HotelRepository;

import java.util.ArrayList;
import java.util.List;

public class HotelViewModel extends ViewModel {
    private final HotelRepository hotelRepository;
    private final MutableLiveData<List<Hotel>> hotelListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Hotel> hotelLiveData = new MutableLiveData<>();
    private List<Hotel> hotels = new ArrayList<>();
    public HotelViewModel() {
        hotelRepository = new HotelRepository();
    }

    public LiveData<List<Hotel>> getAllHotels() {
        hotelRepository.getAllHotels().addOnSuccessListener(hotels -> {
            hotelListLiveData.postValue(hotels);
        }).addOnFailureListener(e -> {
            // Handle error
        });
        return hotelListLiveData;
    }

    public List<Hotel> getHotelsList() {
        return hotels;
    }

    public LiveData<Hotel> getHotelById(String hotelId) {
        hotelRepository.getHotelById(hotelId).addOnSuccessListener(hotel -> {
            hotelLiveData.postValue(hotel);
        }).addOnFailureListener(e -> {
            Log.d("HotelViewModel", "Error getting hotel by ID: " + e.getMessage());
        });
        return hotelLiveData;
    }

    public void updateHotelImage(String hotelId, String imageUrl) {
        getHotelById(hotelId).observeForever(hotel -> {
            if (hotel != null) {
                hotel.setImageUrl(imageUrl);
                updateHotel(hotel);
            }
        });
    }

    public String getUID() {
        return hotelRepository.getUID();
    }

    public void addHotel(Hotel hotel) {
        hotelRepository.addHotel(hotel);
    }

    public void updateHotel(Hotel hotel) {
        hotelRepository.updateHotel(hotel);
    }

    public void deleteHotel(String hotelId) {
        hotelRepository.deleteHotel(hotelId);
    }
}
