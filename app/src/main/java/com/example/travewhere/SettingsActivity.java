package com.example.travewhere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends BaseActivity {

    private static final int REQUEST_CODE_LANGUAGE_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Initialize the settings buttons and set their onClickListeners
        LinearLayout languageSettingsButtonButton = findViewById(R.id.language_settings);
        languageSettingsButtonButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, LanguageSettingsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LANGUAGE_SETTINGS);
        });

        LinearLayout preferencesSettingsButton = findViewById(R.id.preferences_settings);
        preferencesSettingsButton.setOnClickListener(v -> {
            // Open the preferences settings activity
            Intent intent = new Intent(SettingsActivity.this, PreferencesSettingsActivity.class);
            startActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LANGUAGE_SETTINGS && resultCode == RESULT_OK) {
            recreate(); // Reload the activity to apply the new language
        }
    }
}