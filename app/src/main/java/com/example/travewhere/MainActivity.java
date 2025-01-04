package com.example.travewhere;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.travewhere.fragments.HomepageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Load the initial HomeFragment with the arguments
        if (savedInstanceState == null) {
            HomepageFragment homeFragment = new HomepageFragment();
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
//                selectedFragment.setArguments(args);
            } else if (itemId == R.id.nav_explore) {
//                selectedFragment = new ExploreFragment();
            } else if (itemId == R.id.nav_saved) {
//                selectedFragment = new SavedFragment();
            } else if (itemId == R.id.nav_settings) {
//                selectedFragment = new SettingsFragment();
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
}