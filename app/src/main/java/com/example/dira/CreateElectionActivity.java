package com.example.dira;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class CreateElectionActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private TextView step1Circle;
    private TextView step2Circle;
    private TextView step3Circle;
    private TextView step4Circle;
    private TextView tvStepNumber;
    private TextView tvStepTitle;

    // Step 1 Inputs
    private EditText etElectionId;
    private EditText etElectionTitle;
    private EditText etElectionDate;
    private EditText etElectionDuration;

    // Step 2 Inputs
    private LinearLayout sectionsContainer;
    private Button btnAddSection;
    private int sectionCount = 2;

    // Step 3 Inputs
    private EditText etInstructions;

    // Step 4 Confirmation
    private TextView tvConfirmationSummary;

    // Navigation Buttons
    private Button btnNext;
    private Button btnBack;

    private int currentStep = 0; // 0, 1, 2, 3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_election);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        viewFlipper = findViewById(R.id.viewFlipper);
        step1Circle = findViewById(R.id.step1Circle);
        step2Circle = findViewById(R.id.step2Circle);
        step3Circle = findViewById(R.id.step3Circle);
        step4Circle = findViewById(R.id.step4Circle);
        tvStepNumber = findViewById(R.id.tvStepNumber);
        tvStepTitle = findViewById(R.id.tvStepTitle);

        etElectionId = findViewById(R.id.etElectionId);
        etElectionTitle = findViewById(R.id.etElectionTitle);
        etElectionDate = findViewById(R.id.etElectionDate);
        etElectionDuration = findViewById(R.id.etElectionDuration);

        sectionsContainer = findViewById(R.id.sectionsContainer);
        btnAddSection = findViewById(R.id.btnAddSection);
        etInstructions = findViewById(R.id.etInstructions);
        tvConfirmationSummary = findViewById(R.id.tvConfirmationSummary);

        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        updateStepUI();

        // Add Dynamic Section in Step 2
        if (btnAddSection != null) {
            btnAddSection.setOnClickListener(v -> {
                sectionCount++;
                EditText newSectionInput = new EditText(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(48));
                params.setMargins(0, 0, 0, dpToPx(12));
                newSectionInput.setLayoutParams(params);
                newSectionInput.setBackgroundColor(Color.parseColor("#EBEBEB"));
                newSectionInput.setPadding(dpToPx(12), 0, dpToPx(12), 0);
                newSectionInput.setHint("Section " + sectionCount + " Name");
                newSectionInput.setHintTextColor(Color.parseColor("#757575"));
                newSectionInput.setTextColor(Color.BLACK);
                newSectionInput.setTextSize(14);
                sectionsContainer.addView(newSectionInput);
            });
        }

        // Handle Next Click
        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                if (currentStep == 0) {
                    // Validate Step 1
                    String id = etElectionId.getText().toString().trim();
                    String title = etElectionTitle.getText().toString().trim();
                    String date = etElectionDate.getText().toString().trim();
                    String duration = etElectionDuration.getText().toString().trim();

                    if (id.isEmpty() || title.isEmpty() || date.isEmpty() || duration.isEmpty()) {
                        Toast.makeText(this, "Please fill in all Step 1 fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    goToStep(1);
                } else if (currentStep == 1) {
                    // Validate Step 2
                    goToStep(2);
                } else if (currentStep == 2) {
                    // Validate Step 3
                    String title = etElectionTitle.getText().toString().trim();
                    String date = etElectionDate.getText().toString().trim();

                    if (tvConfirmationSummary != null) {
                        tvConfirmationSummary.setText("Your Election [" + title + "]\nscheduled for [" + date + "]\nHas been successfully created");
                    }
                    goToStep(3);
                } else if (currentStep == 3) {
                    // Final Submit
                    String id = etElectionId.getText().toString().trim();
                    String title = etElectionTitle.getText().toString().trim();
                    String date = etElectionDate.getText().toString().trim();
                    String duration = etElectionDuration.getText().toString().trim();
                    String instructions = etInstructions != null ? etInstructions.getText().toString().trim() : "";

                    List<String> sections = new ArrayList<>();
                    if (sectionsContainer != null) {
                        for (int i = 0; i < sectionsContainer.getChildCount(); i++) {
                            View child = sectionsContainer.getChildAt(i);
                            if (child instanceof EditText) {
                                String sec = ((EditText) child).getText().toString().trim();
                                if (!sec.isEmpty()) {
                                    sections.add(sec);
                                }
                            }
                        }
                    }

                    Election election = new Election(id, title, date, duration, sections, instructions);

                    if (btnNext != null) btnNext.setEnabled(false);
                    Toast.makeText(this, "Saving election ..", Toast.LENGTH_SHORT).show();

                    ElectionStorage.saveElection(election, new ElectionStorage.OnElectionSavedListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(CreateElectionActivity.this, "Election Created .", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (btnNext != null) btnNext.setEnabled(true);
                            Toast.makeText(CreateElectionActivity.this, "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }

        // Handle Back Click
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (currentStep > 0) {
                    goToStep(currentStep - 1);
                } else {
                    finish();
                }
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

        if (tvStepTitle != null) {
            switch (currentStep) {
                case 0:
                    tvStepTitle.setText("Create Election");
                    break;
                case 1:
                    tvStepTitle.setText("Create Sections");
                    break;
                case 2:
                    tvStepTitle.setText("Create Instructions");
                    break;
                case 3:
                    tvStepTitle.setText("Confirmation");
                    break;
            }
        }

        if (btnNext != null) {
            if (currentStep == 3) {
                btnNext.setText("Create");
            } else {
                btnNext.setText("Next");
            }
        }

        // Highlight active step indicator
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
        }
    }

    private void resetStepCircles() {
        if (step1Circle != null) step1Circle.setBackgroundResource(0);
        if (step2Circle != null) step2Circle.setBackgroundResource(0);
        if (step3Circle != null) step3Circle.setBackgroundResource(0);
        if (step4Circle != null) step4Circle.setBackgroundResource(0);
    }

    private void highlightStepCircle(TextView circle) {
        if (circle != null) {
            circle.setBackgroundResource(R.drawable.black_circle_svgrepo_com);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
