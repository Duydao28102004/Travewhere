package com.example.travewhere.repositories;

import android.util.Log;

import com.example.travewhere.models.Manager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManagerRepository {
    private final CollectionReference managersCollection;
    private static final String TAG = "ManagerRepository";

    public ManagerRepository() {
        managersCollection = FirebaseFirestore.getInstance().collection("managers");
        Log.d(TAG, "ManagerRepository initialized with Firestore");
    }

    // CREATE: Add a new manager
    public Task<Void> addManager(Manager manager) {
        Log.d(TAG, "addManager() called");
        if (manager.getUid() == null || manager.getUid().isEmpty()) {
            String uniqueId = managersCollection.document().getId();
            manager.setUid(uniqueId);
            Log.d(TAG, "Generated unique ID for manager: " + uniqueId);
        }
        return managersCollection.document(manager.getUid()).set(manager)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Manager added successfully: " + manager.getUid()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add manager: " + e.getMessage()));
    }

    // READ: Fetch a specific manager by ID
    public Task<Manager> getManagerById(String managerId) {
        Log.d(TAG, "getManagerById() called with ID: " + managerId);
        if (managerId == null || managerId.isEmpty()) {
            throw new IllegalArgumentException("Manager ID cannot be null or empty");
        }
        return managersCollection.document(managerId).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return task.getResult().toObject(Manager.class);
                    } else {
                        throw new Exception("Manager not found: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    // UPDATE: Update a manager's details
    public Task<Void> updateManager(Manager manager) {
        Log.d(TAG, "updateManager() called for ID: " + manager.getUid());
        if (manager.getUid() == null || manager.getUid().isEmpty()) {
            throw new IllegalArgumentException("Manager ID cannot be null or empty");
        }
        return managersCollection.document(manager.getUid()).set(manager)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Manager updated successfully: " + manager.getUid()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update manager: " + e.getMessage()));
    }

    // DELETE: Remove a manager by ID
    public Task<Void> deleteManager(String managerId) {
        Log.d(TAG, "deleteManager() called for ID: " + managerId);
        if (managerId == null || managerId.isEmpty()) {
            throw new IllegalArgumentException("Manager ID cannot be null or empty");
        }
        return managersCollection.document(managerId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Manager deleted successfully: " + managerId))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete manager: " + e.getMessage()));
    }
}
