package com.example.travewhere.helpers;

import android.widget.CheckBox;
import android.widget.EditText;

public class AuthFormHelper {

    public static void clearSignupFields(EditText fullName, EditText email, EditText password,
                                         EditText phoneNumber, CheckBox termsAndConditionsCheck) {
        fullName.setText("");
        email.setText("");
        password.setText("");
        phoneNumber.setText("");
        termsAndConditionsCheck.setChecked(false);
    }
}

