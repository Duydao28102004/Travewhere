package com.example.travewhere;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travewhere.models.Customer;
import com.example.travewhere.models.Manager;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.repositories.FirestoreRepository;
import com.example.travewhere.viewmodels.CustomerViewModel;
import com.example.travewhere.viewmodels.ManagerViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthenticationActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private LinearLayout loginLayout, signupLayout;
    private Button loginButton, signupButton;
    private LinearLayout googleSignInButton;
    private TextView switchToSignup, switchToLogin;
    private EditText loginEmail, loginPassword;
    private EditText signupEmail, signupPassword, fullName, phoneNumber;
    private CheckBox checkboxAccommodationOwner, checkboxTermsAndConditions;

    private AuthenticationRepository authRepository;
    private FirestoreRepository firestoreRepository;
    private CustomerViewModel customerViewModel;
    private ManagerViewModel managerViewModel;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Hide the top bar
        getSupportActionBar().hide();

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

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
        googleSignInButton = findViewById(R.id.googleSignInButton);

        // Set up UI interactions
        setupSwitchListeners();
        setupLoginListener();
        setupSignupListener();

        // Google Sign-In button listener
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
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

            authRepository.login(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = authRepository.getCurrentUser();
                            if (user == null) {
                                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            handleUserType(user.getUid());
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

            authRepository.signup(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = authRepository.getCurrentUser();

                            if (checkboxAccommodationOwner.isChecked()) {
                                managerViewModel.addManager(new Manager(user.getUid(), name, email, phone));
                            } else {
                                customerViewModel.addCustomer(new Customer(user.getUid(), name, email, phone));
                            }

                            Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show();
                            loginLayout.setVisibility(View.VISIBLE);
                            signupLayout.setVisibility(View.GONE);

                            clearSignupFields(fullName, signupEmail, signupPassword, phoneNumber, checkboxTermsAndConditions);
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Signup failed";
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Google sign in failed", e);
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            handleUserType(user.getUid());
                        }
                    } else {
                        Log.w("GoogleSignIn", "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleUserType(String userId) {
        customerViewModel.getCustomerById(userId).observe(this, customer -> {
            if (customer != null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("customer", customer);
                startActivity(intent);
                finish();
            } else {
                managerViewModel.getManagerById(userId).observe(this, manager -> {
                    if (manager != null) {
                        Intent intent = new Intent(this, ManagerActivity.class);
                        intent.putExtra("manager", manager);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Unknown user type", Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
