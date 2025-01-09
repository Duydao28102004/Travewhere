package com.example.travewhere;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.travewhere.models.Customer;
import com.example.travewhere.models.Manager;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.repositories.CustomerRepository;
import com.example.travewhere.repositories.ManagerRepository;
import com.example.travewhere.viewmodels.CustomerViewModel;
import com.example.travewhere.viewmodels.ManagerViewModel;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.units.qual.C;

public class AuthenticationActivity extends AppCompatActivity {

    private LinearLayout loginLayout, signupLayout;
    private Button loginButton, signupButton;
    private TextView switchToSignup, switchToLogin;
    private EditText loginEmail, loginPassword;
    private EditText signupEmail, signupPassword, fullName, phoneNumber;
    private CheckBox checkboxAccommodationOwner, checkboxTermsAndConditions;

    private AuthenticationRepository authRepository;
    private FirestoreRepository firestoreRepository;
    private CustomerViewModel customerViewModel;
    private ManagerViewModel managerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Hide the top bar
        getSupportActionBar().hide();

        // Initialize the repository
        authRepository = new AuthenticationRepository();
        firestoreRepository = new FirestoreRepository(this);
        customerViewModel = new CustomerViewModel();
        managerViewModel = new ManagerViewModel();

        // Link UI components
        loginLayout = findViewById(R.id.loginLayout);
        signupLayout = findViewById(R.id.signupLayout);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        switchToLogin = findViewById(R.id.switchToLogin);
        switchToSignup = findViewById(R.id.switchToSignup);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        fullName = findViewById(R.id.fullName);
        phoneNumber = findViewById(R.id.phoneNumber);
        checkboxAccommodationOwner = findViewById(R.id.checkboxAccommodationOwner);
        checkboxTermsAndConditions = findViewById(R.id.checkboxTermsAndConditions);

        // Set up UI interactions
        setupSwitchListeners();
        setupLoginListener();
        setupSignupListener();
    }

    private void setupSwitchListeners() {
        switchToLogin.setOnClickListener(v -> {
            loginLayout.setVisibility(View.VISIBLE);
            signupLayout.setVisibility(View.GONE);
        });

        switchToSignup.setOnClickListener(v -> {
            loginLayout.setVisibility(View.GONE);
            signupLayout.setVisibility(View.VISIBLE);
        });
    }

    private void setupLoginListener() {
        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the login method from the repository
            authRepository.login(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = authRepository.getCurrentUser();
                            if (user == null) {
                                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String userId = user.getUid();

                            // Check if user is a Customer
                            customerViewModel.getCustomerById(userId).observe(this, customer -> {
                                if (customer != null) {
                                    // Redirect to Customer's MainActivity
                                    Intent intent = new Intent(this, MainActivity.class);
                                    intent.putExtra("customer", customer);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Check if user is a Manager
                                    managerViewModel.getManagerById(userId).observe(this, manager -> {
                                        if (manager != null) {
                                            // Redirect to ManagerActivity
                                            Intent intent = new Intent(this, ManagerActivity.class);
                                            intent.putExtra("manager", manager);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // If user is neither a Customer nor a Manager
                                            Toast.makeText(this, "Unknown user type", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }


    private void setupSignupListener() {
        signupButton.setOnClickListener(v -> {
            String name = fullName.getText().toString().trim();
            String email = signupEmail.getText().toString().trim();
            String password = signupPassword.getText().toString().trim();
            String phone = phoneNumber.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidVietnamesePhone(phone)) {
                Toast.makeText(this, "Invalid Vietnamese phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!checkboxTermsAndConditions.isChecked()) {
                Toast.makeText(this, "You must agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the signup method from the repository
            authRepository.signup(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // save customer data to Firestore
                            FirebaseUser user = authRepository.getCurrentUser();

                            //determine if the user is an accommodation owner
                            if (checkboxAccommodationOwner.isChecked()) {
                                managerViewModel.addManager(new Manager(user.getUid(), name, email, phone));
                            } else {
                                customerViewModel.addCustomer(new Customer(user.getUid(), name, email, phone));
                            }

                            Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show();
                            // Navigate to the main activity or dashboard
                            loginLayout.setVisibility(View.VISIBLE);
                            signupLayout.setVisibility(View.GONE);

                            clearSignupFields(fullName, signupEmail, signupPassword,
                                    phoneNumber, checkboxTermsAndConditions);
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Signup failed";
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private boolean isValidVietnamesePhone(String phoneNumber) {
        String vietnamesePhonePattern = "^(03|05|07|08|09)\\d{8}$";
        return !TextUtils.isEmpty(phoneNumber) && phoneNumber.matches(vietnamesePhonePattern);
    }


    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void clearSignupFields(EditText fullName, EditText email, EditText password,
                                         EditText phoneNumber, CheckBox termsAndConditionsCheck) {
        fullName.setText("");
        email.setText("");
        password.setText("");
        phoneNumber.setText("");
        termsAndConditionsCheck.setChecked(false);
    }
}