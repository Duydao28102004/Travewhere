package com.example.travewhere;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travewhere.models.Customer;
import com.example.travewhere.models.MemberStatus;

import java.util.Objects;

public class LoyaltyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty);

        // Hide the top bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Retrieve Customer object from Intent
        Customer customer = (Customer) getIntent().getSerializableExtra("customer");
        if (customer != null) {
            updateMemberStatus(customer);
        }

        // Initialize the Current Point TextView
        TextView currentPoint = findViewById(R.id.customerCurrentPoint);
        assert customer != null;
        currentPoint.setText(getString(R.string.current_points, customer.getPoint()));
    }

    private void updateMemberStatus(Customer customer) {
        MemberStatus status = customer.getMemberStatus();

        TextView memberStatusTitle = findViewById(R.id.memberStatusTitle);
        ImageView memberStatusIcon = findViewById(R.id.memberStatusIcon);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView exclusiveDiscountDescription = findViewById(R.id.exclusiveDiscountDescription);
        TextView exclusiveBirthdayGiftDescription = findViewById(R.id.exclusiveBirthdayGiftDescription);
        TextView exclusiveSpecialOfferDescription = findViewById(R.id.exclusiveSpecialOfferDescription);

        // Update the UI
        memberStatusTitle.setText(status.getTitle());
        memberStatusIcon.setImageResource(status.getIconResId());
        progressBar.setProgress(customer.getPoint());
        exclusiveDiscountDescription.setText(status.getExclusiveDiscountDescription());
        exclusiveBirthdayGiftDescription.setText(status.getExclusiveBirthdayGiftDescription());
        exclusiveSpecialOfferDescription.setText(status.getExclusiveSpecialOfferDescription());

        // Calculate and set the progress
        int progress = calculateProgress(customer.getPoint());
        progressBar.setProgress(progress);
    }

    private int calculateProgress(int points) {
        if (points >= 1000) {
            return 100; // Max progress for Diamond
        } else if (points >= 500) {
            return ((points - 500) * 100) / 500; // Gold range
        } else {
            return (points * 100) / 500; // Bronze range
        }
    }
}