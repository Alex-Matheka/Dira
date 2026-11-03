package com.example.dira;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ManageCandidatesActivity extends AppCompatActivity {

    private String electionId = "";
    private String electionTitle = "";

    private ImageButton btnBack;
    private TextView tvElectionSubHeader;
    private Button btnAddCandidate;
    private RecyclerView rvCandidates;
    private TextView tvEmptyState;

    private CandidateAdapter adapter;
    private List<Candidate> candidateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_candidates);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null) {
            electionId = getIntent().getStringExtra("ELECTION_ID");
            electionTitle = getIntent().getStringExtra("ELECTION_TITLE");
        }

        btnBack = findViewById(R.id.btnBack);
        tvElectionSubHeader = findViewById(R.id.tvElectionSubHeader);
        btnAddCandidate = findViewById(R.id.btnAddCandidate);
        rvCandidates = findViewById(R.id.rvCandidates);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        if (tvElectionSubHeader != null && electionTitle != null) {
            tvElectionSubHeader.setText("Election: " + electionTitle);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnAddCandidate != null) {
            btnAddCandidate.setOnClickListener(v -> showAddCandidateDialog());
        }

        if (rvCandidates != null) {
            rvCandidates.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CandidateAdapter(candidateList, position -> {
                if (position >= 0 && position < candidateList.size()) {
                    Candidate candidateToDelete = candidateList.get(position);
                    CandidateStorage.deleteCandidate(electionId, candidateToDelete.getId(), new CandidateStorage.OnCandidateDeletedListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ManageCandidatesActivity.this, "Candidate deleted", Toast.LENGTH_SHORT).show();
                            loadCandidates();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ManageCandidatesActivity.this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            rvCandidates.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCandidates();
    }

    private void loadCandidates() {
        if (electionId == null || electionId.trim().isEmpty()) return;

        CandidateStorage.getCandidates(electionId, new CandidateStorage.OnCandidatesLoadedListener() {
            @Override
            public void onSuccess(List<Candidate> candidates) {
                candidateList.clear();
                candidateList.addAll(candidates);

                if (candidateList.isEmpty()) {
                    if (tvEmptyState != null) tvEmptyState.setVisibility(View.VISIBLE);
                    if (rvCandidates != null) rvCandidates.setVisibility(View.GONE);
                } else {
                    if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);
                    if (rvCandidates != null) rvCandidates.setVisibility(View.VISIBLE);
                    if (adapter != null) adapter.updateList(candidateList);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageCandidatesActivity.this, "Failed to load candidates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddCandidateDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_candidate, null);

        EditText etCandidateId = dialogView.findViewById(R.id.etCandidateId);
        EditText etCandidateName = dialogView.findViewById(R.id.etCandidateName);
        EditText etCandidatePosition = dialogView.findViewById(R.id.etCandidatePosition);
        EditText etCandidateParty = dialogView.findViewById(R.id.etCandidateParty);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnRegister = dialogView.findViewById(R.id.btnRegister);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnRegister.setOnClickListener(v -> {
            String candId = etCandidateId.getText().toString().trim();
            String name = etCandidateName.getText().toString().trim();
            String position = etCandidatePosition.getText().toString().trim();
            String party = etCandidateParty.getText().toString().trim();

            if (candId.isEmpty() || name.isEmpty() || position.isEmpty()) {
                Toast.makeText(this, "Please fill in ID, Name, and Position", Toast.LENGTH_SHORT).show();
                return;
            }

            Candidate candidate = new Candidate(candId, electionId, name, position, party);
            btnRegister.setEnabled(false);

            CandidateStorage.saveCandidate(electionId, candidate, new CandidateStorage.OnCandidateSavedListener() {
                @Override
                public void onSuccess() {
                    dialog.dismiss();
                    Toast.makeText(ManageCandidatesActivity.this, "Candidate Registered Successfully!", Toast.LENGTH_SHORT).show();
                    loadCandidates();
                }

                @Override
                public void onFailure(Exception e) {
                    btnRegister.setEnabled(true);
                    Toast.makeText(ManageCandidatesActivity.this, "Failed to register candidate: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
