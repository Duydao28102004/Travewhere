package com.example.travewhere.repositories;

import android.util.Log;

import com.example.travewhere.models.Review;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Map;

public class ReviewRepository {
    private static final String TAG = "ReviewRepository";
    private final CollectionReference reviewsCollection;

    public ReviewRepository() {
        reviewsCollection = FirebaseFirestore.getInstance().collection("reviews");
        Log.d(TAG, "ReviewRepository initialized with Firestore");
    }

    // Add a new review
    public Task<Void> addReview(Review review) {
        String reviewId = reviewsCollection.document().getId();
        review.setId(reviewId);
        return reviewsCollection.document(reviewId).set(review);
    }

    // Fetch all reviews for a hotel
    public Query getReviewsByHotelId(String hotelId) {
        return reviewsCollection.whereEqualTo("hotelId", hotelId).orderBy("timestamp", Query.Direction.DESCENDING);
    }

    // Fetch all reviews
    public Task<List<Review>> getAllReviews() {
        return reviewsCollection.get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Review> reviewList = task.getResult().toObjects(Review.class);
                Log.d(TAG, "Fetched " + reviewList.size() + " reviews from Firestore");
                return reviewList;
            } else {
                Log.e(TAG, "Failed to fetch reviews: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                return null;
            }
        });
    }

    // Update a review
    public Task<Void> updateReview(String reviewId, Map<String, Object> updates) {
        return reviewsCollection.document(reviewId).update(updates);
    }

    // Delete a review
    public Task<Void> deleteReview(String reviewId) {
        return reviewsCollection.document(reviewId).delete();
    }
}
