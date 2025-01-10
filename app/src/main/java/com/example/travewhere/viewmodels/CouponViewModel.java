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
        couponRepository.getCouponsByCode(code)
                .addOnSuccessListener(filteredCouponsLiveData::postValue)
                .addOnFailureListener(e -> {
                    Log.d("CouponViewModel", "Error fetching coupons by code: " + e.getMessage());
                    filteredCouponsLiveData.postValue(new ArrayList<>());
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
