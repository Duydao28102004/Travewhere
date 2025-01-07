package com.example.travewhere.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.travewhere.R;
import com.example.travewhere.ReviewActivity;
import com.example.travewhere.models.Review;
import com.google.firebase.Timestamp;

public class AddReviewDialogFragment extends DialogFragment {

    private RatingBar ratingBar;
    private EditText reviewContentEditText;
    private Button cancelButton;
    private Button submitButton;

    public interface AddReviewListener {
        void onReviewSubmitted(Review review);
    }

    private AddReviewListener listener;
    private String hotelId;
    private String userId;

    public AddReviewDialogFragment() {
        // Required empty constructor
    }

    public static AddReviewDialogFragment newInstance(String hotelId, String userId) {
        AddReviewDialogFragment fragment = new AddReviewDialogFragment();
        Bundle args = new Bundle();
        args.putString("HOTEL_ID", hotelId);
        args.putString("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setAddReviewListener(AddReviewListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_review_dialog, container, false);

        if (getArguments() != null) {
            hotelId = getArguments().getString("HOTEL_ID");
            userId = getArguments().getString("USER_ID");
        }

        ratingBar = view.findViewById(R.id.ratingBarAddReview);
        reviewContentEditText = view.findViewById(R.id.etReviewContent);
        cancelButton = view.findViewById(R.id.btnCancelReview);
        submitButton = view.findViewById(R.id.btnSubmitReview);

        cancelButton.setOnClickListener(v -> dismiss());
        submitButton.setOnClickListener(v -> submitReview());

        return view;
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String content = reviewContentEditText.getText().toString().trim();

        if (rating == 0.0f) {
            Toast.makeText(getContext(), "Please provide a rating!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(getContext(), "Please write a review!", Toast.LENGTH_SHORT).show();
            return;
        }

        Review review = new Review();
        review.setHotelId(hotelId);
        review.setUserId(userId);
        review.setContent(content);
        review.setRating(rating);
        review.setTimestamp(Timestamp.now());

        if (listener != null) {
            listener.onReviewSubmitted(review);
        }

        dismiss();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (getActivity() instanceof ReviewActivity) {
            ((ReviewActivity) getActivity()).refreshReviews();
        }
    }

}
