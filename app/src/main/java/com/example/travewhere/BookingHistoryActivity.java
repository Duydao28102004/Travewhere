package com.example.travewhere;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.adapters.BookingAdapter;
import com.example.travewhere.models.Booking;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.viewmodels.BookingViewModel;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView bookingRecyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private BookingViewModel bookingViewModel;
    private AuthenticationRepository authenticationRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        getSupportActionBar().hide();

        // Initialize components
        bookingRecyclerView = findViewById(R.id.recycler_view_bookings);
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(this, bookingList);
        bookingRecyclerView.setAdapter(bookingAdapter);

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
        authenticationRepository = new AuthenticationRepository();

        findViewById(R.id.btnBackLayout).setOnClickListener(v -> {
            finish();
        });

        fetchUserBookings();
    }

    private void fetchUserBookings() {
        String userId = authenticationRepository.getCurrentUser().getUid();

        bookingViewModel.getAllBooking().observe(this, bookings -> {
            bookingList.clear(); // Clear the list to avoid duplication
            for (Booking booking : bookings) {
                Log.d("BookingHistoryActivity", "Booking: " + booking.getCustomerId());
                if (booking.getCustomerId().equals(userId)) {
                    bookingList.add(booking);
                }
            }

            // Call prefetch after filtering user bookings
            bookingAdapter.prefetch(() -> {
                // Notify adapter once prefetch is complete
                bookingAdapter.notifyDataSetChanged();
                Log.d("BookingHistoryActivity", "Prefetch complete and adapter notified.");
            });
        });
    }
}
