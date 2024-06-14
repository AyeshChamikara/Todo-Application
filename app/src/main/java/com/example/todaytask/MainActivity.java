package com.example.todaytask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button signButton;
    private ProgressBar progressBar;
    private EditText loginEmailTextView;
    private EditText loginPasswordTextView;
    private TextView loginSignupLinkTextView;
    private TextView aboutDeveloperTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.loginSpinnerBar);
        signButton = findViewById(R.id.button);
        aboutDeveloperTextView = findViewById(R.id.developer_option);
        loginEmailTextView = findViewById(R.id.loginEmail);
        loginPasswordTextView = findViewById(R.id.loginPassword);
        loginSignupLinkTextView = findViewById(R.id.login_sinup_link);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar.setVisibility(View.INVISIBLE);

        loginSignupLinkTextView.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            disableAllViews();

            new Handler().postDelayed(() -> {
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                finish();
            }, 3000); // Delay in milliseconds (3 seconds)
        });

        // Set click listener to the about developer textview
        aboutDeveloperTextView.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            disableAllViews();

            // Navigate to another screen after 3 seconds
            new Handler().postDelayed(() -> {
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(MainActivity.this, AboutDeveloper.class));
                finish(); // Optionally finish the current activity
            }, 3000); // Delay in milliseconds (3 seconds)
        });

        signButton.setOnClickListener(v -> {
            String email = loginEmailTextView.getText().toString().trim();
            String password = loginPasswordTextView.getText().toString().trim();

            if (validateInput(email, password)) {
                signInUser(email, password);
            }
        });
    }

    private boolean validateInput(String email, String password) {
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
        return true;
    }

    private void signInUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        disableAllViews();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, retrieve user data from Firestore
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            fetchUserData(user.getUid(), email);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        enableAllViews();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void fetchUserData(String userId, String email) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        String name = document.getString("name");

                        // Save user details in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("userLoginStatus", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("userEmail", email);
                        editor.putString("userName", name);
                        editor.apply();

                        Toast.makeText(MainActivity.this, "Log in successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to retrieve user data.",
                                Toast.LENGTH_SHORT).show();
                        enableAllViews();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    // Method to disable all views
    private void disableAllViews() {
        signButton.setEnabled(false);
        loginEmailTextView.setEnabled(false);
        loginPasswordTextView.setEnabled(false);
        loginSignupLinkTextView.setEnabled(false);
        aboutDeveloperTextView.setEnabled(false);
    }

    private void enableAllViews() {
        signButton.setEnabled(true);
        loginEmailTextView.setEnabled(true);
        loginPasswordTextView.setEnabled(true);
        loginSignupLinkTextView.setEnabled(true);
        aboutDeveloperTextView.setEnabled(true);
    }
}
