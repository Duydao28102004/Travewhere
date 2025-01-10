package com.example.travewhere.repositories;

import android.util.Log;

import com.example.travewhere.models.Coupon;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CouponRepository {
    private final CollectionReference couponsCollection;
    private static final String TAG = "CouponRepository";

    public CouponRepository() {
        couponsCollection = FirebaseFirestore.getInstance().collection("coupons");
        Log.d(TAG, "CouponRepository initialized with Firestore");
    }

    public String getUID() {
        return couponsCollection.document().getId();
    }

    public Task<Void> addCoupon(Coupon coupon) {
        Log.d(TAG, "addCoupon() called");
        if (coupon.getId() == null || coupon.getId().isEmpty()) {
            String uniqueId = couponsCollection.document().getId();
            coupon.setId(uniqueId);
            Log.d(TAG, "Generated unique ID for coupon: " + uniqueId);
        }
        return couponsCollection.document(coupon.getId()).set(coupon)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Coupon added successfully: " + coupon.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add coupon: " + e.getMessage()));
    }

    public Task<List<Coupon>> getCouponsByCode(String code) {
        Log.d(TAG, "getCouponsByCode() called with code: " + code);
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be null or empty");
        }

        String normalizedCode = code.trim().replaceAll("\\s+", "").toLowerCase();

        return couponsCollection.get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Coupon> matchingCoupons = new ArrayList<>();
                        task.getResult().forEach(document -> {
                            Coupon coupon = document.toObject(Coupon.class);
                            if (coupon.getCode() != null &&
                                    coupon.getCode().replaceAll("\\s+", "").equalsIgnoreCase(normalizedCode)) {
                                matchingCoupons.add(coupon);
                            }
                        });
                        return matchingCoupons;
                    } else {
                        throw new Exception("Failed to fetch coupons by code: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }


    public Task<Coupon> getCouponById(String couponId) {
        Log.d(TAG, "getCouponById() called with ID: " + couponId);
        if (couponId == null || couponId.isEmpty()) {
            throw new IllegalArgumentException("Coupon ID cannot be null or empty");
        }
        return couponsCollection.document(couponId).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return task.getResult().toObject(Coupon.class);
                    } else {
                        throw new Exception("Coupon not found: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    public Task<List<Coupon>> getAllCoupons() {
        Log.d(TAG, "getAllCoupons() called");
        return couponsCollection.get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Coupon> coupons = new ArrayList<>();
                        task.getResult().forEach(document -> {
                            Coupon coupon = document.toObject(Coupon.class);
                            coupons.add(coupon);
                        });
                        return coupons;
                    } else {
                        throw new Exception("Failed to fetch coupons: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    public Task<Void> updateCoupon(Coupon coupon) {
        Log.d(TAG, "updateCoupon() called for ID: " + coupon.getId());
        if (coupon.getId() == null || coupon.getId().isEmpty()) {
            throw new IllegalArgumentException("Coupon ID cannot be null or empty");
        }
        return couponsCollection.document(coupon.getId()).set(coupon)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Coupon updated successfully: " + coupon.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update coupon: " + e.getMessage()));
    }

    public Task<Void> deleteCoupon(String couponId) {
        Log.d(TAG, "deleteCoupon() called for ID: " + couponId);
        if (couponId == null || couponId.isEmpty()) {
            throw new IllegalArgumentException("Coupon ID cannot be null or empty");
        }
        return couponsCollection.document(couponId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Coupon deleted successfully: " + couponId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete coupon: " + e.getMessage()));
    }
}
