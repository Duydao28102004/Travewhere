package com.example.travewhere.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travewhere.HotelDetailActivity;
import com.example.travewhere.R;
import com.example.travewhere.adapters.HotelAdapter;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.repositories.HotelRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class HomepageFragment extends Fragment implements HotelAdapter.OnHotelClickListener {

    private RecyclerView hotelRecyclerView;
    private HotelAdapter hotelAdapter;
    private List<Hotel> hotelList;
    private HotelRepository hotelRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hotelRepository = new HotelRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        hotelRecyclerView = view.findViewById(R.id.accommodationRecyclerView);

        // Set layout manager for the RecyclerView
        hotelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize the list and adapter
        hotelList = new ArrayList<>();
        hotelAdapter = new HotelAdapter(getContext(), hotelList, this);
        hotelRecyclerView.setAdapter(hotelAdapter);

        // Fetch all hotels and update the RecyclerView
        fetchHotels();
        
        return view;
    }

    private void fetchHotels() {
        hotelRepository.getAllHotels()
                .addOnCompleteListener(new OnCompleteListener<List<Hotel>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Hotel>> task) {
                        if (task.isSuccessful()) {
                            List<Hotel> hotels = task.getResult();
                            if (hotels != null) {
                                hotelList.clear();
                                hotelList.addAll(hotels);
                                hotelAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.e("HomepageFragment", "Error getting hotels: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onHotelClick(String hotelId) {
        Intent intent = new Intent(requireContext(), HotelDetailActivity.class);
        intent.putExtra("HOTEL_ID", hotelId);
        startActivity(intent);
    }
}
