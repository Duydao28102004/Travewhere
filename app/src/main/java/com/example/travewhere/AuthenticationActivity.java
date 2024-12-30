package com.example.travewhere;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

public class AuthenticationActivity extends AppCompatActivity {

    private Button loginButton;
    private LinearLayout loginLayout, signupLayout;
    private TextView switchToSignup, switchToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        loginLayout = findViewById(R.id.loginLayout);
        signupLayout = findViewById(R.id.signupLayout);
        loginButton = findViewById(R.id.loginButton);

        switchToLogin = findViewById(R.id.switchToLogin);
        switchToSignup = findViewById(R.id.switchToSignup);

        switchToLogin.setOnClickListener(v -> {
            loginLayout.setVisibility(View.VISIBLE);
            signupLayout.setVisibility(View.GONE);
        });

        switchToSignup.setOnClickListener(v -> {
            loginLayout.setVisibility(View.GONE);
            signupLayout.setVisibility(View.VISIBLE);
        });
    }
}