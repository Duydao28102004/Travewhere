package com.example.travewhere;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.travewhere.fragments.ExploreFragment;
import com.example.travewhere.fragments.HomepageFragment;
import com.example.travewhere.fragments.SettingsFragment;
import com.example.travewhere.helpers.LanguageHelper;
import com.example.travewhere.models.Customer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Customer currentCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply language before super call
        LanguageHelper.applyLanguage(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Retrieve Customer object from Intent
        currentCustomer = (Customer) getIntent().getSerializableExtra("customer");

        // Load the initial HomeFragment with the Customer data
        if (savedInstanceState == null) {
            HomepageFragment homeFragment = new HomepageFragment();
            Bundle args = new Bundle();
            args.putSerializable("customer", currentCustomer);
            homeFragment.setArguments(args);
            loadFragment(homeFragment);
        }

        // Initialize the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set up the BottomNavigationView to switch between fragments
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomepageFragment();
                Bundle args = new Bundle();
                args.putSerializable("customer", currentCustomer);
                selectedFragment.setArguments(args);
            } else if (itemId == R.id.nav_explore) {
                selectedFragment = new ExploreFragment();
                // Pass customer data if needed
            } else if (itemId == R.id.nav_settings) {
                 selectedFragment = new SettingsFragment();
            } else {
                return false;
            }

            return loadFragment(selectedFragment);
        });
    }

    // Helper method to load fragments
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LanguageHelper.applyLanguage(this);
    }
}