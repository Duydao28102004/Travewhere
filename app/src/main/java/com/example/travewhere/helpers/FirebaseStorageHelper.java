package com.example.travewhere.helpers;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseStorageHelper {
    private static final String TAG = "FirebaseStorageHelper";

    // Uploads an image to Firebase Storage and returns its URL
    public static void uploadImage(Uri imageUri, String folderName, String fileName, OnImageUploadListener listener) {
        if (imageUri == null) {
            listener.onFailure("No image URI provided");
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(folderName + "/" + fileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> listener.onSuccess(uri.toString()))
                        .addOnFailureListener(e -> listener.onFailure("Failed to get download URL: " + e.getMessage())))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Image upload failed", e);
                    listener.onFailure("Failed to upload image: " + e.getMessage());
                });
    }

    // Listener interface for upload results
    public interface OnImageUploadListener {
        void onSuccess(String imageUrl);
        void onFailure(String errorMessage);
    }
}
