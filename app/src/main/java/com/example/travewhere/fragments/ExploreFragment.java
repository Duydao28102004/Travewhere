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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.travewhere.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMapInstance;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;

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
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        // Initialize the custom relocation button
        ImageButton customRelocationButton = view.findViewById(R.id.btnRelocate);
        customRelocationButton.setOnClickListener(v -> relocateToCurrentLocation());

        return view;
    }

    // Callback method for when the Google Map is ready
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMapInstance = googleMap;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
            moveToCurrentLocation();
        } else {
            requestLocationPermission();
        }
    }

    // Helper method to request location permission
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    // Helper method to enable the user's location on the map
    private void enableMyLocation() {
        if (googleMapInstance != null && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMapInstance.setMyLocationEnabled(true);
        }
    }

    // Helper method to move the camera to the current location
    private void moveToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        // Get the last known location of the device
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && googleMapInstance != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        });
    }

    // Helper method to relocate the camera to the current location
    private void relocateToCurrentLocation() {
        moveToCurrentLocation();
    }
}