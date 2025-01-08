package com.example.travewhere;

import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        btnBackLayout = findViewById(R.id.btnBackLayout);

        RecyclerView recyclerView = findViewById(R.id.couponsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        couponAdapter = new CouponAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(couponAdapter);

        couponViewModel = new ViewModelProvider(this).get(CouponViewModel.class);

        couponViewModel.getAllCoupons().observe(this, this::updateCoupons);
        btnBackLayout.setOnClickListener(v -> finish());
    }

    private void updateCoupons(List<Coupon> coupons) {
        if (coupons != null && !coupons.isEmpty()) {
            couponAdapter.setCoupons(coupons);
        }
    }
}
