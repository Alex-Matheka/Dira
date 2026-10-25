package com.example.dira;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.TimeUnit;

public class Signup extends AppCompatActivity {

    private static final String TAG = "FirebaseAuth";

    private TextInputEditText phoneEditText;
    private TextInputEditText usernameEditText;
    private TextInputEditText OTPEditText;
    private Button OTPButton;
    private Button signupButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);

        mAuth = FirebaseAuth.getInstance();
        mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

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
                    sendOTP(phone);
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
                } else if (mVerificationId == null) {
                    Toast.makeText(this, "Please request an OTP first", Toast.LENGTH_SHORT).show();
                } else {
                    verifyOTP(otp, username);
                }
            });
        }
    }

    private String formatPhoneNumber(String rawPhone) {
        String clean = rawPhone.replaceAll("[^0-9+]", "");
        if (clean.startsWith("+")) {
            return clean;
        }
        if (clean.length() == 10) {
            return "+1" + clean;
        }
        if (clean.length() == 11 && clean.startsWith("1")) {
            return "+" + clean;
        }
        return "+" + clean;
    }

    private void sendOTP(String rawPhone) {
        String formattedPhone = formatPhoneNumber(rawPhone);

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (OTPButton != null) OTPButton.setEnabled(false);

        Toast.makeText(this, "Sending OTP to " + formattedPhone, Toast.LENGTH_SHORT).show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(formattedPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        Log.d(TAG, "onVerificationCompleted");
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        String code = credential.getSmsCode();
                        if (code != null && OTPEditText != null) {
                            OTPEditText.setText(code);
                        }
                        String username = usernameEditText != null && usernameEditText.getText() != null
                                ? usernameEditText.getText().toString().trim() : "";
                        signInWithCredential(credential, username);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e(TAG, "onVerificationFailed: " + e.getMessage(), e);
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (OTPButton != null) OTPButton.setEnabled(true);

                        new AlertDialog.Builder(Signup.this)
                                .setTitle("Firebase Verification Error")
                                .setMessage("Error: " + e.getLocalizedMessage() + "\n\nTip: Ensure Phone Sign-In is enabled in Firebase Console and test phone number is +16505553434.")
                                .setPositiveButton("OK", null)
                                .show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        Log.d(TAG, "onCodeSent: " + verificationId);
                        mVerificationId = verificationId;
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (OTPButton != null) OTPButton.setEnabled(true);
                        Toast.makeText(Signup.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOTP(String code, String username) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (signupButton != null) signupButton.setEnabled(false);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithCredential(credential, username);
    }

    private void signInWithCredential(PhoneAuthCredential credential, String username) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (signupButton != null) signupButton.setEnabled(true);
            if (OTPButton != null) OTPButton.setEnabled(true);

            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && !username.isEmpty()) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();
                    user.updateProfile(profileUpdates);
                }

                Toast.makeText(Signup.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Signup.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Exception ex = task.getException();
                Log.e(TAG, "signInWithCredential failed", ex);
                String errorMsg = ex != null ? ex.getLocalizedMessage() : "Authentication failed";

                new AlertDialog.Builder(Signup.this)
                        .setTitle("Signup Failed")
                        .setMessage("Error: " + errorMsg)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }
}
