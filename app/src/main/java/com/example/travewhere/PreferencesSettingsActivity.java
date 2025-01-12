package com.example.travewhere;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travewhere.helpers.ThemeHelper;

public class PreferencesSettingsActivity extends AppCompatActivity {

    private RadioButton lightThemeRadioButton;
    private RadioButton darkThemeRadioButton;
    private RadioButton systemDefaultThemeRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferences_settings);

        RadioGroup themeRadioGroup = findViewById(R.id.theme_radio_group);
        Button saveButton = findViewById(R.id.save_button);

        lightThemeRadioButton = findViewById(R.id.light_theme);
        darkThemeRadioButton = findViewById(R.id.dark_theme);
        systemDefaultThemeRadioButton = findViewById(R.id.system_default_theme);

        // Set the current theme selection
        setCurrentThemeSelection();

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            String selectedTheme;

            // Determine the selected theme
            if (lightThemeRadioButton.isChecked()) {
                selectedTheme = "Light";
            } else if (darkThemeRadioButton.isChecked()) {
                selectedTheme = "Dark";
            } else {
                selectedTheme = "System Default";
            }

            // Save and apply the selected theme
            ThemeHelper.setTheme(this, selectedTheme);

            // Show a confirmation message
            Toast.makeText(this, "Theme updated to " + selectedTheme, Toast.LENGTH_SHORT).show();

            // Finish activity and return to the previous screen
            setResult(RESULT_OK);
            finish();
        });
    }

    private void setCurrentThemeSelection() {
        String currentTheme = ThemeHelper.getTheme(this);

        if ("Light".equals(currentTheme)) {
            lightThemeRadioButton.setChecked(true);
        } else if ("Dark".equals(currentTheme)) {
            darkThemeRadioButton.setChecked(true);
        } else {
            systemDefaultThemeRadioButton.setChecked(true);
        }
    }
}