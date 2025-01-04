package com.example.travewhere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travewhere.helpers.DateTimeHelper;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.repositories.HotelRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class HotelDetailActivity extends AppCompatActivity {
    private static final String TAG = "HotelDetailActivity";
    private ImageButton btnBack;
    private ImageView imgHotel, imgCall, imgDirections, imgReviews;
    private TextView tvHotelName, tvHotelLocation, tvPhoneNumber, tvEmail, tvCallAccommodation, getDirection, showReviews;
    private RatingBar ratingBar;
    private Button btnCheckInTime, btnCheckOutTime, btnAddToBookingList;

    private HotelRepository hotelRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        hotelRepository = new HotelRepository();

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
    }

    private void fetchHotelDetails(String hotelId) {
        hotelRepository.getHotelById(hotelId)
                .addOnSuccessListener(new OnSuccessListener<Hotel>() {
                    @Override
                    public void onSuccess(Hotel hotel) {
                        if (hotel != null) {
                            tvHotelName.setText(hotel.getName());
                            tvHotelLocation.setText(hotel.getAddress());
                            tvPhoneNumber.setText(hotel.getPhoneNumber());
                            tvEmail.setText(hotel.getEmail());

                            // ratingBar.setRating((float) hotel.getRating());
                            //  imgHotel.setImageResource(hotel.getImageResourceId());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to fetch hotel details", e);
                        Toast.makeText(HotelDetailActivity.this, "Failed to fetch hotel details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
