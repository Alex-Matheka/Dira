package com.example.dira;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ManageElections extends AppCompatActivity {

    private ImageButton btnBack;
    private RecyclerView rvElections;
    private TextView tvEmptyState;

    private ElectionAdapter adapter;
    private List<Election> electionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.manage_elections);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btnBack);
        rvElections = findViewById(R.id.rvElections);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (rvElections != null) {
            rvElections.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ElectionAdapter(electionList, position -> {
                if (position >= 0 && position < electionList.size()) {
                    Election electionToDelete = electionList.get(position);
                    ElectionStorage.deleteElection(electionToDelete.getId(), new ElectionStorage.OnElectionDeletedListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ManageElections.this, "Election deleted from Firestore", Toast.LENGTH_SHORT).show();
                            loadElections();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ManageElections.this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            rvElections.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadElections();
    }

    private void loadElections() {
        ElectionStorage.getElections(new ElectionStorage.OnElectionsLoadedListener() {
            @Override
            public void onSuccess(List<Election> elections) {
                electionList.clear();
                electionList.addAll(elections);

                if (electionList.isEmpty()) {
                    if (tvEmptyState != null) tvEmptyState.setVisibility(View.VISIBLE);
                    if (rvElections != null) rvElections.setVisibility(View.GONE);
                } else {
                    if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);
                    if (rvElections != null) rvElections.setVisibility(View.VISIBLE);
                    if (adapter != null) adapter.updateList(electionList);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageElections.this, "Failed to load elections: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
