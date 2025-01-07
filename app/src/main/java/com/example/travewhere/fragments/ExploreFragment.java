package com.example.travewhere.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.travewhere.R;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMapInstance;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private HotelViewModel hotelViewModel;

    private final List<Marker> hotelMarkers = new ArrayList<>(); // To store hotel markers for search functionality

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the permission request launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        enableMyLocation();
                        moveToCurrentLocation();
                        fetchAndDisplayHotels();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // Initialize the Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Initialize the SearchView
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.requestFocus();

        // Handle search queries
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterMarkers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMarkers(newText);
                return true;
            }
        });

        // Initialize the custom relocation button
        ImageButton customRelocationButton = view.findViewById(R.id.btnRelocate);
        customRelocationButton.setOnClickListener(v -> relocateToCurrentLocation());

        // Initialize the HotelViewModel
        hotelViewModel = new ViewModelProvider(this).get(HotelViewModel.class);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Called when the map is ready to be used
        this.googleMapInstance = googleMap;

        googleMapInstance.getUiSettings().setMyLocationButtonEnabled(false);
        googleMapInstance.getUiSettings().setCompassEnabled(true);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
            moveToCurrentLocation();
            fetchAndDisplayHotels();
        } else {
            requestLocationPermission();
        }
    }

    private void fetchAndDisplayHotels() {
        // Fetch and display hotel markers on the map
        hotelViewModel.getAllHotels().observe(getViewLifecycleOwner(), hotels -> {
            if (hotels != null && googleMapInstance != null) {
                displayHotelsOnMap(hotels);
            }
        });
    }

    private void displayHotelsOnMap(List<Hotel> hotels) {
        // Add markers for all hotels to the map
        hotelMarkers.clear(); // Clear any existing markers

        for (Hotel hotel : hotels) {
            // Check if latitude and longitude are within valid ranges
            if (hotel.getLatitude() >= -90 && hotel.getLatitude() <= 90 &&
                    hotel.getLongitude() >= -180 && hotel.getLongitude() <= 180) {
                LatLng hotelLocation = new LatLng(hotel.getLatitude(), hotel.getLongitude());
                Marker marker = googleMapInstance.addMarker(new MarkerOptions()
                        .position(hotelLocation)
                        .title(hotel.getName())
                        .snippet(hotel.getAddress()));
                if (marker != null) {
                    hotelMarkers.add(marker); // Add the marker to the list
                }
            }
        }
    }

    private void filterMarkers(String query) {
        // Filter hotel markers based on the search query
        boolean markerFound = false;

        if (query == null || query.trim().isEmpty()) {
            // If the query is empty, reset to current location
            moveToCurrentLocation();
            return;
        }

        for (Marker marker : hotelMarkers) {
            if (marker.getTitle() != null && marker.getSnippet() != null) {
                String title = marker.getTitle().toLowerCase();
                String address = marker.getSnippet().toLowerCase();
                query = query.toLowerCase();

                // Check if the query matches the hotel name or address
                if (title.contains(query) || address.contains(query)) {
                    marker.setVisible(true); // Show matched marker
                    marker.showInfoWindow(); // Display info window
                    if (!markerFound) { // Move the camera only to the first match
                        googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                        markerFound = true;
                    }
                } else {
                    marker.setVisible(false); // Hide unmatched markers
                }
            }
        }

        // If no markers match the query, move to the current location
        if (!markerFound) {
            Toast.makeText(requireContext(), "No hotels found matching your search.", Toast.LENGTH_SHORT).show();
            moveToCurrentLocation();
        }
    }

    private void requestLocationPermission() {
        // Request location permission from the user
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void enableMyLocation() {
        // Enable the user's current location on the map
        if (googleMapInstance != null && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMapInstance.setMyLocationEnabled(true);
        }
    }

    private void moveToCurrentLocation() {
        // Move the map camera to the user's current location
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && googleMapInstance != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } else {
                // Optional: Handle cases where location is unavailable
                Toast.makeText(requireContext(), "Unable to fetch current location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void relocateToCurrentLocation() {
        // Relocate the map camera to the current location
        moveToCurrentLocation();
    }
}