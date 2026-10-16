package com.example.dira;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OnboardingScreen2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.onboarding_screen2);

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

        Button signupButton = findViewById(R.id.signupButton);
        if (signupButton != null) {
            signupButton.setOnClickListener(v -> {
                Intent intent = new Intent(OnboardingScreen2.this, Signup.class);
                startActivity(intent);
            });
        }

        Button loginButton = findViewById(R.id.loginButton);
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                Intent intent = new Intent(OnboardingScreen2.this, Login.class);
                startActivity(intent);
            });
        }
    }
}
