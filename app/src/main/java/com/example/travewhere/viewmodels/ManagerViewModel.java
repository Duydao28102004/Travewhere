package com.example.travewhere.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travewhere.models.Manager;
import com.example.travewhere.repositories.ManagerRepository;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class ManagerViewModel extends ViewModel {
    private final ManagerRepository managerRepository;
    private final MutableLiveData<Manager> managerLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Manager>> managerListLiveData = new MutableLiveData<>();

    public ManagerViewModel() {
        managerRepository = new ManagerRepository();
    }

    // Add a new manager
    public void addManager(Manager manager) {
        managerRepository.addManager(manager)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    // Fetch a specific manager by ID
    public LiveData<Manager> getManagerById(String managerId) {
        managerRepository.getManagerById(managerId).addOnSuccessListener(manager -> {
            managerLiveData.postValue(manager);
        }).addOnFailureListener(e -> {
            // Handle error
        });
        return managerLiveData;
    }

    // Update an existing manager
    public void updateManager(Manager manager) {
        managerRepository.updateManager(manager)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    // Delete a manager by ID
    public void deleteManager(String managerId) {
        managerRepository.deleteManager(managerId)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
}
