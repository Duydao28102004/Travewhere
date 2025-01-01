package com.example.travewhere;

import android.content.Context;
import android.widget.Toast;

import com.example.travewhere.models.Customer;
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
        // Create hashmap to store data and key first
        Map<String, Object> customer = new HashMap<>();
        customer.put("name", name);
        customer.put("email", email);
        customer.put("phone", phone);

        // Add data to Firestore
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
}
