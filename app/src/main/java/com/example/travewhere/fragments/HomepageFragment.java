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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.travewhere.BookingHistoryActivity;
import com.example.travewhere.HotelDetailActivity;
import com.example.travewhere.R;
import com.example.travewhere.SearchActivity;
import com.example.travewhere.adapters.HotelAdapter;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.repositories.HotelRepository;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class HomepageFragment extends Fragment {

    private RecyclerView hotelRecyclerView;
    private HotelAdapter hotelAdapter;
    private List<Hotel> hotelList;
    private HotelViewModel hotelViewModel = new HotelViewModel();
    private LinearLayout searchBar, bookingHistoryButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        hotelRecyclerView = view.findViewById(R.id.accommodationRecyclerView);
        searchBar = view.findViewById(R.id.searchBar);
        bookingHistoryButton = view.findViewById(R.id.bookingLinearLayout);

        // Set layout manager for the RecyclerView
        hotelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize the list and adapter
        hotelList = new ArrayList<>();
        hotelAdapter = new HotelAdapter(this.getContext(), hotelList);
        hotelRecyclerView.setAdapter(hotelAdapter);
        hotelAdapter.setOrientation(true);

        // Trigger search activity
        searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });

        // Trigger booking history activity
        bookingHistoryButton.setOnClickListener(v -> {
            // Handle the booking history button
            Intent intent = new Intent(getContext(), BookingHistoryActivity.class);
            startActivity(intent);
        });

        // Fetch all hotels and update the RecyclerView
        fetchHotels();
        
        return view;
    }

    private void fetchHotels() {
        hotelViewModel.getAllHotels().observe(getViewLifecycleOwner(), allHotels -> {
            if (allHotels != null && !allHotels.isEmpty()) {
                hotelAdapter.prefetch(() -> {
                    hotelAdapter.updateHotelList(allHotels);
                });
            } else {
                Toast.makeText(getContext(), "No hotels found", Toast.LENGTH_SHORT).show();
                Log.d("HomepageFragment", "No hotels available to display.");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchHotels();
    }
}
