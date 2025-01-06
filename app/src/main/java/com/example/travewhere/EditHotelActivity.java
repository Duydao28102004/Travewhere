package com.example.travewhere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.example.travewhere.adapters.RoomAdapter;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.models.Room;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.example.travewhere.viewmodels.RoomViewModel;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditHotelActivity extends AppCompatActivity {
    private Uri imageUri;
    private ImageView hotelImageView;
    private Button selectImageButton, saveButton, selectAddressButton, addRoomButton;
    private TextView addressTextView;
    private EditText nameEditText, phoneEditText, roomTypeEditText, priceEditText, capacityEditText;
    private RecyclerView roomRecyclerView;
    private RoomAdapter roomAdapter;

    private HotelViewModel hotelViewModel;
    private RoomViewModel roomViewModel;
    private String hotelId;
    private Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hotel);

        hotelViewModel = new HotelViewModel();
        roomViewModel = new RoomViewModel();

        nameEditText = findViewById(R.id.editNameEditText);
        phoneEditText = findViewById(R.id.editPhoneEditText);
        addressTextView = findViewById(R.id.editAddressTextView);
        hotelImageView = findViewById(R.id.editHotelImageView);
        selectImageButton = findViewById(R.id.editUploadImageButton);
        saveButton = findViewById(R.id.editSaveButton);
        selectAddressButton = findViewById(R.id.editSelectAddressButton);
        addRoomButton = findViewById(R.id.addRoomButton);

        roomTypeEditText = findViewById(R.id.roomTypeEditText);
        priceEditText = findViewById(R.id.priceEditText);
        capacityEditText = findViewById(R.id.capacityEditText);

        roomRecyclerView = findViewById(R.id.editRoomRecyclerView);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomAdapter = new RoomAdapter(this, roomViewModel.getRoomList());
        roomRecyclerView.setAdapter(roomAdapter);

        hotelId = getIntent().getStringExtra("HOTEL_ID");

        loadHotelDetails();

        selectImageButton.setOnClickListener(v -> openImagePicker());
        selectAddressButton.setOnClickListener(v -> openAutocomplete());
        addRoomButton.setOnClickListener(v -> addRoom());
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void loadHotelDetails() {
        hotelViewModel.getHotelById(hotelId).observe(this, hotel -> {
            if (hotel != null) {
                this.hotel = hotel;
                nameEditText.setText(hotel.getName());
                phoneEditText.setText(hotel.getPhoneNumber());
                addressTextView.setText(hotel.getAddress());

                if (hotel.getImageUrl() != null && !hotel.getImageUrl().isEmpty()) {
                    Glide.with(this).load(hotel.getImageUrl()).into(hotelImageView);
                }

                roomViewModel.getRoomsByHotel(hotelId).observe(this, rooms -> {
                    if (rooms != null) {
                        roomViewModel.getRoomList().addAll(rooms);
                        roomAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void addRoom() {
        String roomType = roomTypeEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();
        String capacityStr = capacityEditText.getText().toString();

        if (!roomType.isEmpty() && !priceStr.isEmpty() && !capacityStr.isEmpty()) {
            double price = Double.parseDouble(priceStr);
            int capacity = Integer.parseInt(capacityStr);

            // Create a Room object but do not save it yet
            Room room = new Room(roomViewModel.getUID(), roomType, price, hotelId, capacity);

            // Add the room to the RoomAdapter for display
            roomViewModel.getRoomList().add(room);
            roomAdapter.notifyItemInserted(roomViewModel.getRoomList().size() - 1);

            // Clear input fields
            roomTypeEditText.setText("");
            priceEditText.setText("");
            capacityEditText.setText("");
        } else {
            Toast.makeText(this, "All fields are required to add a room", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveChanges() {
        // Update hotel details
        hotel.setName(nameEditText.getText().toString());
        hotel.setPhoneNumber(phoneEditText.getText().toString());
        hotel.setAddress(addressTextView.getText().toString());

        if (imageUri != null) {
            uploadImage(hotelId);
        }

        // Get the updated room list
        List<String> updatedRoomIdList = new ArrayList<>();
        for (Room room : roomViewModel.getRoomList()) {
            updatedRoomIdList.add(room.getId());

            // Check if the room is new in the hotel current room id list
            if (!hotel.getRoomIdList().contains(room.getId())) {
                roomViewModel.addRoom(room);
            }
        }

        // Compare with the existing room list
        List<String> existingRoomIdList = hotel.getRoomIdList() != null ? hotel.getRoomIdList() : new ArrayList<>();

        // Find rooms to remove
        List<String> roomsToRemove = new ArrayList<>(existingRoomIdList);
        roomsToRemove.removeAll(updatedRoomIdList);

        // Remove old rooms from the collection
        for (String roomId : roomsToRemove) {
            roomViewModel.deleteRoom(roomId); // Assume this method removes a room by its ID
        }

        // Update the hotel's room list
        hotel.setRoomIdList(updatedRoomIdList);

        // Save the updated hotel
        hotelViewModel.updateHotel(hotel);
        Toast.makeText(this, "Hotel updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }



    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, 1001);
    }

    private void uploadImage(String hotelId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("hotel_images/" + hotelId + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            hotel.setImageUrl(uri.toString());
                            hotelViewModel.updateHotel(hotel);
                            Toast.makeText(this, "Image updated successfully", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }


    private void openAutocomplete() {
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))
                .build(this);
        startActivityForResult(intent, 1);
    }

}

