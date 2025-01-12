package com.example.travewhere.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    private static final String PREFS_NAME = "settings";
    public static final String THEME_KEY = "theme";

    public static void setTheme(Context context, String theme) {
        if (theme == null || theme.isEmpty()) {
            theme = "System Default"; // Default to System Default if invalid input
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(THEME_KEY, theme).apply();

        applyTheme(theme);
    }

    public static String getTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        try {
            return prefs.getString(THEME_KEY, "System Default"); // Default to System Default
        } catch (ClassCastException e) {
            // Reset invalid value to System Default
            prefs.edit().putString(THEME_KEY, "System Default").apply();
            return "System Default";
        }
    }

    public static void applyTheme(Context context) {
        String theme = getTheme(context);
        applyTheme(theme);
    }

    private static void applyTheme(String theme) {
        switch (theme) {
            case "Light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "Dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
