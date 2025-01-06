package com.example.travewhere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.travewhere.helpers.DateTimeHelper;
import com.example.travewhere.viewmodels.HotelViewModel;

import java.util.Locale;

public class HotelDetailActivity extends AppCompatActivity {
    private static final String TAG = "HotelDetailActivity";
    private ImageButton btnBack;
    private ImageView imgHotel, imgCall, imgDirections, imgReviews;
    private TextView tvHotelName, tvHotelLocation, tvPhoneNumber, tvEmail, tvCallAccommodation, getDirection, showReviews;
    private RatingBar ratingBar;
    private Button btnCheckInTime, btnCheckOutTime, btnAddToBookingList;

    private HotelViewModel hotelViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        hotelViewModel = new ViewModelProvider(this).get(HotelViewModel.class);

        // Initialize UI components
        btnBack = findViewById(R.id.btnBack);
        imgHotel = findViewById(R.id.imgHotel);
        tvHotelName = findViewById(R.id.tvHotelName);
        tvHotelLocation = findViewById(R.id.tvHotelLocation);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvEmail = findViewById(R.id.tvEmail);
        ratingBar = findViewById(R.id.ratingBar);
        btnCheckInTime = findViewById(R.id.btnCheckInTime);
        btnCheckOutTime = findViewById(R.id.btnCheckOutTime);
        btnAddToBookingList = findViewById(R.id.btnAddToBookingList);
        imgCall = findViewById(R.id.imgCallAccommodation);
        imgDirections = findViewById(R.id.imgDirections);
        imgReviews = findViewById(R.id.imgReviews);
        tvCallAccommodation = findViewById(R.id.tvCallAccommodation);
        getDirection = findViewById(R.id.getDirection);
        showReviews = findViewById(R.id.showReviews);

        // Get the hotel ID from the intent
        String hotelId = getIntent().getStringExtra("HOTEL_ID");
        if (hotelId != null) {
            fetchHotelDetails(hotelId);
        } else {
            Toast.makeText(this, "No hotel ID provided!", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBack.setOnClickListener(v -> finish());
        
        btnCheckInTime.setOnClickListener(v -> DateTimeHelper.showDateTimePicker(this, btnCheckInTime));
        btnCheckOutTime.setOnClickListener(v -> DateTimeHelper.showDateTimePicker(this, btnCheckOutTime));

        btnAddToBookingList.setOnClickListener(v -> {
            Toast.makeText(HotelDetailActivity.this, "Added to booking list!", Toast.LENGTH_SHORT).show();
        });

        LinearLayout linearLayoutCallAccommodation = findViewById(R.id.linearLayoutCallAccommodation);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        // Click listener for Call Accommodation Linear Layout
        linearLayoutCallAccommodation.setOnClickListener(v -> {
            String phoneNumber = tvPhoneNumber.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            } else {
                Toast.makeText(HotelDetailActivity.this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout linearLayoutShowReviews = findViewById(R.id.linearLayoutShowReviews);
        linearLayoutShowReviews.setOnClickListener(v -> {
            Intent intent = new Intent(HotelDetailActivity.this, ReviewActivity.class);
            intent.putExtra("HOTEL_ID", hotelId);
            startActivity(intent);
        });
    }

    private void displayAverageRating(String hotelId) {
        FirestoreRepository firestoreRepository = new FirestoreRepository(this);

        firestoreRepository.calculateAverageRating(hotelId, averageRating -> {
            ratingBar.setRating(averageRating);
            TextView tvAverageRating = findViewById(R.id.tvAverageRating);
            tvAverageRating.setText(String.format(Locale.getDefault(), "%.1f", averageRating));
        });
    }

    private void fetchHotelDetails(String hotelId) {
        hotelViewModel.getHotelById(hotelId).observe(this, hotel -> {
            if (hotel != null) {
                tvHotelName.setText(hotel.getName());
                tvHotelLocation.setText(hotel.getAddress());
                tvPhoneNumber.setText(hotel.getPhoneNumber());
                tvEmail.setText(hotel.getEmail());
                displayAverageRating(hotelId);
            } else {
                Toast.makeText(HotelDetailActivity.this, "Hotel details not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
