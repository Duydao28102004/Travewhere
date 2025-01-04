package com.example.travewhere.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travewhere.models.Customer;
import com.example.travewhere.repositories.CustomerRepository;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class CustomerViewModel extends ViewModel {
    private final CustomerRepository customerRepository;
    private final MutableLiveData<Customer> customerLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Customer>> customerListLiveData = new MutableLiveData<>();

    public CustomerViewModel() {
        customerRepository = new CustomerRepository();
    }

    // Add a new customer
    public void addCustomer(Customer customer) {
        customerRepository.addCustomer(customer)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    // Fetch a specific customer by ID
    public LiveData<Customer> getCustomerById(String customerId) {
        customerRepository.getCustomerById(customerId).addOnSuccessListener(customer -> {
            customerLiveData.postValue(customer);
        }).addOnFailureListener(e -> {
            // Handle error
        });
        return customerLiveData;
    }

    // Update an existing customer
    public void updateCustomer(Customer customer) {
        customerRepository.updateCustomer(customer)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    // Delete a customer by ID
    public void deleteCustomer(String customerId) {
        customerRepository.deleteCustomer(customerId)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
}

