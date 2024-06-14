package com.example.todaytask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private TextView signInLink;
    private EditText loginName;
    private EditText loginPassword;
    private EditText confirmPassword;
    private EditText emailBox;
    private Button signUpButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressBar = findViewById(R.id.loginSpinnerBar);
        progressBar.setVisibility(View.INVISIBLE);

        signInLink = findViewById(R.id.signInLink);
        loginName = findViewById(R.id.loginName);
        loginPassword = findViewById(R.id.loginPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        emailBox = findViewById(R.id.emailBox);
        signUpButton = findViewById(R.id.signUpButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signInLink.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        });

        signUpButton.setOnClickListener(v -> {
            String name = loginName.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();
            String confPassword = confirmPassword.getText().toString().trim();
            String email = emailBox.getText().toString().trim();

            if (validateInput(name, password, confPassword, email)) {
                disableAllViews();
                progressBar.setVisibility(View.VISIBLE);

                // Create user with email and password in Firebase Auth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, store user data in Firestore
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    storeUserData(user.getUid(), name, email);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUpActivity.this, "Sign up failed.",
                                        Toast.LENGTH_SHORT).show();
                                enableAllViews();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        });
    }

    private boolean validateInput(String name, String password, String confPassword, String email) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void storeUserData(String userId, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // Document successfully written
                    Toast.makeText(SignUpActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Error writing document
                    Toast.makeText(SignUpActivity.this, "Error registering user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    enableAllViews();
                    progressBar.setVisibility(View.INVISIBLE);
                });
    }

    private void disableAllViews() {
        signInLink.setEnabled(false);
        loginName.setEnabled(false);
        loginPassword.setEnabled(false);
        confirmPassword.setEnabled(false);
        emailBox.setEnabled(false);
        signUpButton.setEnabled(false);
    }

    private void enableAllViews() {
        signInLink.setEnabled(true);
        loginName.setEnabled(true);
        loginPassword.setEnabled(true);
        confirmPassword.setEnabled(true);
        emailBox.setEnabled(true);
        signUpButton.setEnabled(true);
    }
}
