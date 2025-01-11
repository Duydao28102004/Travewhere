package com.example.travewhere.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.travewhere.CouponActivity;
import com.example.travewhere.BookingHistoryActivity;
import com.example.travewhere.HotelDetailActivity;
import com.example.travewhere.LoyaltyActivity;
import com.example.travewhere.R;
import com.example.travewhere.adapters.CouponAdapter;
import com.example.travewhere.adapters.HotelAdapter;
import com.example.travewhere.models.Coupon;
import com.example.travewhere.SearchActivity;
import com.example.travewhere.adapters.HotelAdapter;
import com.example.travewhere.models.Customer;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.repositories.HotelRepository;
import com.example.travewhere.viewmodels.CouponViewModel;
import com.example.travewhere.viewmodels.HotelViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomepageFragment extends Fragment {

    private RecyclerView hotelRecyclerView;
    private RecyclerView couponRecyclerView;
    private HotelAdapter hotelAdapter;
    private CouponAdapter couponAdapter;
    private List<Hotel> hotelList;
    private List<Coupon> couponList;
    private HotelRepository hotelRepository;
    private HotelViewModel hotelViewModel = new HotelViewModel();
    private CouponViewModel couponViewModel = new CouponViewModel();
    private Customer currentCustomer;
    private LinearLayout searchBar, bookingHistoryButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the Customer object from the arguments
        if (getArguments() != null) {
            currentCustomer = (Customer) getArguments().getSerializable("customer");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
      
        hotelRecyclerView = view.findViewById(R.id.accommodationRecyclerView);
        searchBar = view.findViewById(R.id.searchBar);
        bookingHistoryButton = view.findViewById(R.id.bookingLinearLayout);

        // Set click listener for LoyaltyActivity button trigger
        view.findViewById(R.id.btnLoyalty).setOnClickListener(v -> {
            if (currentCustomer != null) {
                Intent intent = new Intent(getContext(), LoyaltyActivity.class);
                intent.putExtra("customer", currentCustomer);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Customer data is not available", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout couponsLayout = view.findViewById(R.id.couponLinearLayout);

        couponsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CouponActivity.class);
            startActivity(intent);
        });

        LinearLayout hotelLinearLayout = view.findViewById(R.id.hotelLinearLayout);

        hotelLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });


        hotelRecyclerView = view.findViewById(R.id.accommodationRecyclerView);
        couponRecyclerView = view.findViewById(R.id.couponRecyclerView);
        hotelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        couponRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        hotelList = new ArrayList<>();
        hotelAdapter = new HotelAdapter(this.getContext(), hotelList);
        hotelRecyclerView.setAdapter(hotelAdapter);
        hotelAdapter.setOrientation(true);

        couponList = new ArrayList<>();
        couponAdapter = new CouponAdapter(this.getContext(), couponList);
        couponRecyclerView.setAdapter(couponAdapter);
        couponAdapter.setOrientation(true);
        fetchCoupons();
      
        // Trigger search activity
        searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });

        // Trigger booking history activity
        bookingHistoryButton.setOnClickListener(v -> {
            // Handle the booking history button
            Intent intent = new Intent(getContext(), BookingHistoryActivity.class);
            startActivity(intent);
        });

        // Fetch all hotels and update the RecyclerView
        fetchHotels();

        return view;
    }

    private void fetchHotels() {
        hotelViewModel.getAllHotels().observe(getViewLifecycleOwner(), allHotels -> {
            if (allHotels != null && !allHotels.isEmpty()) {
                hotelAdapter.prefetch(() -> {
                    hotelAdapter.updateHotelList(allHotels);
                });
            } else {
                Toast.makeText(getContext(), "No hotels found", Toast.LENGTH_SHORT).show();
                Log.d("HomepageFragment", "No hotels available to display.");
            }
        });
    }

    private void fetchCoupons() {
        couponViewModel.getAllCoupons().observe(getViewLifecycleOwner(), allCoupons -> {
            if (allCoupons != null && !allCoupons.isEmpty()) {
                couponAdapter.prefetch(() -> {
                    couponAdapter.updateCouponList(allCoupons);
                });
            } else {
                Toast.makeText(getContext(), "No coupons found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchHotels();
        fetchCoupons();
    }
}