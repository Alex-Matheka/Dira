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

public class AdminTools extends AppCompatActivity {

    private MaterialCardView cardCreateElection;
    private MaterialCardView cardManageElections;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_tools);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardCreateElection = findViewById(R.id.cardCreateElection);
        cardManageElections = findViewById(R.id.cardManageElections);
        btnBack = findViewById(R.id.btnBack);

        if (cardCreateElection != null) {
            cardCreateElection.setOnClickListener(v -> {
                Intent intent = new Intent(AdminTools.this, CreateElectionActivity.class);
                startActivity(intent);
            });
        }

        if (cardManageElections != null) {
            cardManageElections.setOnClickListener(v ->
                    Toast.makeText(this, "Manage Elections clicked", Toast.LENGTH_SHORT).show());
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
