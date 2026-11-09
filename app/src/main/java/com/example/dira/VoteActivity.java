package com.example.dira;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VoteActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private TextView step1Circle, step2Circle, step3Circle, step4Circle, step5Circle;
    private TextView tvStepNumber;

    // Step 1 Views
    private EditText etElectionId, etVoterId, etOTP;
    private TextView tvResendOTP;
    private Button btnJoin;

    // Step 2 Views
    private TextView tvLandingTitle, tvLandingId, tvLandingDate, tvLandingStatus, tvLandingSections, tvLandingDuration, tvLandingInstructions;
    private Button btnViewCandidates, btnElectionNews, btnNextStep2, btnBackStep2;
    private CheckBox cbUnderstand;

    // Step 3 Views
    private TextView tvSectionTitle, tvSectionName;
    private LinearLayout llCandidatesContainer;
    private Button btnNextStep3, btnBackStep3;

    // Step 4 Views
    private TextView tvReviewTitle, tvReviewSections, tvReviewTimeStamp, tvReviewReceiptRef;
    private Button btnNextStep4, btnBackStep4;

    // Step 5 Views
    private TextView tvReceiptHash, tvReceiptTime, tvReceiptReference;
    private TextView tvProgressStatus, tvProgressTime, tvProgressCount;
    private ProgressBar progressVoting;
    private Button btnFinish;

    // Data State
    private int currentStep = 0; // 0 to 4
    private Election currentElection;
    private Voter currentVoter;
    private List<Candidate> allCandidates = new ArrayList<>();
    private int currentSectionIndex = 0;
    private Map<String, String> selectedCandidates = new HashMap<>(); // sectionName -> candidateId:candidateName
    private Ballot submittedBallot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vote);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupStep1();
        setupStep2();
        setupStep3Navigation();
        setupStep4();
        setupStep5();

        updateStepUI();
    }

    private void initViews() {
        viewFlipper = findViewById(R.id.viewFlipper);
        step1Circle = findViewById(R.id.step1Circle);
        step2Circle = findViewById(R.id.step2Circle);
        step3Circle = findViewById(R.id.step3Circle);
        step4Circle = findViewById(R.id.step4Circle);
        step5Circle = findViewById(R.id.step5Circle);
        tvStepNumber = findViewById(R.id.tvStepNumber);

        // Step 1
        etElectionId = findViewById(R.id.etElectionId);
        etVoterId = findViewById(R.id.etVoterId);
        etOTP = findViewById(R.id.etOTP);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        btnJoin = findViewById(R.id.btnJoin);

        // Step 2
        tvLandingTitle = findViewById(R.id.tvLandingTitle);
        tvLandingId = findViewById(R.id.tvLandingId);
        tvLandingDate = findViewById(R.id.tvLandingDate);
        tvLandingStatus = findViewById(R.id.tvLandingStatus);
        tvLandingSections = findViewById(R.id.tvLandingSections);
        tvLandingDuration = findViewById(R.id.tvLandingDuration);
        tvLandingInstructions = findViewById(R.id.tvLandingInstructions);
        btnViewCandidates = findViewById(R.id.btnViewCandidates);
        btnElectionNews = findViewById(R.id.btnElectionNews);
        cbUnderstand = findViewById(R.id.cbUnderstand);
        btnNextStep2 = findViewById(R.id.btnNextStep2);
        btnBackStep2 = findViewById(R.id.btnBackStep2);

        // Step 3
        tvSectionTitle = findViewById(R.id.tvSectionTitle);
        tvSectionName = findViewById(R.id.tvSectionName);
        llCandidatesContainer = findViewById(R.id.llCandidatesContainer);
        btnNextStep3 = findViewById(R.id.btnNextStep3);
        btnBackStep3 = findViewById(R.id.btnBackStep3);

        // Step 4
        tvReviewTitle = findViewById(R.id.tvReviewTitle);
        tvReviewSections = findViewById(R.id.tvReviewSections);
        tvReviewTimeStamp = findViewById(R.id.tvReviewTimeStamp);
        tvReviewReceiptRef = findViewById(R.id.tvReviewReceiptRef);
        btnNextStep4 = findViewById(R.id.btnNextStep4);
        btnBackStep4 = findViewById(R.id.btnBackStep4);

        // Step 5
        tvReceiptHash = findViewById(R.id.tvReceiptHash);
        tvReceiptTime = findViewById(R.id.tvReceiptTime);
        tvReceiptReference = findViewById(R.id.tvReceiptReference);
        tvProgressStatus = findViewById(R.id.tvProgressStatus);
        tvProgressTime = findViewById(R.id.tvProgressTime);
        tvProgressCount = findViewById(R.id.tvProgressCount);
        progressVoting = findViewById(R.id.progressVoting);
        btnFinish = findViewById(R.id.btnFinish);
    }

    private void setupStep1() {
        if (tvResendOTP != null) {
            tvResendOTP.setOnClickListener(v ->
                    Toast.makeText(this, "OTP resent to registered phone number", Toast.LENGTH_SHORT).show());
        }

        if (btnJoin != null) {
            btnJoin.setOnClickListener(v -> {
                String electionIdInput = etElectionId != null ? etElectionId.getText().toString().trim() : "";
                String voterIdInput = etVoterId != null ? etVoterId.getText().toString().trim() : "";

                if (electionIdInput.isEmpty() || voterIdInput.isEmpty()) {
                    Toast.makeText(this, "Please enter Election ID and Voter ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnJoin.setEnabled(false);
                Toast.makeText(this, "Verifying Election and Voter...", Toast.LENGTH_SHORT).show();

                // Fetch election
                ElectionStorage.getElections(new ElectionStorage.OnElectionsLoadedListener() {
                    @Override
                    public void onSuccess(List<Election> elections) {
                        Election matchedElection = null;
                        for (Election e : elections) {
                            if (electionIdInput.equalsIgnoreCase(e.getId())) {
                                matchedElection = e;
                                break;
                            }
                        }

                        if (matchedElection == null) {
                            btnJoin.setEnabled(true);
                            Toast.makeText(VoteActivity.this, "Election ID not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        currentElection = matchedElection;
                        verifyVoterAndProceed(electionIdInput, voterIdInput);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        btnJoin.setEnabled(true);
                        Toast.makeText(VoteActivity.this, "Error fetching election: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private void verifyVoterAndProceed(String electionId, String voterId) {
        VoterStorage.getVoters(electionId, new VoterStorage.OnVotersLoadedListener() {
            @Override
            public void onSuccess(List<Voter> voters) {
                if (btnJoin != null) btnJoin.setEnabled(true);
                Voter matchedVoter = null;
                for (Voter v : voters) {
                    if (voterId.equalsIgnoreCase(v.getId())) {
                        matchedVoter = v;
                        break;
                    }
                }

                if (matchedVoter == null) {
                    Toast.makeText(VoteActivity.this, "Voter ID not registered for this election", Toast.LENGTH_LONG).show();
                    return;
                }

                if ("Voted".equalsIgnoreCase(matchedVoter.getStatus())) {
                    new AlertDialog.Builder(VoteActivity.this)
                            .setTitle("Already Voted")
                            .setMessage("Voter " + matchedVoter.getName() + " has already cast a ballot for this election.")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                currentVoter = matchedVoter;
                populateStep2Data();
                goToStep(1); // Step 2 (0-indexed 1)
            }

            @Override
            public void onFailure(Exception e) {
                if (btnJoin != null) btnJoin.setEnabled(true);
                Toast.makeText(VoteActivity.this, "Error verifying voter: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateStep2Data() {
        if (currentElection == null) return;

        if (tvLandingTitle != null) tvLandingTitle.setText("TITLE: " + currentElection.getTitle());
        if (tvLandingId != null) tvLandingId.setText("ID: " + currentElection.getId());
        if (tvLandingDate != null) tvLandingDate.setText("DATE: " + currentElection.getDate());
        if (tvLandingStatus != null) tvLandingStatus.setText("STATUS: " + currentElection.getStatus());
        if (tvLandingSections != null) {
            String secStr = (currentElection.getSections() != null && !currentElection.getSections().isEmpty())
                    ? String.join(", ", currentElection.getSections()) : "General";
            tvLandingSections.setText("SECTIONS: " + secStr);
        }
        if (tvLandingDuration != null) tvLandingDuration.setText("DURATION: " + currentElection.getDuration());
        if (tvLandingInstructions != null) {
            String inst = currentElection.getInstructions();
            tvLandingInstructions.setText((inst != null && !inst.isEmpty()) ? inst : "Follow voting guidelines carefully.");
        }
    }

    private void setupStep2() {
        if (btnViewCandidates != null) {
            btnViewCandidates.setOnClickListener(v ->
                    Toast.makeText(this, "Loading candidates overview...", Toast.LENGTH_SHORT).show());
        }

        if (btnElectionNews != null) {
            btnElectionNews.setOnClickListener(v ->
                    Toast.makeText(this, "Election news: All systems operational.", Toast.LENGTH_SHORT).show());
        }

        if (btnNextStep2 != null) {
            btnNextStep2.setOnClickListener(v -> {
                if (cbUnderstand != null && !cbUnderstand.isChecked()) {
                    Toast.makeText(this, "Please check the box to confirm you understand the instructions", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Load Candidates for Section Voting
                loadCandidatesForStep3();
            });
        }

        if (btnBackStep2 != null) {
            btnBackStep2.setOnClickListener(v -> goToStep(0));
        }
    }

    private void loadCandidatesForStep3() {
        if (currentElection == null) return;

        Toast.makeText(this, "Loading candidates...", Toast.LENGTH_SHORT).show();
        CandidateStorage.getCandidates(currentElection.getId(), new CandidateStorage.OnCandidatesLoadedListener() {
            @Override
            public void onSuccess(List<Candidate> candidates) {
                allCandidates = candidates;
                currentSectionIndex = 0;
                populateStep3Section();
                goToStep(2); // Step 3
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(VoteActivity.this, "Failed to load candidates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateStep3Section() {
        if (currentElection == null) return;

        List<String> sections = currentElection.getSections();
        String currentSectionName = (sections != null && currentSectionIndex < sections.size())
                ? sections.get(currentSectionIndex) : "General Section";

        if (tvSectionTitle != null) tvSectionTitle.setText("TITLE: " + currentElection.getTitle());
        if (tvSectionName != null) tvSectionName.setText("Section " + (currentSectionIndex + 1) + ": " + currentSectionName);

        if (llCandidatesContainer != null) {
            llCandidatesContainer.removeAllViews();

            // Filter candidates for this section/position if position matches or show all candidates
            List<Candidate> sectionCandidates = new ArrayList<>();
            for (Candidate c : allCandidates) {
                if (c.getPosition() == null || c.getPosition().equalsIgnoreCase(currentSectionName) || sections == null || sections.size() <= 1) {
                    sectionCandidates.add(c);
                }
            }
            if (sectionCandidates.isEmpty()) {
                sectionCandidates = allCandidates; // Fallback to show candidates
            }

            for (Candidate cand : sectionCandidates) {
                View candCard = LayoutInflater.from(this).inflate(R.layout.item_candidate, llCandidatesContainer, false);

                TextView tvName = candCard.findViewById(R.id.tvCandidateName);
                TextView tvPos = candCard.findViewById(R.id.tvCandidatePosition);
                TextView tvId = candCard.findViewById(R.id.tvCandidateId);
                TextView tvParty = candCard.findViewById(R.id.tvCandidateParty);
                View btnDelete = candCard.findViewById(R.id.btnDeleteCandidate);
                if (btnDelete != null) btnDelete.setVisibility(View.GONE);

                if (tvName != null) tvName.setText(cand.getName());
                if (tvPos != null) tvPos.setText("Position: " + cand.getPosition());
                if (tvId != null) tvId.setText("ID: " + cand.getId());
                if (tvParty != null) tvParty.setText("Party: " + cand.getParty());

                // Selection state
                String currentChoice = selectedCandidates.get(currentSectionName);
                boolean isSelected = currentChoice != null && currentChoice.startsWith(cand.getId());

                if (isSelected) {
                    candCard.setBackgroundColor(Color.parseColor("#FFF8E1")); // Gold tint
                } else {
                    candCard.setBackgroundColor(Color.parseColor("#CCFFFFFF"));
                }

                candCard.setOnClickListener(v -> {
                    selectedCandidates.put(currentSectionName, cand.getId() + ":" + cand.getName());
                    populateStep3Section(); // Refresh highlight
                });

                llCandidatesContainer.addView(candCard);
            }
        }
    }

    private void setupStep3Navigation() {
        if (btnNextStep3 != null) {
            btnNextStep3.setOnClickListener(v -> {
                List<String> sections = currentElection != null ? currentElection.getSections() : null;
                String currentSectionName = (sections != null && currentSectionIndex < sections.size())
                        ? sections.get(currentSectionIndex) : "General Section";

                if (!selectedCandidates.containsKey(currentSectionName)) {
                    Toast.makeText(this, "Please select a candidate before proceeding", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sections != null && currentSectionIndex < sections.size() - 1) {
                    currentSectionIndex++;
                    populateStep3Section();
                } else {
                    populateStep4Review();
                    goToStep(3); // Step 4
                }
            });
        }

        if (btnBackStep3 != null) {
            btnBackStep3.setOnClickListener(v -> {
                if (currentSectionIndex > 0) {
                    currentSectionIndex--;
                    populateStep3Section();
                } else {
                    goToStep(1); // Return to Step 2
                }
            });
        }
    }

    private void populateStep4Review() {
        if (currentElection == null) return;

        if (tvReviewTitle != null) tvReviewTitle.setText("TITLE: " + currentElection.getTitle());

        StringBuilder choicesText = new StringBuilder();
        for (Map.Entry<String, String> entry : selectedCandidates.entrySet()) {
            String section = entry.getKey();
            String val = entry.getValue();
            String candName = val.contains(":") ? val.split(":")[1] : val;
            choicesText.append("• ").append(section).append(" -> ").append(candName).append("\n");
        }
        if (tvReviewSections != null) tvReviewSections.setText("CHOICES:\n" + choicesText.toString().trim());

        String timeNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        if (tvReviewTimeStamp != null) tvReviewTimeStamp.setText("Time Stamp: " + timeNow);

        String refPreview = "DIRA-BLOCK-" + Long.toHexString(System.currentTimeMillis()).toUpperCase();
        if (tvReviewReceiptRef != null) tvReviewReceiptRef.setText("Receipt Ref: " + refPreview);
    }

    private void setupStep4() {
        if (btnNextStep4 != null) {
            btnNextStep4.setOnClickListener(v -> {
                if (currentElection == null || currentVoter == null) return;

                btnNextStep4.setEnabled(false);
                Toast.makeText(this, "Recording ballot on Blockchain Ledger...", Toast.LENGTH_SHORT).show();

                String timeNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                Ballot ballot = new Ballot("", currentElection.getId(), currentVoter.getId(), selectedCandidates, timeNow, "", "");

                VoteStorage.submitBallot(ballot, new VoteStorage.OnBallotSubmittedListener() {
                    @Override
                    public void onSuccess(Ballot savedBallot) {
                        submittedBallot = savedBallot;
                        populateStep5Receipt();
                        goToStep(4); // Step 5
                    }

                    @Override
                    public void onFailure(Exception e) {
                        btnNextStep4.setEnabled(true);
                        Toast.makeText(VoteActivity.this, "Failed to record vote on Blockchain: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });
        }

        if (btnBackStep4 != null) {
            btnBackStep4.setOnClickListener(v -> goToStep(2));
        }
    }

    private void populateStep5Receipt() {
        if (submittedBallot == null) return;

        if (tvReceiptHash != null) tvReceiptHash.setText("Transaction hash:\n" + submittedBallot.getTransactionHash());
        if (tvReceiptTime != null) tvReceiptTime.setText("Time stamp: " + submittedBallot.getTimestamp());
        if (tvReceiptReference != null) tvReceiptReference.setText("Reference: " + submittedBallot.getReceiptReference());

        // Fetch live progress
        if (currentElection != null) {
            VoteStorage.getElectionProgress(currentElection.getId(), new VoteStorage.OnProgressLoadedListener() {
                @Override
                public void onSuccess(int votedCount, int totalVoters) {
                    if (tvProgressStatus != null) tvProgressStatus.setText("Status: " + currentElection.getStatus());
                    if (tvProgressTime != null) tvProgressTime.setText("Time Remaining: " + currentElection.getDuration());

                    if (progressVoting != null) {
                        progressVoting.setMax(totalVoters > 0 ? totalVoters : 100);
                        progressVoting.setProgress(votedCount);
                    }
                    if (tvProgressCount != null) {
                        tvProgressCount.setText(votedCount + " of " + totalVoters + " participants voted");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    if (tvProgressCount != null) tvProgressCount.setText("Progress updated on Blockchain");
                }
            });
        }
    }

    private void setupStep5() {
        if (btnFinish != null) {
            btnFinish.setOnClickListener(v -> {
                Toast.makeText(this, "Thank you for voting!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private void goToStep(int step) {
        currentStep = step;
        if (viewFlipper != null) {
            viewFlipper.setDisplayedChild(currentStep);
        }
        updateStepUI();
    }

    private void updateStepUI() {
        if (tvStepNumber != null) {
            tvStepNumber.setText("Step " + (currentStep + 1) + ".");
        }

        resetStepCircles();
        switch (currentStep) {
            case 0:
                highlightStepCircle(step1Circle);
                break;
            case 1:
                highlightStepCircle(step2Circle);
                break;
            case 2:
                highlightStepCircle(step3Circle);
                break;
            case 3:
                highlightStepCircle(step4Circle);
                break;
            case 4:
                highlightStepCircle(step5Circle);
                break;
        }
    }

    private void resetStepCircles() {
        if (step1Circle != null) step1Circle.setTextColor(Color.BLACK);
        if (step2Circle != null) step2Circle.setTextColor(Color.BLACK);
        if (step3Circle != null) step3Circle.setTextColor(Color.BLACK);
        if (step4Circle != null) step4Circle.setTextColor(Color.BLACK);
        if (step5Circle != null) step5Circle.setTextColor(Color.BLACK);
    }

    private void highlightStepCircle(TextView circle) {
        if (circle != null) {
            circle.setTextColor(Color.WHITE);
        }
    }
}
