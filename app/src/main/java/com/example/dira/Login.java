package com.example.dira;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    private static final String TAG = "FirebaseAuth";

    private TextInputEditText phoneEditText;
    private TextInputEditText OTPEditText;
    private Button OTPButton;
    private Button loginButton;

    private FirebaseAuth mAuth;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

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
                    sendOTP(phone);
                }
            });
        }

        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                String otp = OTPEditText != null && OTPEditText.getText() != null
                        ? OTPEditText.getText().toString().trim() : "";

                if (otp.isEmpty()) {
                    Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                } else if (mVerificationId == null) {
                    Toast.makeText(this, "Please request an OTP first", Toast.LENGTH_SHORT).show();
                } else {
                    verifyOTP(otp);
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
                        String code = credential.getSmsCode();
                        if (code != null && OTPEditText != null) {
                            OTPEditText.setText(code);
                        }
                        signInWithCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e(TAG, "onVerificationFailed: " + e.getMessage(), e);
                        if (OTPButton != null) OTPButton.setEnabled(true);

                        new AlertDialog.Builder(Login.this)
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
                        if (OTPButton != null) OTPButton.setEnabled(true);
                        Toast.makeText(Login.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOTP(String code) {
        if (loginButton != null) loginButton.setEnabled(false);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (loginButton != null) loginButton.setEnabled(true);
            if (OTPButton != null) OTPButton.setEnabled(true);

            if (task.isSuccessful()) {
                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Exception ex = task.getException();
                Log.e(TAG, "signInWithCredential failed", ex);
                String errorMsg = ex != null ? ex.getLocalizedMessage() : "Authentication failed";

                new AlertDialog.Builder(Login.this)
                        .setTitle("Login Failed")
                        .setMessage("Error: " + errorMsg)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }
}
