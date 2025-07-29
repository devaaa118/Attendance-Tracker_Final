package com.example.attendence_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.attendence_tracker.Model.TeacherInstance;
import com.example.attendence_tracker.TeacherDataStore;

import android.content.SharedPreferences;

public class HomeActivity extends AppCompatActivity {

    TextView welcomeText;
    Button btnMarkAttendance, btnViewAttendance, btnStudentList, btnViewTimetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String teacherName = prefs.getString("teacherName", null);
        int teacherID = prefs.getInt("teacherID", -1);

        // Handle session validation
        if (!isLoggedIn || teacherName == null || teacherID == -1) {
            Intent intent = new Intent(HomeActivity.this, TeacherLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Restore session manually into memory
        TeacherInstance teacher = new TeacherInstance();
        teacher.setTeacherName(teacherName);
        teacher.setTeacherID(teacherID);
        TeacherDataStore.setCurrentTeacher(teacher);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            toolbar.setNavigationIcon(android.R.drawable.ic_menu_myplaces);
            toolbar.setNavigationOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }

        // UI refs
        welcomeText = findViewById(R.id.welcomeText);
        btnMarkAttendance = findViewById(R.id.btnMarkAttendance);
        btnViewAttendance = findViewById(R.id.btnViewAttendance);
        btnStudentList = findViewById(R.id.btnStudentList);
        Button btnViewTimetable = findViewById(R.id.btnViewTimetable);

        welcomeText.setText("Welcome, " + teacher.getTeacherName() + "!");

        // Button Actions
        btnMarkAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ClassSelectionActivity.class);
            startActivity(intent);
        });

        btnViewAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ClassSelectionActivity.class);
            intent.putExtra("forViewAttendance", true);
            startActivity(intent);
        });

        btnStudentList.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, StudentListActivity.class));
        });

        btnViewTimetable.setOnClickListener(v -> {
            int teacherID1 = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("teacherID", -1);
            Intent intent = new Intent(HomeActivity.this, TimeTableActivity.class);
            intent.putExtra("teacherID", teacherID1);
            startActivity(intent);
        });

    }
}
