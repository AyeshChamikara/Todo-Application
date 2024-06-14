package com.example.todaytask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserInformationActivity extends AppCompatActivity {

    private TextView fullNameTextView;
    private EditText fullNameEditText, passwordEditText, newPasswordEditText;
    private Button editButton;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        fullNameTextView = findViewById(R.id.textView12);
        TextView emailTextView = findViewById(R.id.textView10);
        fullNameEditText = findViewById(R.id.editTextText);
        passwordEditText = findViewById(R.id.editTextText2);
        newPasswordEditText = findViewById(R.id.editTextText5);
        editButton = findViewById(R.id.button4);
        Button logOutButton = findViewById(R.id.logOutButton);

        ImageButton imageButton = findViewById(R.id.imageButton);

        SharedPreferences sharedPreferences = getSharedPreferences("userLoginStatus", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "");
        String userEmail = sharedPreferences.getString("userEmail", "");

        fullNameTextView.setText(userName);
        emailTextView.setText(userEmail);
        fullNameEditText.setText(userName);

        disableFields();

        imageButton.setOnClickListener(v -> {
            startActivity(new Intent(UserInformationActivity.this, HomeActivity.class));
            finish();
        });

        editButton.setOnClickListener(v -> {
            if (editButton.getText().toString().equals("Edit My Info")) {
                enableFields();
                editButton.setText("Save Changes");
            } else {
                // Save changes
                String newName = fullNameEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();

                if (!newName.isEmpty() && !newName.equals(userName)) {
                    updateUserName(newName);
                }

                if (!newPassword.isEmpty()) {
                    updatePassword(newPassword);
                }

                disableFields();
                editButton.setText("Edit My Info");
            }
        });

        logOutButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferencesLogout = getSharedPreferences("userLoginStatus", Context.MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferencesLogout.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            Toast.makeText(UserInformationActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserInformationActivity.this, MainActivity.class));
            finish();
        });
    }

    private void disableFields() {
        fullNameEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        newPasswordEditText.setEnabled(false);
    }

    private void enableFields() {
        fullNameEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        newPasswordEditText.setEnabled(true);
    }

    private void updateUserName(String newName) {
        SharedPreferences sharedPreferences = getSharedPreferences("userLoginStatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", newName);
        editor.apply();

        // Update user's name in Firestore
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        userRef.update("userName", newName)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserInformationActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                        fullNameTextView.setText(newName); // Update TextView
                    } else {
                        Toast.makeText(UserInformationActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePassword(String newPassword) {
        currentUser.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserInformationActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserInformationActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
