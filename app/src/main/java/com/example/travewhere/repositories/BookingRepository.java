package com.example.travewhere.repositories;

import android.util.Log;

import com.example.travewhere.models.Booking;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    private final CollectionReference bookingsCollection;
    private static final String TAG = "BookingRepository";

    public BookingRepository() {
        bookingsCollection = FirebaseFirestore.getInstance().collection("bookings");
        Log.d(TAG, "BookingRepository initialized with Firestore");
    }

    public String getUID() {
        return bookingsCollection.document().getId();
    }

    // CREATE: Add a new booking
    public Task<Void> addBooking(Booking booking) {
        Log.d(TAG, "addBooking() called");
        if (booking.getId() == null || booking.getId().isEmpty()) {
            String uniqueId = bookingsCollection.document().getId(); // Generate a unique ID
            booking.setId(uniqueId);
            Log.d(TAG, "Generated unique ID for booking: " + uniqueId);
        }
        return bookingsCollection.document(booking.getId()).set(booking)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Booking added successfully: " + booking.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add booking: " + e.getMessage()));
    }

    // READ: Fetch a specific booking by ID
    public Task<Booking> getBookingById(String bookingId) {
        Log.d(TAG, "getBookingById() called with ID: " + bookingId);
        if (bookingId == null || bookingId.isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be null or empty");
        }
        return bookingsCollection.document(bookingId).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return task.getResult().toObject(Booking.class);
                    } else {
                        throw new Exception("Booking not found: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    // READ: Fetch all bookings for a specific customer
    public Task<List<Booking>> getBookingsByCustomerId(String customerId) {
        Log.d(TAG, "getBookingsByCustomerId() called for customer ID: " + customerId);
        return bookingsCollection.whereEqualTo("customer.id", customerId).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Booking> bookings = new ArrayList<>();
                        task.getResult().forEach(document -> {
                            Booking booking = document.toObject(Booking.class);
                            bookings.add(booking);
                        });
                        return bookings;
                    } else {
                        throw new Exception("Failed to fetch bookings: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    // UPDATE: Update an existing booking
    public Task<Void> updateBooking(Booking booking) {
        Log.d(TAG, "updateBooking() called for ID: " + booking.getId());
        if (booking.getId() == null || booking.getId().isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be null or empty");
        }
        return bookingsCollection.document(booking.getId()).set(booking)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Booking updated successfully: " + booking.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update booking: " + e.getMessage()));
    }

    // DELETE: Remove a booking by ID
    public Task<Void> deleteBooking(String bookingId) {
        Log.d(TAG, "deleteBooking() called for ID: " + bookingId);
        if (bookingId == null || bookingId.isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be null or empty");
        }
        return bookingsCollection.document(bookingId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Booking deleted successfully: " + bookingId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete booking: " + e.getMessage()));
    }
}
