package com.example.travewhere;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.adapters.ReviewAdapter;
import com.example.travewhere.fragments.AddReviewDialogFragment;
import com.example.travewhere.fragments.ConfirmDeleteDialogFragment;
import com.example.travewhere.repositories.FirestoreRepository;
import com.example.travewhere.viewmodels.ReviewViewModel;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {
    private ReviewAdapter reviewAdapter;
    private ReviewViewModel reviewViewModel;
    private RecyclerView reviewRecyclerView;
    private String hotelId;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        getSupportActionBar().hide();
        FirestoreRepository firestoreRepository = new FirestoreRepository(this);

        hotelId = getIntent().getStringExtra("HOTEL_ID");

        if (hotelId == null) {
            Toast.makeText(this, "Hotel ID not provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        btnBack = findViewById(R.id.btnBack);
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(this, new ArrayList<>(), new FirestoreRepository(this), reviewId -> {
            ConfirmDeleteDialogFragment dialogFragment = ConfirmDeleteDialogFragment.newInstance(reviewId);

            dialogFragment.setConfirmDeleteListener(reviewIdToDelete -> {
                reviewViewModel.deleteReview(reviewIdToDelete);
                Toast.makeText(this, "Review deleted!", Toast.LENGTH_SHORT).show();
                refreshReviews();
                reviewAdapter.notifyDataSetChanged();
            });

            dialogFragment.show(getSupportFragmentManager(), "ConfirmDeleteDialogFragment");
        });

        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        btnBack.setOnClickListener(v -> finish());

        fetchReviews(hotelId);

        // Add review button setup
        Button addReviewButton = findViewById(R.id.btnAddReview);
        addReviewButton.setOnClickListener(v -> {
            String userId = FirestoreRepository.getCurrentUserId();

            if (userId != null) {
                firestoreRepository.checkIfUserReviewedHotel(hotelId, userId, task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // User has already reviewed the hotel
                        Toast.makeText(this, "You have already reviewed this hotel.", Toast.LENGTH_SHORT).show();
                    } else {
                        // User hasn't reviewed the hotel, show review dialog
                        AddReviewDialogFragment dialogFragment = AddReviewDialogFragment.newInstance(hotelId, userId);

                        dialogFragment.setAddReviewListener(review -> {
                            reviewViewModel.addReview(review);
                            Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show();
                        });

                        dialogFragment.show(getSupportFragmentManager(), "AddReviewDialogFragment");
                    }
                });
            } else {
                Toast.makeText(this, "You must be logged in to add a review!", Toast.LENGTH_SHORT).show();
            }
        });

        reviewViewModel.getOperationStatus().observe(this, status -> {
            if (status != null) {
                Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchReviews(String hotelId) {
        reviewViewModel.getReviews(hotelId).observe(this, reviews -> {
            if (reviews != null) {
                reviewAdapter.setReviews(reviews);
            } else {
                Toast.makeText(this, "No reviews available for this hotel.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshReviews() {
        fetchReviews(hotelId);
    }

}
