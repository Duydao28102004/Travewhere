package com.example.travewhere.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travewhere.models.Booking;
import com.example.travewhere.repositories.BookingRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookingViewModel extends ViewModel {
    private final BookingRepository bookingRepository;
    private final MutableLiveData<List<Booking>> bookingsLiveData;
    private final MutableLiveData<String> statusLiveData;

    public BookingViewModel() {
        bookingRepository = new BookingRepository();
        bookingsLiveData = new MutableLiveData<>();
        statusLiveData = new MutableLiveData<>();
    }

    public String getUID() {
        return bookingRepository.getUID();
    }

    // Expose bookings as LiveData
    public LiveData<List<Booking>> getBookingsLiveData() {
        return bookingsLiveData;
    }

    // Expose status as LiveData
    public LiveData<String> getStatusLiveData() {
        return statusLiveData;
    }

    // Add a new booking
    public void addBooking(Booking booking) {
        bookingRepository.addBooking(booking).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                statusLiveData.setValue("Booking added successfully");
            } else {
                statusLiveData.setValue("Failed to add booking: " + task.getException().getMessage());
            }
        });
    }

    // Fetch all bookings for a specific customer
    public void fetchBookingsByCustomerId(String customerId) {
        bookingRepository.getBookingsByCustomerId(customerId).addOnCompleteListener(new OnCompleteListener<List<Booking>>() {
            @Override
            public void onComplete(@NonNull Task<List<Booking>> task) {
                if (task.isSuccessful()) {
                    List<Booking> bookings = task.getResult();
                    bookingsLiveData.setValue(bookings);
                } else {
                    statusLiveData.setValue("Failed to fetch bookings: " + task.getException().getMessage());
                }
            }
        });
    }

    // Fetch a specific booking by ID
    public void fetchBookingById(String bookingId) {
        bookingRepository.getBookingById(bookingId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Booking> singleBooking = new ArrayList<>();
                singleBooking.add(task.getResult());
                bookingsLiveData.setValue(singleBooking);
            } else {
                statusLiveData.setValue("Failed to fetch booking: " + task.getException().getMessage());
            }
        });
    }

    // Update an existing booking
    public void updateBooking(Booking booking) {
        bookingRepository.updateBooking(booking).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                statusLiveData.setValue("Booking updated successfully");
            } else {
                statusLiveData.setValue("Failed to update booking: " + task.getException().getMessage());
            }
        });
    }

    // Delete a booking by ID
    public void deleteBooking(String bookingId) {
        bookingRepository.deleteBooking(bookingId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                statusLiveData.setValue("Booking deleted successfully");
            } else {
                statusLiveData.setValue("Failed to delete booking: " + task.getException().getMessage());
            }
        });
    }
}
