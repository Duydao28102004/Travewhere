package com.example.travewhere;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.R;
import com.example.travewhere.adapters.HotelAdapter;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.viewmodels.HotelViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView searchResultsRecyclerView;

    private HotelViewModel hotelViewModel = new HotelViewModel();
    private HotelAdapter hotelAdapter;
    private List<Hotel> hotelResults = new ArrayList<>(); // Filtered list
    private List<Hotel> allHotels = new ArrayList<>();    // Backup list with all hotels

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();

        searchEditText = findViewById(R.id.searchEditText);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        // Initialize adapter
        hotelAdapter = new HotelAdapter(this, hotelResults);
        hotelAdapter.setOrientation(false);

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(hotelAdapter);

        RelativeLayout backButton = findViewById(R.id.btnBackLayout);

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // Observe LiveData once in onCreate
        hotelViewModel.getAllHotels().observe(this, fetchedHotels -> {
            if (fetchedHotels != null) {
                allHotels.clear();
                allHotels.addAll(fetchedHotels); // Backup original list
                hotelResults.clear();
                hotelResults.addAll(allHotels); // Initially show all hotels
                hotelAdapter.prefetch(() -> hotelAdapter.updateHotelList(allHotels));
            }
        });

        // Add TextWatcher for auto-search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    performSearch(query);
                } else {
                    // Show all hotels when search query is cleared
                    hotelResults.clear();
                    hotelAdapter.prefetch(() -> hotelAdapter.updateHotelList(allHotels));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String query) {
        List<Hotel> filteredResults = new ArrayList<>();
        for (Hotel hotel : allHotels) { // Use the backup list for filtering
            if (hotel.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredResults.add(hotel);
            }
        }

        hotelAdapter.prefetch(() -> hotelAdapter.updateHotelList(filteredResults));

        if (filteredResults.isEmpty()) {
            Toast.makeText(SearchActivity.this, "No results found", Toast.LENGTH_SHORT).show();
        }
    }
}
