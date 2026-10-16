package com.example.dira;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    private TextInputEditText phoneEditText;
    private TextInputEditText OTPEditText;
    private Button OTPButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

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
        OTPButton = findViewById(R.id.OTPButton);
        OTPEditText = findViewById(R.id.OTPEditText);
        loginButton = findViewById(R.id.loginButton);

        if (OTPButton != null) {
            OTPButton.setOnClickListener(v -> {
                String phone = phoneEditText != null && phoneEditText.getText() != null
                        ? phoneEditText.getText().toString().trim() : "";

                if (phone.isEmpty()) {
                    Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "OTP sent to " + phone, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                String phone = phoneEditText != null && phoneEditText.getText() != null
                        ? phoneEditText.getText().toString().trim() : "";
                String otp = OTPEditText != null && OTPEditText.getText() != null
                        ? OTPEditText.getText().toString().trim() : "";

                if (phone.isEmpty() || otp.isEmpty()) {
                    Toast.makeText(this, "Please enter both phone number and OTP", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
