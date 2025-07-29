package com.example.attendence_tracker;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendence_tracker.Model.StudentInstance;
import com.example.attendence_tracker.StudentDataStore; // 👈 this is your global holder

import java.util.List;
import android.content.Intent;
import android.content.SharedPreferences;

public class StudentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        // Session validation
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String teacherName = prefs.getString("teacherName", null);
        int teacherID = prefs.getInt("teacherID", -1);

        if (!isLoggedIn || teacherName == null || teacherID == -1) {
            Intent intent = new Intent(StudentListActivity.this, TeacherLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        recyclerView = findViewById(R.id.attendanceRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchStudents();
    }

    private void fetchStudents() {
        StudentRepository.getInstance().getStudents(this, new StudentRepository.StudentCallback() {
            @Override
            public void onSuccess(List<StudentInstance> students) {
                // If your adapter expects StudentInstance, convert here if needed
                // Otherwise, update StudentRepository and API to use StudentInstance everywhere
                // For now, assuming Student == StudentInstance
                StudentListAdapter adapter = new StudentListAdapter(students);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(StudentListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
          
