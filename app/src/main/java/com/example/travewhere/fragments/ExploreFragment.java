package com.example.travewhere.fragments;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.travewhere.R;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Objects;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private ImageButton customRelocationButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // Initialize the map fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the SearchView
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setIconified(false); // Expand the SearchView
        searchView.requestFocus(); // Request focus on the SearchView

        // Initialize the custom relocation button
        customRelocationButton = view.findViewById(R.id.btnRelocate);
        customRelocationButton.setOnClickListener(v -> {
            // Handle the relocation button
        });

        return view;
    }

    @Override
    public void onMapReady(@NonNull com.google.android.gms.maps.GoogleMap googleMap) {
        // Handle the map when it's ready
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapFragment != null) {
            mapFragment.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapFragment != null) {
            mapFragment.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapFragment != null) {
            mapFragment.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapFragment != null) {
            mapFragment.onLowMemory();
        }
    }
}