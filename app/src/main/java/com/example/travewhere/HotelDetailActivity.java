package com.example.travewhere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travewhere.adapters.RoomAdapter;
import com.example.travewhere.repositories.FirestoreRepository;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.example.travewhere.viewmodels.RoomViewModel;

import java.util.Locale;

public class HotelDetailActivity extends AppCompatActivity {
    private static final String TAG = "HotelDetailActivity";
    private RelativeLayout btnBackLayout;
    private ImageView imgHotel, imgCall, imgDirections, imgReviews;
    private TextView tvHotelName, tvHotelLocation, tvPhoneNumber, tvEmail, tvCallAccommodation, getDirection, showReviews;
    private RatingBar ratingBar;

    private HotelViewModel hotelViewModel;
    private RoomViewModel roomViewModel;
    private RecyclerView roomListRecyclerView;
    private RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);
        getSupportActionBar().hide();

        hotelViewModel = new ViewModelProvider(this).get(HotelViewModel.class);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        // Initialize UI components
        btnBackLayout = findViewById(R.id.btnBackLayout);
        imgHotel = findViewById(R.id.imgHotel);
        tvHotelName = findViewById(R.id.tvHotelName);
        tvHotelLocation = findViewById(R.id.tvHotelLocation);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvEmail = findViewById(R.id.tvEmail);
        ratingBar = findViewById(R.id.ratingBar);
        imgCall = findViewById(R.id.imgCallAccommodation);
        imgDirections = findViewById(R.id.imgDirections);
        imgReviews = findViewById(R.id.imgReviews);
        tvCallAccommodation = findViewById(R.id.tvCallAccommodation);
        getDirection = findViewById(R.id.getDirection);
        showReviews = findViewById(R.id.showReviews);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        roomListRecyclerView = findViewById(R.id.roomListRecyclerView);

        // Get the hotel ID from the intent
        String hotelId = getIntent().getStringExtra("HOTEL_ID");
        if (hotelId != null) {
            fetchHotelDetails(hotelId);
            fetchHotelRooms(hotelId);
        } else {
            Toast.makeText(this, "No hotel ID provided!", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBackLayout.setOnClickListener(v -> finish());

        LinearLayout linearLayoutGetDirection = findViewById(R.id.linearLayoutGetDirection);
        linearLayoutGetDirection.setOnClickListener(v -> {
            hotelViewModel.getHotelById(hotelId).observe(this, hotel -> {
                if (hotel != null) {
                    double latitude = hotel.getLatitude();
                    double longitude = hotel.getLongitude();
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", latitude, longitude, latitude, longitude, Uri.encode(hotel.getName()));

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(HotelDetailActivity.this, "Google Maps is not installed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HotelDetailActivity.this, "Hotel details not available", Toast.LENGTH_SHORT).show();
                }
            });
        });

        LinearLayout linearLayoutCallAccommodation = findViewById(R.id.linearLayoutCallAccommodation);
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

                // Using Glide to display hotel image
                String imageUrl = hotel.getImageUrl(); // Assuming you have an `imageUrl` field in your Hotel class
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.testingimage) // Placeholder while loading
                        .error(R.drawable.testingimage) // Fallback in case of error
                        .into(imgHotel);
            } else {
                Toast.makeText(HotelDetailActivity.this, "Hotel details not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchHotelRooms(String hotelId) {
        roomViewModel.getRoomsByHotel(hotelId).observe(this, rooms -> {
            if (rooms != null && !rooms.isEmpty()) {
                roomAdapter = new RoomAdapter(HotelDetailActivity.this, rooms);
                roomAdapter.prefetch(() -> {});
                roomListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                roomListRecyclerView.setAdapter(roomAdapter);
            } else {
                Toast.makeText(HotelDetailActivity.this, "No rooms available for this hotel!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String hotelId = getIntent().getStringExtra("HOTEL_ID");
        if (hotelId != null) {
            displayAverageRating(hotelId);
        }
    }

}
