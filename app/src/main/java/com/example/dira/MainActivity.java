package com.example.dira;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

public class MainActivity extends AppCompatActivity {

    private ShapeableImageView imgAvatar;
    private ImageButton btnSettings;
    private MaterialCardView cardJoinElection;
    private MaterialCardView cardAdminTools;
    private MaterialCardView cardRegisteredElections;
    private ImageButton btnNotification;
    private ImageButton btnNews;

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        imgAvatar = findViewById(R.id.imgAvatar);
        btnSettings = findViewById(R.id.btnSettings);
        cardJoinElection = findViewById(R.id.cardJoinElection);
        cardAdminTools = findViewById(R.id.cardAdminTools);
        cardRegisteredElections = findViewById(R.id.cardRegisteredElections);
        btnNotification = findViewById(R.id.btnNotification);
        btnNews = findViewById(R.id.btnNews);
        btnBack = findViewById(R.id.btnBack);

        // Setup Click Listeners
        if (imgAvatar != null) {
            imgAvatar.setOnClickListener(v ->
                    Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show());
        }

        if (btnSettings != null) {
            btnSettings.setOnClickListener(v ->
                    Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show());
        }

        if (cardJoinElection != null) {
            cardJoinElection.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, VoteActivity.class);
                startActivity(intent);
            });
        }

        if (cardAdminTools != null) {
            cardAdminTools.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AdminTools.class);
                startActivity(intent);
            });
        }

        if (cardRegisteredElections != null) {
            cardRegisteredElections.setOnClickListener(v ->
                    Toast.makeText(this, "Registered Elections clicked", Toast.LENGTH_SHORT).show());
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v ->
                    Toast.makeText(this, "Back clicked", Toast.LENGTH_SHORT).show());
        }


        if (btnNotification != null) {
            btnNotification.setOnClickListener(v ->
                    Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show());
        }

        if (btnNews != null) {
            btnNews.setOnClickListener(v ->
                    Toast.makeText(this, "News clicked", Toast.LENGTH_SHORT).show());
        }
    }
}
