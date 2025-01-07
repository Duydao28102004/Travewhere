package com.example.travewhere.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travewhere.models.Review;
import com.example.travewhere.repositories.ReviewRepository;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class ReviewViewModel extends ViewModel {
    private final ReviewRepository reviewRepository;
    private final MutableLiveData<List<Review>> reviewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> operationStatus = new MutableLiveData<>();

    public ReviewViewModel() {
        reviewRepository = new ReviewRepository();
    }

    public LiveData<List<Review>> getReviews(String hotelId) {
        reviewRepository.getReviewsByHotelId(hotelId)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        operationStatus.postValue("Error fetching reviews: " + e.getMessage());
                        return;
                    }
                    if (querySnapshot != null) {
                        List<Review> reviews = querySnapshot.toObjects(Review.class);
                        reviewsLiveData.postValue(reviews);
                    }
                });
        return reviewsLiveData;
    }

    public LiveData<List<Review>> getAllReviews() {
        reviewRepository.getAllReviews()
                .addOnSuccessListener(reviews -> reviewsLiveData.postValue(reviews))
                .addOnFailureListener(e -> operationStatus.postValue("Failed to fetch reviews: " + e.getMessage()));
        return reviewsLiveData;
    }

    public void addReview(Review review) {
        reviewRepository.addReview(review)
                .addOnSuccessListener(aVoid -> operationStatus.postValue("Review added successfully!"))
                .addOnFailureListener(e -> operationStatus.postValue("Failed to add review: " + e.getMessage()));
    }

    public void updateReview(String reviewId, Map<String, Object> updates) {
        reviewRepository.updateReview(reviewId, updates)
                .addOnSuccessListener(aVoid -> operationStatus.postValue("Review updated successfully!"))
                .addOnFailureListener(e -> operationStatus.postValue("Failed to update review: " + e.getMessage()));
    }

    public void deleteReview(String reviewId) {
        reviewRepository.deleteReview(reviewId)
                .addOnSuccessListener(aVoid -> operationStatus.postValue("Review deleted successfully!"))
                .addOnFailureListener(e -> operationStatus.postValue("Failed to delete review: " + e.getMessage()));
    }

    public LiveData<String> getOperationStatus() {
        return operationStatus;
    }
}
