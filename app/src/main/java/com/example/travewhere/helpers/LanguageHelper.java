package com.example.travewhere.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageHelper {
    private static final String PREFS_NAME = "settings";
    private static final String LANGUAGE_KEY = "language";

    public static void setLanguage(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(LANGUAGE_KEY, languageCode).apply();

        Locale newLocale = new Locale(languageCode);
        Locale.setDefault(newLocale);

        Configuration config = new Configuration();
        config.setLocale(newLocale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(LANGUAGE_KEY, Locale.getDefault().getLanguage());
    }

    public static void applyLanguage(Context context) {
        String language = getLanguage(context);
        setLanguage(context, language);
    }
}