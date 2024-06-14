package com.example.todaytask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private View dialogLayout;
    private TextView tvLogoutMessage;
    private Button btnYes;
    private LinearLayout eventsContainer;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton addTaskButton = findViewById(R.id.addTaskButton);
        Button logOutButton = findViewById(R.id.logOutButton);

        dialogLayout = findViewById(R.id.logoutConfirmationDialog);
        tvLogoutMessage = dialogLayout.findViewById(R.id.tvLogoutMessage);
        btnYes = dialogLayout.findViewById(R.id.btnYes); // Fixed: Initialize btnYes
        Button btnNo = dialogLayout.findViewById(R.id.btnNo);

        ImageView UserProfileImage = findViewById(R.id.UserProfileImage);

        eventsContainer = findViewById(R.id.eventsContainer);
        dbHelper = new DatabaseHelper(this);

        loadEvents();

        addTaskButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AddNewEventActivity.class));
            finish();
        });

        UserProfileImage.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, UserInformationActivity.class));
            finish();
        });

        logOutButton.setOnClickListener(v -> showLogoutDialog(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("userLoginStatus", Context.MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply(); // Fixed: Apply changes to SharedPreferences

            Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
        }));

        btnNo.setOnClickListener(v -> dialogLayout.setVisibility(View.GONE));
    }

    @SuppressLint("SetTextI18n")
    private void showLogoutDialog(Runnable onYesAction) {
        tvLogoutMessage.setText("Are you sure you want to log out?");
        dialogLayout.setVisibility(View.VISIBLE);
        btnYes.setOnClickListener(v -> {
            dialogLayout.setVisibility(View.GONE);
            onYesAction.run();
        });
    }

    private void loadEvents() {
        eventsContainer.removeAllViews();
        Cursor cursor = dbHelper.getAllTasks();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_NAME));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_DATE));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_TIME));

                View eventView = LayoutInflater.from(this).inflate(R.layout.event_item, eventsContainer, false);

                TextView tvEventTitle = eventView.findViewById(R.id.tvEventTitle);
                TextView tvEventDate = eventView.findViewById(R.id.tvEventDate);
                TextView tvEventTime = eventView.findViewById(R.id.tvEventTime);
                ImageButton btnEdit = eventView.findViewById(R.id.btnEdit);
                ImageButton btnDelete = eventView.findViewById(R.id.btnDelete);

                tvEventTitle.setText(title);
                tvEventDate.setText(date);
                tvEventTime.setText(time);

                btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(HomeActivity.this, EditTaskActivity.class);
                    intent.putExtra("EVENT_ID", id);
                    startActivity(intent);
                });

                btnDelete.setOnClickListener(v -> {
                    dbHelper.deleteTask(id);
                    loadEvents();
                });

                eventsContainer.addView(eventView);
            } while (cursor.moveToNext());

            cursor.close();
        }
    }
}
