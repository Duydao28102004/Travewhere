package com.example.travewhere;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.adapters.CouponAdapter;
import com.example.travewhere.models.Coupon;
import com.example.travewhere.viewmodels.CouponViewModel;

import java.util.ArrayList;
import java.util.List;

public class CouponActivity extends AppCompatActivity {

    private CouponAdapter couponAdapter;
    private CouponViewModel couponViewModel;
    private RelativeLayout btnBackLayout;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        btnBackLayout = findViewById(R.id.btnBackLayout);
        searchView = findViewById(R.id.searchView);

        RecyclerView recyclerView = findViewById(R.id.couponsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        couponAdapter = new CouponAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(couponAdapter);

        couponViewModel = new ViewModelProvider(this).get(CouponViewModel.class);

        couponViewModel.getAllCoupons().observe(this, this::updateCoupons);
        btnBackLayout.setOnClickListener(v -> finish());

        // Set up SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCoupons(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    couponViewModel.getAllCoupons().observe(CouponActivity.this, coupons -> {
                        if (coupons != null) {
                            couponAdapter.setCoupons(coupons);
                        }
                    });
                } else {
                    searchCoupons(newText);
                }
                return true;
            }
        });
    }

    private void updateCoupons(List<Coupon> coupons) {
        if (coupons != null && !coupons.isEmpty()) {
            couponAdapter.setCoupons(coupons);
        }
    }

    private void searchCoupons(String query) {
        couponViewModel.getCouponsByCode(query).observe(this, filteredCoupons -> {
            if (filteredCoupons != null && !filteredCoupons.isEmpty()) {
                couponAdapter.setCoupons(filteredCoupons);
            } else {
                couponAdapter.setCoupons(new ArrayList<>());
            }
        });
    }
}
