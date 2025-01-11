package com.example.travewhere.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.travewhere.AuthenticationActivity;
import com.example.travewhere.LanguageSettingsActivity;
import com.example.travewhere.PreferencesSettingsActivity;
import com.example.travewhere.R;
import com.example.travewhere.repositories.AuthenticationRepository;


public class SettingsFragment extends Fragment {

    private AuthenticationRepository authRepository = new AuthenticationRepository();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the settings buttons and set their onClickListeners
        LinearLayout languageSettingsButtonButton = view.findViewById(R.id.language_settings);
        languageSettingsButtonButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LanguageSettingsActivity.class);
            startActivity(intent);
        });

        LinearLayout preferencesSettingsButton = view.findViewById(R.id.preferences_settings);
        preferencesSettingsButton.setOnClickListener(v -> {
            // Open the preferences settings activity
            Intent intent = new Intent(getActivity(), PreferencesSettingsActivity.class);
            startActivity(intent);
        });

        LinearLayout securitySettingsButton = view.findViewById(R.id.security_settings);
        securitySettingsButton.setOnClickListener(v -> {
            // Open the security settings activity
        });

        LinearLayout currencySettingsButton = view.findViewById(R.id.currency_settings);
        currencySettingsButton.setOnClickListener(v -> {
            // Open the currency settings activity
        });

        LinearLayout aboutSettingsButton = view.findViewById(R.id.about_us_settings);
        aboutSettingsButton.setOnClickListener(v -> {
            // Open the about settings activity
        });

        LinearLayout faqSettingsButton = view.findViewById(R.id.faq_settings);
        faqSettingsButton.setOnClickListener(v -> {
            // Open the faq settings activity
        });

        LinearLayout contactUsSettingsButton = view.findViewById(R.id.contact_us_settings);
        contactUsSettingsButton.setOnClickListener(v -> {
            // Open the contact us settings activity
        });

        LinearLayout customLogoutButton = view.findViewById(R.id.logout_button);
        customLogoutButton.setOnClickListener(v -> {
            // Log out the user
            authRepository.logout();
            Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
            startActivity(intent);

            // Finish the current activity
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}