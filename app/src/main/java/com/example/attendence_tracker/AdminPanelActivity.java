package com.example.attendence_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import com.google.android.material.button.MaterialButton;
import androidx.appcompat.widget.Toolbar;

public class AdminPanelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String role = prefs.getString("role", "");
        if (!isLoggedIn || !"admin".equalsIgnoreCase(role)) {
            Intent intent = new Intent(AdminPanelActivity.this, TeacherLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            toolbar.setNavigationIcon(android.R.drawable.ic_menu_myplaces);
            toolbar.setNavigationOnClickListener(v -> {
                Intent intent = new Intent(AdminPanelActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }

        Button btnAddStudent = findViewById(R.id.btnAddStudent);
        Button btnAddTeacher = findViewById(R.id.btnAddTeacher);
        Button btnAddCourse = findViewById(R.id.btnAddCourse);
        Button btnAssignCourses = findViewById(R.id.btnAssignCourses);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnAddStudent.setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, AddStudentActivity.class));
        });
        btnAddTeacher.setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, AddTeacherActivity.class));
        });
        btnAddCourse.setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, AddCourseActivity.class));
        });
        btnAssignCourses.setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, AssignCoursesActivity.class));
        });
        MaterialButton btnAssignTimetable = findViewById(R.id.btnAssignTimetable);
        btnAssignTimetable.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this, AssignTimeTableActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Clear in-memory teacher data
            TeacherDataStore.clear();
            
            // Clear SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            
            // Navigate to login screen and clear activity stack
            Intent intent = new Intent(AdminPanelActivity.this, TeacherLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
} 