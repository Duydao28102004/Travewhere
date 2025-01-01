package com.example.travewhere;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AuthenticationActivity extends AppCompatActivity {

    private LinearLayout loginLayout, signupLayout;
    private Button loginButton, signupButton;
    private TextView switchToSignup, switchToLogin;
    private EditText loginEmail, loginPassword;
    private EditText signupEmail, signupPassword, fullName, phoneNumber;
    private CheckBox checkboxAccommodationOwner, checkboxTermsAndConditions;

    private AuthenticationRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Initialize the repository
        authRepository = new AuthenticationRepository();

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
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                            // Navigate to the main activity or dashboard
                            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
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

            if (!checkboxTermsAndConditions.isChecked()) {
                Toast.makeText(this, "You must agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the signup method from the repository
            authRepository.signup(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Save additional user data
                            authRepository.saveUserData(name, email, phone)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show();
                                        // Navigate to the main activity or dashboard
                                        loginLayout.setVisibility(View.VISIBLE);
                                        signupLayout.setVisibility(View.GONE);

                                        clearSignupFields(fullName, signupEmail, signupPassword,
                                                phoneNumber, checkboxTermsAndConditions);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show());
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Signup failed";
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
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