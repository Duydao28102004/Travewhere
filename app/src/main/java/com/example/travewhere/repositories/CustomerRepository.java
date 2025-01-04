package com.example.travewhere.repositories;

import android.util.Log;

import com.example.travewhere.models.Customer;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CustomerRepository {
    private final CollectionReference customersCollection;
    private static final String TAG = "CustomerRepository";

    public CustomerRepository() {
        customersCollection = FirebaseFirestore.getInstance().collection("customers");
        Log.d(TAG, "CustomerRepository initialized with Firestore");
    }

    // CREATE: Add a new customer
    public Task<Void> addCustomer(Customer customer) {
        Log.d(TAG, "addCustomer() called");
        if (customer.getUid() == null || customer.getUid().isEmpty()) {
            String uniqueId = customersCollection.document().getId();
            customer.setUid(uniqueId);
            Log.d(TAG, "Generated unique ID for customer: " + uniqueId);
        }
        return customersCollection.document(customer.getUid()).set(customer)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Customer added successfully: " + customer.getUid()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add customer: " + e.getMessage()));
    }

    // READ: Fetch a specific customer by ID
    public Task<Customer> getCustomerById(String customerId) {
        Log.d(TAG, "getCustomerById() called with ID: " + customerId);
        if (customerId == null || customerId.isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        return customersCollection.document(customerId).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return task.getResult().toObject(Customer.class);
                    } else {
                        throw new Exception("Customer not found: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    // UPDATE: Update a customer's details
    public Task<Void> updateCustomer(Customer customer) {
        Log.d(TAG, "updateCustomer() called for ID: " + customer.getUid());
        if (customer.getUid() == null || customer.getUid().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        return customersCollection.document(customer.getUid()).set(customer)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Customer updated successfully: " + customer.getUid()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update customer: " + e.getMessage()));
    }

    // DELETE: Remove a customer by ID
    public Task<Void> deleteCustomer(String customerId) {
        Log.d(TAG, "deleteCustomer() called for ID: " + customerId);
        if (customerId == null || customerId.isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        return customersCollection.document(customerId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Customer deleted successfully: " + customerId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete customer: " + e.getMessage()));
    }
}
