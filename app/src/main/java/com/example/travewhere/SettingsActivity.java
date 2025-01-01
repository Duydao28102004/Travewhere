package com.example.travewhere;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Initialize the settings buttons and set their onClickListeners
        LinearLayout languageSettingsButtonButton = findViewById(R.id.language_settings);
        languageSettingsButtonButton.setOnClickListener(v -> {
            // Open the language settings activity
        });

        LinearLayout preferencesSettingsButton = findViewById(R.id.preference_settings);
        preferencesSettingsButton.setOnClickListener(v -> {
            // Open the preferences settings activity
        });

        LinearLayout securitySettingsButton = findViewById(R.id.security_settings);
        securitySettingsButton.setOnClickListener(v -> {
            // Open the security settings activity
        });

        LinearLayout currencySettingsButton = findViewById(R.id.currency_settings);
        currencySettingsButton.setOnClickListener(v -> {
            // Open the currency settings activity
        });

        LinearLayout aboutSettingsButton = findViewById(R.id.about_us_settings);
        aboutSettingsButton.setOnClickListener(v -> {
            // Open the about settings activity
        });

        LinearLayout faqSettingsButton = findViewById(R.id.faq_settings);
        faqSettingsButton.setOnClickListener(v -> {
            // Open the faq settings activity
        });

        LinearLayout contactUsSettingsButton = findViewById(R.id.contact_us_settings);
        contactUsSettingsButton.setOnClickListener(v -> {
            // Open the contact us settings activity
        });

        LinearLayout customLogoutButton = findViewById(R.id.logout_button);
        customLogoutButton.setOnClickListener(v -> {
            // Logout the user
        });
    }
}