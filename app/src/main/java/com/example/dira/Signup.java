package com.example.dira;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class Signup extends AppCompatActivity {

    private TextInputEditText phoneEditText;
    private TextInputEditText usernameEditText;
    private TextInputEditText OTPEditText;
    private Button OTPButton;
    private Button signupButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);

                    return insets;
                });

        phoneEditText = findViewById(R.id.phoneEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        OTPButton = findViewById(R.id.OTPButton);
        OTPEditText = findViewById(R.id.OTPEditText);
        signupButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.progressBar);

        if (OTPButton != null) {
            OTPButton.setOnClickListener(v -> {
                String phone = phoneEditText != null && phoneEditText.getText() != null
                        ? phoneEditText.getText().toString().trim() : "";

                if (phone.isEmpty()) {
                    Toast.makeText(this, "Please enter your phone number first", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "OTP sent to " + phone, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (signupButton != null) {
            signupButton.setOnClickListener(v -> {
                String phone = phoneEditText != null && phoneEditText.getText() != null
                        ? phoneEditText.getText().toString().trim() : "";
                String username = usernameEditText != null && usernameEditText.getText() != null
                        ? usernameEditText.getText().toString().trim() : "";
                String otp = OTPEditText != null && OTPEditText.getText() != null
                        ? OTPEditText.getText().toString().trim() : "";

                if (phone.isEmpty() || username.isEmpty() || otp.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    signupButton.setEnabled(false);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        signupButton.setEnabled(true);
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Signup.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }, 1500);
                }
            });
        }
    }
}
