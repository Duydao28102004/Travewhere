package com.example.travewhere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactUsActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText messageEditText;
    private Button submitButton, callUsButton, emailUsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        getSupportActionBar().hide();

        // Initialize UI components
        nameEditText = findViewById(R.id.contact_us_name);
        emailEditText = findViewById(R.id.contact_us_email);
        messageEditText = findViewById(R.id.contact_us_message);
        submitButton = findViewById(R.id.contact_us_submit_button);
        callUsButton = findViewById(R.id.contact_us_call_button);
        emailUsButton = findViewById(R.id.contact_us_mail_button);

        // Set button click listeners
        submitButton.setOnClickListener(v -> handleStaticSubmit());
        callUsButton.setOnClickListener(v -> openDialer());
        emailUsButton.setOnClickListener(v -> openMailClient());
    }

    private void handleStaticSubmit() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a confirmation message
        Toast.makeText(this, "Thank you for reaching out, " + name + "!", Toast.LENGTH_LONG).show();

        // Clear the input fields after submission
        nameEditText.setText("");
        emailEditText.setText("");
        messageEditText.setText("");
    }

    private void openDialer() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:+84123456789")); // Replace with your support number
        startActivity(callIntent);
    }

    private void openMailClient() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:support@travewhere.com")); // Replace with your support email
        startActivity(emailIntent);
    }
}
