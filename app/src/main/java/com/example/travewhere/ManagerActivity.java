package com.example.travewhere;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);
        FloatingActionButton addHotelButton = findViewById(R.id.addHotelButton);
        addHotelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, CreateHotelActivity.class);
            startActivity(intent);
        });
    }
}