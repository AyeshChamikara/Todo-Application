package com.example.todaytask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddNewEventActivity extends AppCompatActivity {

    private EditText taskName;
    private EditText description;
    private EditText taskDate;
    private EditText taskTime;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        Button addTaskButton = findViewById(R.id.addTaskButton);

        ImageButton imageButton = findViewById(R.id.imageButton);

        taskName = findViewById(R.id.taskName);
        description = findViewById(R.id.description);
        taskDate = findViewById(R.id.taskDate);
        taskTime = findViewById(R.id.taskTime);

        dbHelper = new DatabaseHelper(this);

        addTaskButton.setOnClickListener(v -> {
                startActivity(new Intent(AddNewEventActivity.this, HomeActivity.class));
            finish();
        });

        imageButton.setOnClickListener(v -> {
            startActivity(new Intent(AddNewEventActivity.this, HomeActivity.class));
            finish();
        });

        addTaskButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String name = taskName.getText().toString().trim();
                String desc = description.getText().toString().trim();
                String date = taskDate.getText().toString().trim();
                String time = taskTime.getText().toString().trim();

                boolean isInserted = dbHelper.insertTask(name, desc, date, time);
                if (isInserted) {
                    Toast.makeText(AddNewEventActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AddNewEventActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(AddNewEventActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            Toast.makeText(this, "Date cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (time.isEmpty()) {
            Toast.makeText(this, "Time cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
