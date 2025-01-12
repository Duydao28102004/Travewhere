package com.example.travewhere.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.content.Context;
import androidx.annotation.NonNull;

import com.example.travewhere.AboutUsActivity;
import com.example.travewhere.AuthenticationActivity;
import com.example.travewhere.ContactUsActivity;
import com.example.travewhere.FAQsActivity;
import com.example.travewhere.LanguageSettingsActivity;
import com.example.travewhere.PreferencesSettingsActivity;
import com.example.travewhere.R;
import com.example.travewhere.helpers.LanguageHelper;
import com.example.travewhere.helpers.ThemeHelper;
import com.example.travewhere.repositories.AuthenticationRepository;

public class SettingsFragment extends Fragment {
    private boolean isRecreating = false;
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

//        LinearLayout securitySettingsButton = view.findViewById(R.id.security_settings);
//        securitySettingsButton.setOnClickListener(v -> {
//            // Open the security settings activity
//        });
//
//        LinearLayout currencySettingsButton = view.findViewById(R.id.currency_settings);
//        currencySettingsButton.setOnClickListener(v -> {
//            // Open the currency settings activity
//        });

        LinearLayout aboutSettingsButton = view.findViewById(R.id.about_us_settings);
        aboutSettingsButton.setOnClickListener(v -> {
            // Open the about settings activity
            Intent intent = new Intent(getActivity(), AboutUsActivity.class);
            startActivity(intent);
        });

        LinearLayout faqSettingsButton = view.findViewById(R.id.faq_settings);
        faqSettingsButton.setOnClickListener(v -> {
            // Open the faq settings activity
            Intent intent = new Intent(getActivity(), FAQsActivity.class);
            startActivity(intent);
        });

        LinearLayout contactUsSettingsButton = view.findViewById(R.id.contact_us_settings);
        contactUsSettingsButton.setOnClickListener(v -> {
            // Open the contact us settings activity
            Intent intent = new Intent(getActivity(), ContactUsActivity.class);
            startActivity(intent);
        });

        LinearLayout customLogoutButton = view.findViewById(R.id.logout_button);
        customLogoutButton.setOnClickListener(v -> {
            // Log out the user
            authRepository.logout(); // Ensure this clears user session
            Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Finish the current activity
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Apply language when the fragment is resumed
        LanguageHelper.applyLanguage(requireContext());

        // Apply theme when the fragment is resumed
        ThemeHelper.applyTheme(requireContext());
    }
}