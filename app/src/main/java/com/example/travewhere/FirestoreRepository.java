package com.example.travewhere;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.travewhere.models.Customer;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.models.Manager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreRepository {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;

    public FirestoreRepository(Context context) {
        this.context = context;
    }

    public void createCustomer(String id, String name, String email, String phone) {
        // Check if customer ID already exists
        db.collection("customers").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Toast.makeText(context, "Customer ID already exists", Toast.LENGTH_SHORT).show();
            } else {
                // Create customer class to store data
                Customer customer = new Customer(id, name, email, phone);

                // Create customer in Firestore
                db.collection("customers")
                        .document(id)
                        .set(customer)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Customer added successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error adding customer", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    public void createManager(String id, String name, String email, String phone) {
        // Check if manager ID already exists
        db.collection("managers").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Toast.makeText(context, "Manager ID already exists", Toast.LENGTH_SHORT).show();
            } else {
                // Create manager class to store data
                Manager manager = new Manager(id, name, email, phone);

                // Create manager in Firestore
                db.collection("managers")
                        .document(id)
                        .set(manager)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Manager added successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error adding manager", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public void getUserNameById(String userId, OnUserNameFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("customers")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("name")) {
                        listener.onUserNameFetched(documentSnapshot.getString("name"));
                    } else {
                        listener.onUserNameFetched("Unknown User");
                    }
                })
                .addOnFailureListener(e -> listener.onUserNameFetched("Unknown User"));
    }

    public interface OnUserNameFetchedListener {
        void onUserNameFetched(String userName);
    }

    public void calculateAverageRating(String hotelId, OnAverageRatingCalculatedListener listener) {
        if (hotelId == null || hotelId.isEmpty()) {
            listener.onAverageRatingCalculated(0.0f);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reviews")
                .whereEqualTo("hotelId", hotelId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        listener.onAverageRatingCalculated(0.0f);
                        return;
                    }

                    float totalRating = 0.0f;
                    int reviewCount = queryDocumentSnapshots.size();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        if (document.contains("rating")) {
                            totalRating += document.getDouble("rating").floatValue();
                        }
                    }

                    float averageRating = totalRating / reviewCount;
                    listener.onAverageRatingCalculated(averageRating);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreRepository", "Error calculating average rating", e);
                    listener.onAverageRatingCalculated(0.0f);
                });
    }

    public interface OnAverageRatingCalculatedListener {
        void onAverageRatingCalculated(float averageRating);
    }

}
