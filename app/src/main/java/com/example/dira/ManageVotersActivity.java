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

public class ManageVotersActivity extends AppCompatActivity {

    private String electionId = "";
    private String electionTitle = "";

    private ImageButton btnBack;
    private TextView tvElectionSubHeader;
    private Button btnAddVoter;
    private RecyclerView rvVoters;
    private TextView tvEmptyState;

    private VoterAdapter adapter;
    private List<Voter> voterList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_voters);

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
        btnAddVoter = findViewById(R.id.btnAddVoter);
        rvVoters = findViewById(R.id.rvVoters);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        if (tvElectionSubHeader != null && electionTitle != null) {
            tvElectionSubHeader.setText("Election: " + electionTitle);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnAddVoter != null) {
            btnAddVoter.setOnClickListener(v -> showAddVoterDialog());
        }

        if (rvVoters != null) {
            rvVoters.setLayoutManager(new LinearLayoutManager(this));
            adapter = new VoterAdapter(voterList, position -> {
                if (position >= 0 && position < voterList.size()) {
                    Voter voterToDelete = voterList.get(position);
                    VoterStorage.deleteVoter(electionId, voterToDelete.getId(), new VoterStorage.OnVoterDeletedListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ManageVotersActivity.this, "Voter deleted", Toast.LENGTH_SHORT).show();
                            loadVoters();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ManageVotersActivity.this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            rvVoters.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVoters();
    }

    private void loadVoters() {
        if (electionId == null || electionId.trim().isEmpty()) return;

        VoterStorage.getVoters(electionId, new VoterStorage.OnVotersLoadedListener() {
            @Override
            public void onSuccess(List<Voter> voters) {
                voterList.clear();
                voterList.addAll(voters);

                if (voterList.isEmpty()) {
                    if (tvEmptyState != null) tvEmptyState.setVisibility(View.VISIBLE);
                    if (rvVoters != null) rvVoters.setVisibility(View.GONE);
                } else {
                    if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);
                    if (rvVoters != null) rvVoters.setVisibility(View.VISIBLE);
                    if (adapter != null) adapter.updateList(voterList);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ManageVotersActivity.this, "Failed to load voters: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddVoterDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_voter, null);

        EditText etVoterId = dialogView.findViewById(R.id.etVoterId);
        EditText etVoterName = dialogView.findViewById(R.id.etVoterName);
        EditText etVoterPhone = dialogView.findViewById(R.id.etVoterPhone);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnRegister = dialogView.findViewById(R.id.btnRegister);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnRegister.setOnClickListener(v -> {
            String votId = etVoterId.getText().toString().trim();
            String name = etVoterName.getText().toString().trim();
            String phone = etVoterPhone.getText().toString().trim();

            if (votId.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in Voter ID, Name, and Phone", Toast.LENGTH_SHORT).show();
                return;
            }

            Voter voter = new Voter(votId, electionId, name, phone, "Registered");
            btnRegister.setEnabled(false);

            VoterStorage.saveVoter(electionId, voter, new VoterStorage.OnVoterSavedListener() {
                @Override
                public void onSuccess() {
                    dialog.dismiss();
                    Toast.makeText(ManageVotersActivity.this, "Voter Registered Successfully!", Toast.LENGTH_SHORT).show();
                    loadVoters();
                }

                @Override
                public void onFailure(Exception e) {
                    btnRegister.setEnabled(true);
                    Toast.makeText(ManageVotersActivity.this, "Failed to register voter: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
