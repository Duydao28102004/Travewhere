package com.example.travewhere.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.repositories.FirestoreRepository;
import com.example.travewhere.R;
import com.example.travewhere.models.Review;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private final List<Review> reviewList;
    private final FirestoreRepository firestoreRepository;
    private final OnReviewDeleteListener deleteListener;

    public interface OnReviewDeleteListener {
        void onDeleteReview(String reviewId);
    }


    public ReviewAdapter(Context context, List<Review> reviewList, FirestoreRepository firestoreRepository, OnReviewDeleteListener deleteListener) {
        this.context = context;
        this.reviewList = reviewList;
        this.firestoreRepository = firestoreRepository;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.contentTextView.setText(review.getContent());
        holder.ratingBar.setRating(review.getRating());
        firestoreRepository.getUserNameById(review.getUserId(), userName -> {
            holder.userIdTextView.setText(userName != null ? userName : "Unknown User");
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        holder.timestampTextView.setText(dateFormat.format(review.getTimestamp().toDate()));

        // Show or hide delete button based on user authorization
        String currentUserId = FirestoreRepository.getCurrentUserId();
        if (review.getUserId().equals(currentUserId)) {
            holder.btnDeleteReview.setVisibility(View.VISIBLE);
            holder.btnDeleteReview.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteReview(review.getId());
                }
            });
        } else {
            holder.btnDeleteReview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void setReviews(List<Review> reviews) {
        this.reviewList.clear();
        this.reviewList.addAll(reviews);
        notifyDataSetChanged();
    }


    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView userIdTextView;
        TextView contentTextView;
        RatingBar ratingBar;
        TextView timestampTextView;
        ImageButton btnDeleteReview;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdTextView = itemView.findViewById(R.id.tvUsername);
            contentTextView = itemView.findViewById(R.id.tvReviewContent);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            timestampTextView = itemView.findViewById(R.id.tvDateTime);
            btnDeleteReview = itemView.findViewById(R.id.btnDeleteReview);
        }
    }
}
