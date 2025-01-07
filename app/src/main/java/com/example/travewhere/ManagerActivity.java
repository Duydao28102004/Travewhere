package com.example.travewhere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.adapters.HotelAdapter;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.example.travewhere.viewmodels.ManagerViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ManagerActivity extends AppCompatActivity {
    ManagerViewModel managerViewModel;
    HotelViewModel hotelViewModel;
    AuthenticationRepository authenticationRepository;
    private HotelAdapter hotelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);
        getSupportActionBar().hide();

        authenticationRepository = new AuthenticationRepository();
        managerViewModel = new ManagerViewModel();
        hotelViewModel = new HotelViewModel();

        FloatingActionButton addHotelButton = findViewById(R.id.addHotelButton);
        addHotelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, CreateHotelActivity.class);
            startActivity(intent);
        });

        // Initialize RecyclerView and Adapter
        RecyclerView hotelRecyclerView = findViewById(R.id.accommodationList);
        hotelAdapter = new HotelAdapter(this, new ArrayList<>());
        hotelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        hotelRecyclerView.setAdapter(hotelAdapter);
        hotelAdapter.setOrientation(false);

        fetchHotels(); // Fetch hotels on activity creation
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchHotels(); // Refetch hotels when the activity resumes
    }

    private void fetchHotels() {
        String currentManagerId = authenticationRepository.getCurrentUser().getUid();

        hotelViewModel.getAllHotels().observe(this, allHotels -> {
            if (allHotels != null && !allHotels.isEmpty()) {
                // Filter hotels by manager ID
                List<Hotel> filteredHotels = new ArrayList<>();
                for (Hotel hotel : allHotels) {
                    if (hotel.getManager() != null && currentManagerId.equals(hotel.getManager().getUid())) {
                        filteredHotels.add(hotel);
                    }
                }

                // Update the adapter with the filtered list
                hotelAdapter.prefetchRooms(() -> {
                    hotelAdapter.updateHotelList(filteredHotels); // Update the adapter data
                });
            } else {
                Toast.makeText(this, "No hotels found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
