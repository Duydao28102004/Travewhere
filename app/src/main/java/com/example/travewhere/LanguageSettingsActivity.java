package com.example.travewhere;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;

public class LanguageSettingsActivity extends BaseActivity {

    private RadioButton englishRadioButton;
    private RadioButton vietnameseRadioButton;
    private RadioButton spanishRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);

        RadioGroup languageRadioGroup = findViewById(R.id.languages_selection);
        englishRadioButton = findViewById(R.id.english);
        vietnameseRadioButton = findViewById(R.id.vietnamese);
        spanishRadioButton = findViewById(R.id.spanish);

        // Set the current language selection
        setCurrentLanguageSelection();

        // Set the listener for language change
        languageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.english) {
                changeLanguage("en");
            } else if (checkedId == R.id.vietnamese) {
                changeLanguage("vi");
            } else if (checkedId == R.id.spanish) {
                changeLanguage("es");
            }
        });
    }

    private void setCurrentLanguageSelection() {
        String currentLanguage = getPreferredLanguage(this);
        if ("en".equals(currentLanguage)) {
            englishRadioButton.setChecked(true);
        } else if ("vi".equals(currentLanguage)) {
            vietnameseRadioButton.setChecked(true);
        } else if ("es".equals(currentLanguage)) {
            spanishRadioButton.setChecked(true);
        }
    }

    private void changeLanguage(String languageCode) {
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        prefs.edit().putString("language", languageCode).apply();

        Locale newLocale = new Locale(languageCode);
        Locale.setDefault(newLocale);
        Configuration config = new Configuration();
        config.setLocale(newLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Restart the activity to apply the language change
        recreate();
    }

    private void updateUI() {
        // Update the text of UI elements to reflect the new language
        TextView languageSelectionTitle = findViewById(R.id.language_selection_title);
        languageSelectionTitle.setText(R.string.languages_selection);

        englishRadioButton.setText(R.string.language_en);
        vietnameseRadioButton.setText(R.string.language_vn);
        spanishRadioButton.setText(R.string.language_es);
    }
}