package com.example.travewhere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.adapters.HotelAdapter;
import com.example.travewhere.models.Booking;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.models.Manager;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.viewmodels.BookingViewModel;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.example.travewhere.viewmodels.ManagerViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ManagerActivity extends AppCompatActivity {
    ManagerViewModel managerViewModel;
    HotelViewModel hotelViewModel;
    BookingViewModel bookingViewModel;
    AuthenticationRepository authenticationRepository;
    private HotelAdapter hotelAdapter;
    private List<Booking> bookingList = new ArrayList<>();
    private Manager currentManager = null;
    TextView greetingText, totalBookingsValue, totalIncomeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);
        getSupportActionBar().hide();

        authenticationRepository = new AuthenticationRepository();
        managerViewModel = new ManagerViewModel();
        hotelViewModel = new HotelViewModel();
        bookingViewModel = new BookingViewModel();

        greetingText = findViewById(R.id.greetingText);
        totalBookingsValue = findViewById(R.id.totalBookingsValue);
        totalIncomeValue = findViewById(R.id.totalIncomeValue);

        FloatingActionButton addHotelButton = findViewById(R.id.addHotelButton);
        addHotelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, CreateHotelActivity.class);
            startActivity(intent);
        });

        TextView bookingsSeeMore = findViewById(R.id.bookingsSeeMore);
        bookingsSeeMore.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, BookingHistoryActivity.class);
            startActivity(intent);
        });

        // Initialize RecyclerView and Adapter
        RecyclerView hotelRecyclerView = findViewById(R.id.accommodationList);
        hotelAdapter = new HotelAdapter(this, new ArrayList<>());
        hotelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        hotelRecyclerView.setAdapter(hotelAdapter);
        hotelAdapter.setOrientation(false);

        fetchHotelsAndBookings(); // Fetch hotels on activity creation
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchHotelsAndBookings(); // Refetch hotels when the activity resumes
    }

    private void fetchHotelsAndBookings() {
        String currentManagerId = authenticationRepository.getCurrentUser().getUid();

        managerViewModel.getManagerById(authenticationRepository.getCurrentUser().getUid()).observe(this, manager -> {
            if (manager != null) {
                currentManager = manager;
                greetingText.setText("Welcome back, " + manager.getName() + "!");
                bookingViewModel.getAllBooking().observe(this, allBookings -> {
                    if (allBookings != null && !allBookings.isEmpty()) {
                        // Filter bookings by manager ID
                        List<Booking> filteredBookings = new ArrayList<>();
                        double totalEarning = 0;
                        for (Booking booking : allBookings) {
                            if (currentManager.getHotelList().contains(booking.getHotelId())) {
                                filteredBookings.add(booking);
                                totalEarning += booking.getTotalPrice();
                            }
                        }

                        totalBookingsValue.setText(String.valueOf(filteredBookings.size()));
                        totalIncomeValue.setText(String.format("$ %.2f", totalEarning));

                        // Update the booking list
                        bookingList.clear();
                        bookingList.addAll(filteredBookings);
                    } else {
                        Toast.makeText(this, "No bookings found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

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
                hotelAdapter.prefetch(() -> {
                    hotelAdapter.updateHotelList(filteredHotels); // Update the adapter data
                });
            } else {
                Toast.makeText(this, "No hotels found", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
