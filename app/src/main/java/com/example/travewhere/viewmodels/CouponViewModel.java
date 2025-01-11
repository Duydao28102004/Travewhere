package com.example.travewhere.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travewhere.models.Coupon;
import com.example.travewhere.repositories.CouponRepository;

import java.util.ArrayList;
import java.util.List;

public class CouponViewModel extends ViewModel {
    private final CouponRepository couponRepository;
    private final MutableLiveData<List<Coupon>> couponListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Coupon> couponLiveData = new MutableLiveData<>();
    private List<Coupon> coupons = new ArrayList<>();

    public CouponViewModel() {
        couponRepository = new CouponRepository();
    }

    public LiveData<List<Coupon>> getAllCoupons() {
        couponRepository.getAllCoupons().addOnSuccessListener(coupons -> {
            couponListLiveData.postValue(coupons);
        }).addOnFailureListener(e -> {
            Log.d("CouponViewModel", "Error fetching coupons: " + e.getMessage());
        });
        return couponListLiveData;
    }

    public LiveData<List<Coupon>> getCouponsByCode(String code) {
        MutableLiveData<List<Coupon>> filteredCouponsLiveData = new MutableLiveData<>();
        couponRepository.getAllCoupons() // Fetch all coupons
                .addOnSuccessListener(coupons -> {
                    List<Coupon> filteredCoupons = new ArrayList<>();
                    for (Coupon coupon : coupons) {
                        if (coupon.getCode() != null && coupon.getCode().toLowerCase().contains(code.toLowerCase())) {
                            filteredCoupons.add(coupon);
                        }
                    }
                    filteredCouponsLiveData.postValue(filteredCoupons);
                })
                .addOnFailureListener(e -> {
                    Log.d("CouponViewModel", "Error fetching coupons: " + e.getMessage());
                    filteredCouponsLiveData.postValue(new ArrayList<>()); // Return an empty list on failure
                });
        return filteredCouponsLiveData;
    }


    public LiveData<Coupon> getCouponById(String couponId) {
        couponRepository.getCouponById(couponId).addOnSuccessListener(coupon -> {
            couponLiveData.postValue(coupon);
        }).addOnFailureListener(e -> {
            Log.d("CouponViewModel", "Error fetching coupon by ID: " + e.getMessage());
        });
        return couponLiveData;
    }

    public void addCoupon(Coupon coupon) {
        couponRepository.addCoupon(coupon);
    }

    public void updateCoupon(Coupon coupon) {
        couponRepository.updateCoupon(coupon);
    }

    public void deleteCoupon(String couponId) {
        couponRepository.deleteCoupon(couponId);
    }
}
