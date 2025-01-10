package com.example.travewhere;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.R;
import com.example.travewhere.adapters.HotelAdapter;
import com.example.travewhere.models.Coupon;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.viewmodels.HotelViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView searchResultsRecyclerView;
    private Button clearButton;

    private HotelViewModel hotelViewModel = new HotelViewModel();
    private HotelAdapter hotelAdapter;
    private List<Hotel> hotelResults = new ArrayList<>(); // Filtered list
    private List<Hotel> allHotels = new ArrayList<>();    // Backup list with all hotels
    private List<Coupon> couponResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        clearButton = findViewById(R.id.clearButton);

        // Initialize adapter
        hotelAdapter = new HotelAdapter(this, hotelResults);
        hotelAdapter.setOrientation(false);

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(hotelAdapter);

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

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (TextUtils.isEmpty(query)) {
                Toast.makeText(SearchActivity.this, "Enter a search query", Toast.LENGTH_SHORT).show();
            } else {
                performSearch(query);
            }
        });

        clearButton.setOnClickListener(v -> {
            searchEditText.setText(""); // Clear the search input
            hotelResults.clear();
            hotelAdapter.prefetch(() -> hotelAdapter.updateHotelList(allHotels));});
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
            Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
        }
    }
}
