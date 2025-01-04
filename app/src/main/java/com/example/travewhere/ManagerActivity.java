package com.example.travewhere;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        // Get List Hotels own by customer
        managerViewModel.getManagerById(authenticationRepository.getCurrentUser().getUid()).observe(this, manager -> {
            if (manager != null) {
                hotelViewModel.getAllHotels().observe(this, hotels -> {

                });
            }
        });
    }
}