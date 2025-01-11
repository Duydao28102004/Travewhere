package com.example.travewhere;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LanguageSettingsActivity extends AppCompatActivity {

    private RadioButton englishRadioButton;
    private RadioButton vietnameseRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);

        RadioGroup languageRadioGroup = findViewById(R.id.languages_selection);
        englishRadioButton = findViewById(R.id.english);
        vietnameseRadioButton = findViewById(R.id.vietnamese);
        LinearLayout confirmButton = findViewById(R.id.confirm_language_button);

        setCurrentLanguageSelection();

        confirmButton.setOnClickListener(v -> {
            String selectedLanguage = englishRadioButton.isChecked() ? "en" : "vi";
            changeLanguage(selectedLanguage);
            setResult(RESULT_OK); // Notify parent activity
            finish();
        });
    }

    private void setCurrentLanguageSelection() {
        String currentLanguage = getPreferredLanguage(this);

        if ("en".equals(currentLanguage)) {
            englishRadioButton.setChecked(true);
        } else if ("vi".equals(currentLanguage)) {
            vietnameseRadioButton.setChecked(true);
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
    }
}
