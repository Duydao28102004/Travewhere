package com.example.travewhere;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.adapters.RoomAdapter;
import com.example.travewhere.models.Room;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.example.travewhere.viewmodels.RoomViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateHotelActivity extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private PlacesClient placesClient;
    private ArrayAdapter<String> adapter;

    private Button selectAddressButton;
    private EditText roomTypeEditText, priceEditText, capacityEditText;
    private RecyclerView roomRecyclerView;
    private RoomAdapter roomAdapter;

    private HotelViewModel hotelViewModel;
    private RoomViewModel roomViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hotel);

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAmYG0ewlmb4zaJAkC6pBsFjqi0NBQu-Po");
        }

        // Add instance in ViewModel
        hotelViewModel = new HotelViewModel();
        roomViewModel = new RoomViewModel();

        selectAddressButton = findViewById(R.id.selectAddressButton);
        roomTypeEditText = findViewById(R.id.roomTypeEditText);
        priceEditText = findViewById(R.id.priceEditText);
        capacityEditText = findViewById(R.id.capacityEditText);
        roomRecyclerView = findViewById(R.id.roomRecyclerView);

        selectAddressButton.setOnClickListener(v -> openAutocomplete());

        roomAdapter = new RoomAdapter(this, roomViewModel.getRoomList());

        roomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomRecyclerView.setAdapter(roomAdapter);

        findViewById(R.id.addRoomButton).setOnClickListener(v -> addRoom());
    }

    private void addRoom() {
        String roomType = roomTypeEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();
        String capacityStr = capacityEditText.getText().toString();

        if (!roomType.isEmpty() && !priceStr.isEmpty() && !capacityStr.isEmpty()) {
            double price = Double.parseDouble(priceStr);
            int capacity = Integer.parseInt(capacityStr);
            roomViewModel.getRoomList().add(new Room("",roomType, price,"", capacity));
            roomAdapter.notifyItemInserted(roomViewModel.getRoomList().size() - 1);

            roomTypeEditText.setText("");
            priceEditText.setText("");
            capacityEditText.setText("");
        }
    }

    // Method to open Google Places Autocomplete
    private void openAutocomplete() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    // Handle the result of the address selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("AutocompleteError", status.getStatusMessage());
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle the user canceling the autocomplete
                Toast.makeText(this, "Address selection canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}