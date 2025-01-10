package com.example.travewhere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.adapters.RoomAdapter;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.models.Room;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.example.travewhere.viewmodels.ManagerViewModel;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateHotelActivity extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 1001;
    private Uri imageUri;
    private ImageView hotelImageView;
    private Button selectImageButton;

    private PlacesClient placesClient;
    private ArrayAdapter<String> adapter;
    private Place selectedPlace;

    private RelativeLayout btnBackLayout;
    private TextView nameTextView, phoneTextView, selectedAddressTextView;
    private Button selectAddressButton;
    private EditText roomTypeEditText, priceEditText, capacityEditText;
    private RecyclerView roomRecyclerView;
    private RoomAdapter roomAdapter;

    private HotelViewModel hotelViewModel;
    private RoomViewModel roomViewModel;
    private ManagerViewModel managerViewModel;
    private AuthenticationRepository authenticationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hotel);
        getSupportActionBar().hide();

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAmYG0ewlmb4zaJAkC6pBsFjqi0NBQu-Po");
        }

        // Add instance in ViewModel
        hotelViewModel = new HotelViewModel();
        roomViewModel = new RoomViewModel();
        managerViewModel = new ManagerViewModel();

        authenticationRepository = new AuthenticationRepository();

        // Set up the UI
        btnBackLayout = findViewById(R.id.btnBackLayout);
        nameTextView = findViewById(R.id.nameEditText);
        phoneTextView = findViewById(R.id.phoneEditText);
        selectedAddressTextView = findViewById(R.id.addressTextView);
        selectAddressButton = findViewById(R.id.selectAddressButton);
        roomTypeEditText = findViewById(R.id.roomTypeEditText);
        priceEditText = findViewById(R.id.priceEditText);
        capacityEditText = findViewById(R.id.capacityEditText);
        roomRecyclerView = findViewById(R.id.roomRecyclerView);

        hotelImageView = findViewById(R.id.hotelImageView);
        selectImageButton = findViewById(R.id.uploadImageButton);

        // Set up the image picker
        selectImageButton.setOnClickListener(v -> openImagePicker());


        selectAddressButton.setOnClickListener(v -> openAutocomplete());

        roomAdapter = new RoomAdapter(this, roomViewModel.getRoomList());
        roomAdapter.prefetch(() -> {});
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomRecyclerView.setAdapter(roomAdapter);

        btnBackLayout.setOnClickListener(v -> finish());
        findViewById(R.id.addRoomButton).setOnClickListener(v -> addRoom());
        findViewById(R.id.saveButton).setOnClickListener(v -> saveHotel());
    }

    private boolean validateFields() {
        if (nameTextView.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Hotel name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneTextView.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedPlace == null || selectedPlace.getAddress() == null || selectedPlace.getAddress().trim().isEmpty()) {
            Toast.makeText(this, "Please select an address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (roomViewModel.getRoomList().isEmpty()) {
            Toast.makeText(this, "Please add at least one room", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadImage(String hotelId) {
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("hotel_images/" + hotelId + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                // Save the image URL to the database
                                hotelViewModel.updateHotelImage(hotelId, imageUrl);
                                Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveHotel() {
        if (!validateFields()) {
            return;
        }
        Toast.makeText(this, "Saving hotel...", Toast.LENGTH_SHORT).show();
        List<String> roomIdList = new ArrayList<>();

        String hotelId = hotelViewModel.getUID();
        double latitude = selectedPlace.getLatLng().latitude;
        double longitude = selectedPlace.getLatLng().longitude;
        managerViewModel.getManagerById(authenticationRepository.getCurrentUser().getUid()).observe(this, manager -> {
            for (Room room : roomViewModel.getRoomList()) {
                roomIdList.add(room.getId());
                room.setHotelId(hotelId);
                roomViewModel.addRoom(room);
            }
            hotelViewModel.addHotel(new Hotel(hotelId, nameTextView.getText().toString(), selectedPlace.getAddress(), latitude, longitude, phoneTextView.getText().toString(), "", manager, roomIdList));
            manager.getHotelList().add(hotelId);
            managerViewModel.updateManager(manager);
            uploadImage(hotelId);
            finish();
        });
    }

    private void addRoom() {
        String roomType = roomTypeEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();
        String capacityStr = capacityEditText.getText().toString();

        if (!roomType.isEmpty() && !priceStr.isEmpty() && !capacityStr.isEmpty()) {
            double price = Double.parseDouble(priceStr);
            int capacity = Integer.parseInt(capacityStr);
            roomViewModel.getRoomList().add(new Room(roomViewModel.getUID(), roomType, price,"", capacity));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageUri = data.getData(); // Retrieve the selected image URI
                Log.d("ImagePicker", "Image URI: " + imageUri.toString());

                // Persist URI permission
                try {
                    getContentResolver().takePersistableUriPermission(
                            imageUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                    Log.d("ImagePicker", "Persisted URI permission");
                } catch (SecurityException e) {
                    Log.e("ImagePicker", "Failed to persist URI permission: " + e.getMessage());
                }

                // Display the image in the ImageView
                hotelImageView.setImageURI(imageUri);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                selectedPlace = Autocomplete.getPlaceFromIntent(data);
                selectedAddressTextView.setText(selectedPlace.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("AutocompleteError", status.getStatusMessage());
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Address selection canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

}