package com.example.travewhere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.adapters.HotelAdapter;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.example.travewhere.viewmodels.ManagerViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ManagerActivity extends AppCompatActivity {
    ManagerViewModel managerViewModel;
    HotelViewModel hotelViewModel;
    AuthenticationRepository authenticationRepository;
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

        RecyclerView hotelRecyclerView = findViewById(R.id.accommodationList);
        HotelAdapter hotelAdapter = new HotelAdapter(this, hotelViewModel.getHotelsList());
        hotelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        hotelRecyclerView.setAdapter(hotelAdapter);
        hotelAdapter.setOrientation(false);


        managerViewModel.getManagerById(authenticationRepository.getCurrentUser().getUid()).observe(this, manager -> {
            if (manager != null) {
                for (String hotelId : manager.getHotelList()) {
                    hotelViewModel.getHotelById(hotelId).observe(this, hotel -> {
                        if (hotel != null) {
                            hotelViewModel.getHotelsList().add(hotel);
                            hotelAdapter.notifyDataSetChanged(); // Update adapter after adding new data
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Manager not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

}