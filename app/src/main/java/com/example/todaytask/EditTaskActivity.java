package com.example.todaytask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditTaskActivity extends AppCompatActivity {

    private EditText taskName, description, taskDate, taskTime;
    private Button button4;
    private DatabaseHelper dbHelper;
    private String taskId;
    private static final String TAG = "EditTaskActivity";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        taskName = findViewById(R.id.taskName);
        description = findViewById(R.id.description);
        taskDate = findViewById(R.id.taskDate);
        taskTime = findViewById(R.id.taskTime);
        button4 = findViewById(R.id.button4);

        ImageButton imageButton = findViewById(R.id.imageButton);

        dbHelper = new DatabaseHelper(this);

        taskId = getIntent().getStringExtra("EVENT_ID");
        if (taskId == null) {
            Log.e(TAG, "No EVENT_ID passed in intent");
            finish();
            return;
        }

        loadTaskDetails(taskId);

        disableFields();

        button4.setOnClickListener(v -> {
            if (button4.getText().toString().equals("Edit Task")) {
                enableFields();
                button4.setText("Save Task");
            } else {
                if (validateInputs()) {
                    updateTask(taskId);
                    button4.setText("Edit Task");
                    disableFields();
                }
            }
        });

        imageButton.setOnClickListener(v -> {
            startActivity(new Intent(EditTaskActivity.this, HomeActivity.class));
            finish();
        });
    }

    @SuppressLint("Range")
    private void loadTaskDetails(String taskId) {
        Cursor cursor = dbHelper.getTaskById(taskId);
        if (cursor != null && cursor.moveToFirst()) {
            taskName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_NAME)));
            description.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION)));
            taskDate.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_DATE)));
            taskTime.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_TIME)));
            cursor.close();
        } else {
            Log.e(TAG, "Task not found with id: " + taskId);
        }
    }

    private void disableFields() {
        taskName.setEnabled(false);
        description.setEnabled(false);
        taskDate.setEnabled(false);
        taskTime.setEnabled(false);
    }

    private void enableFields() {
        taskName.setEnabled(true);
        description.setEnabled(true);
        taskDate.setEnabled(true);
        taskTime.setEnabled(true);
    }

    private boolean validateInputs() {
        String name = taskName.getText().toString().trim();
        String desc = description.getText().toString().trim();
        String date = taskDate.getText().toString().trim();
        String time = taskTime.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (desc.isEmpty()) {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (date.isEmpty()) {
            Toast.makeText(this, "Invalid date format. Use yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (time.isEmpty()) {
            Toast.makeText(this, "Invalid time format. Use HH:mm", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateTask(String taskId) {
        String name = taskName.getText().toString().trim();
        String desc = description.getText().toString().trim();
        String date = taskDate.getText().toString().trim();
        String time = taskTime.getText().toString().trim();

        boolean isUpdated = dbHelper.updateTask(taskId, name, desc, date, time);
        if (isUpdated) {
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show();
        }
    }
}
