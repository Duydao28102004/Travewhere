package com.example.travewhere.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.travewhere.HotelDetailActivity;
import com.example.travewhere.R;
import com.example.travewhere.adapters.HotelSearchAdapter;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMapInstance;
    private HotelViewModel hotelViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private RecyclerView searchResultsRecyclerView;
    private HotelSearchAdapter hotelSearchAdapter;
    private FrameLayout hotelDetailsFrameLayout;
    private TextView hotelNameTextView, hotelAddressTextView;
    private Button btnViewDetails;
    private ImageView hotelImageView;

    private final List<Hotel> allHotels = new ArrayList<>();
    private final List<Hotel> searchResults = new ArrayList<>();
    private final HashMap<String, Hotel> markerHotelMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        enableMyLocation();
                        moveToCurrentLocation();
                        fetchAndDisplayHotels();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        setupUI(view);
        initializeMap();
        initializeHotelViewModel();
        return view;
    }

    private void setupUI(View view) {
        // RecyclerView for hotel search results
        searchResultsRecyclerView = view.findViewById(R.id.searchResults);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        SearchView searchView = view.findViewById(R.id.searchView); // Reference to SearchView
        hotelSearchAdapter = new HotelSearchAdapter(searchResults, hotel -> {
            onHotelSelected(hotel);

            // Set the SearchView text to the selected hotel's name
            searchView.setQuery(hotel.getName(), false);
        });
        searchResultsRecyclerView.setAdapter(hotelSearchAdapter);

        // FrameLayout for hotel details
        hotelDetailsFrameLayout = view.findViewById(R.id.hotelDetailsFrameLayout);
        hotelNameTextView = view.findViewById(R.id.hotelName);
        hotelAddressTextView = view.findViewById(R.id.hotelAddress);
        hotelImageView = view.findViewById(R.id.hotelImage);
        Button btnDirections = view.findViewById(R.id.btnDirections);
        btnViewDetails = view.findViewById(R.id.btnViewDetails);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> hotelDetailsFrameLayout.setVisibility(View.GONE));
        btnDirections.setOnClickListener(v -> navigateToHotel());

        // Configure the SearchView
        searchView.setQueryHint("Search hotels by name or address");
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterHotels(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterHotels(newText);
                return true;
            }
        });

        // Relocation button to move camera to current location
        view.findViewById(R.id.btnRelocate).setOnClickListener(v -> relocateToCurrentLocation());
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
    }

    private void initializeHotelViewModel() {
        hotelViewModel = new ViewModelProvider(this).get(HotelViewModel.class);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMapInstance = googleMap;
        googleMapInstance.getUiSettings().setMyLocationButtonEnabled(false);

        googleMapInstance.setOnMarkerClickListener(marker -> {
            Hotel hotel = markerHotelMap.get(marker.getId());
            if (hotel != null) showHotelDetails(hotel);
            return true;
        });

        googleMapInstance.setOnCameraIdleListener(() -> {
            LatLngBounds bounds = googleMapInstance.getProjection().getVisibleRegion().latLngBounds;
            String currentHotelName = hotelNameTextView.getText().toString();

            boolean isInBounds = allHotels.stream()
                    .anyMatch(h -> h.getName().equals(currentHotelName) &&
                            bounds.contains(new LatLng(h.getLatitude(), h.getLongitude())));

            if (!isInBounds) hotelDetailsFrameLayout.setVisibility(View.GONE);
        });

        enableMyLocation();
        moveToCurrentLocation();
        fetchAndDisplayHotels();
    }

    private void fetchAndDisplayHotels() {
        hotelViewModel.getAllHotels().observe(getViewLifecycleOwner(), hotels -> {
            if (hotels != null) {
                allHotels.clear();
                allHotels.addAll(hotels);
                displayHotelsOnMap(hotels);
            }
        });
    }

    private void displayHotelsOnMap(List<Hotel> hotels) {
        markerHotelMap.clear();
        googleMapInstance.clear();
        for (Hotel hotel : hotels) {
            LatLng location = new LatLng(hotel.getLatitude(), hotel.getLongitude());
            Marker marker = googleMapInstance.addMarker(new MarkerOptions().position(location));
            if (marker != null) markerHotelMap.put(marker.getId(), hotel);
        }
    }

    private void showHotelDetails(Hotel hotel) {
        hotelNameTextView.setText(hotel.getName());
        hotelAddressTextView.setText(hotel.getAddress());
        Glide.with(requireContext()).load(hotel.getImageUrl()).into(hotelImageView);
        hotelDetailsFrameLayout.setVisibility(View.VISIBLE);
        btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HotelDetailActivity.class);
            intent.putExtra("HOTEL_ID", hotel.getId());
            startActivity(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterHotels(String query) {
        searchResults.clear();
        if (query == null || query.trim().isEmpty()) {
            searchResultsRecyclerView.setVisibility(View.GONE);
            return;
        }

        for (Hotel hotel : allHotels) {
            if (hotel.getName().toLowerCase().contains(query.toLowerCase()) ||
                    hotel.getAddress().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(hotel);
            }
        }

        if (searchResults.isEmpty()) {
            searchResultsRecyclerView.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "No hotels found matching your search.", Toast.LENGTH_SHORT).show();
        } else {
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
            hotelSearchAdapter.notifyDataSetChanged();
        }
    }

    private void navigateToHotel() {
        String currentHotelName = hotelNameTextView.getText().toString();
        for (Hotel hotel : allHotels) {
            if (hotel.getName().equals(currentHotelName) && googleMapInstance.getMyLocation() != null) {
                LatLng destination = new LatLng(hotel.getLatitude(), hotel.getLongitude());
                LatLng origin = new LatLng(googleMapInstance.getMyLocation().getLatitude(),
                        googleMapInstance.getMyLocation().getLongitude());
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + origin.latitude + "," + origin.longitude +
                                "&daddr=" + destination.latitude + "," + destination.longitude));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
                return;
            }
        }
        Toast.makeText(requireContext(), "Unable to fetch current location or hotel data.", Toast.LENGTH_SHORT).show();
    }

    private void onHotelSelected(Hotel hotel) {
        // Move the map camera to the selected hotel's location
        LatLng hotelLocation = new LatLng(hotel.getLatitude(), hotel.getLongitude());
        googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(hotelLocation, 15));

        // Show the hotel details when user selects a hotel on the matching results of the search
        showHotelDetails(hotel);

        // Retrieve the existing marker from the markerHotelMap
        Marker marker = null;
        for (String markerId : markerHotelMap.keySet()) {
            if (Objects.equals(markerHotelMap.get(markerId), hotel)) {
                googleMapInstance.getProjection().getVisibleRegion().latLngBounds.contains(new LatLng(
                        hotel.getLatitude(), hotel.getLongitude()));
                break;
            }
        }

        // Update the SearchView with the selected hotel's name
        SearchView searchView = requireView().findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setQuery(hotel.getName(), false); // Set the selected hotel name
        }

        // Hide the RecyclerView for search results
        searchResultsRecyclerView.setVisibility(View.GONE);
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMapInstance.setMyLocationEnabled(true);
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void moveToCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } else {
                Toast.makeText(requireContext(), "Unable to fetch current location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void relocateToCurrentLocation() {
        moveToCurrentLocation();
    }
}
