package com.example.travewhere;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travewhere.models.Customer;
import com.example.travewhere.models.MemberStatus;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.viewmodels.CustomerViewModel;

import java.util.Objects;
import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LoyaltyActivity extends AppCompatActivity {
    CustomerViewModel customerViewModel = new CustomerViewModel();
    AuthenticationRepository authenticationRepository = new AuthenticationRepository();
    Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty);

        // Hide the top bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        findViewById(R.id.btnBackLayout).setOnClickListener(v -> {
            finish();
        });

        // Fetch and wait for customer data
        fetchUser();
    }

    private void fetchUser() {
        customerViewModel.getCustomerById(authenticationRepository.getCurrentUser().getUid()).observe(this, fetchedCustomer -> {
            if (fetchedCustomer != null) {
                this.customer = fetchedCustomer;
                updateMemberStatus(customer); // Update UI when customer is fetched
                setupView(); // Set up the remaining views
            }
        });
    }

    private void setupView() {
        // Initialize the Current Point TextView
        TextView currentPoint = findViewById(R.id.customerCurrentPoint);
        if (customer != null) {
            currentPoint.setText(getString(R.string.current_points, customer.getPoint()));
        }
    }

    private void updateMemberStatus(Customer customer) {
        MemberStatus status = customer.getMemberStatus();

        TextView memberStatusTitle = findViewById(R.id.memberStatusTitle);
        ImageView memberStatusIcon = findViewById(R.id.memberStatusIcon);
        ImageView point500 = findViewById(R.id.point_500);
        ImageView point1000 = findViewById(R.id.point_1000);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView exclusiveDiscountDescription = findViewById(R.id.exclusiveDiscountDescription);
        TextView exclusiveBirthdayGiftDescription = findViewById(R.id.exclusiveBirthdayGiftDescription);
        TextView exclusiveSpecialOfferDescription = findViewById(R.id.exclusiveSpecialOfferDescription);

        if (status != null) {
            // Update UI for member status
            memberStatusTitle.setText(status.getTitle());
            memberStatusIcon.setImageResource(status.getIconResId());
            exclusiveDiscountDescription.setText(status.getExclusiveDiscountDescription());
            exclusiveBirthdayGiftDescription.setText(status.getExclusiveBirthdayGiftDescription());
            exclusiveSpecialOfferDescription.setText(status.getExclusiveSpecialOfferDescription());
            // Update milestone images
            if (customer.getPoint() >= 500) {
                point500.setImageResource(R.drawable.ic_point_reached);
            }
            if (customer.getPoint() >= 1000) {
                point1000.setImageResource(R.drawable.ic_point_reached);
            }
            // Calculate progress and set it
            int progress = calculateProgress(customer.getPoint());
            progressBar.setProgress(progress);
            progressBar.invalidate(); // Ensure the progress bar UI refreshes
        }
    }

    private int calculateProgress(int points) {
        // Clamp points to valid range (0-1000)
        points = Math.max(0, Math.min(1000, points));

        // Convert points to a percentage (0-100)
        return (points * 100) / 1000;
    }

}


