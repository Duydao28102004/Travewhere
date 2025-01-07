package com.example.travewhere.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmDeleteDialogFragment extends DialogFragment {

    private static final String ARG_REVIEW_ID = "review_id";

    private ConfirmDeleteListener listener;

    public interface ConfirmDeleteListener {
        void onConfirmDelete(String reviewId);
    }

    public static ConfirmDeleteDialogFragment newInstance(String reviewId) {
        ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REVIEW_ID, reviewId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setConfirmDeleteListener(ConfirmDeleteListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String reviewId = getArguments() != null ? getArguments().getString(ARG_REVIEW_ID) : null;

        return new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this review?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (listener != null && reviewId != null) {
                        listener.onConfirmDelete(reviewId);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
    }
}
