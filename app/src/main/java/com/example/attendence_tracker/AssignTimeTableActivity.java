// AssignTimeTableActivity.java
package com.example.attendence_tracker;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendence_tracker.Model.CourseInstance;
import com.example.attendence_tracker.Model.TeacherInstance;
import com.example.attendence_tracker.Model.TimeTableEntry;
import com.example.attendence_tracker.RetrofitService.RetroFitService;
import com.example.attendence_tracker.RetrofitService.TeacherAPI;
import com.example.attendence_tracker.RetrofitService.TimeTableAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
import android.content.SharedPreferences;

public class AssignTimeTableActivity extends AppCompatActivity {

    private Spinner spinnerTeacher, spinnerCourse, spinnerDay;
    private EditText etStartTime, etEndTime;
    private Button btnAssign;
    private ProgressBar progressBar;

    private List<TeacherInstance> teacherList = new ArrayList<>();
    private List<CourseInstance> courseList = new ArrayList<>();
    private ArrayAdapter<String> teacherAdapter, courseAdapter;
    private int selectedTeacherId = -1;
    private int selectedCourseId = -1;

    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_timetable);

        // Session validation - only admin can assign timetables
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String role = prefs.getString("role", "");
        
        if (!isLoggedIn || !"admin".equalsIgnoreCase(role)) {
            Intent intent = new Intent(AssignTimeTableActivity.this, TeacherLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

         spinnerTeacher = findViewById(R.id.spinnerTeacher);
        spinnerCourse = findViewById(R.id.spinnerCourse);
        spinnerDay = findViewById(R.id.spinnerDay);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        btnAssign = findViewById(R.id.btnAssign);
        progressBar = findViewById(R.id.progressBar);

        spinnerDay.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days));

        fetchTeachers();

        spinnerTeacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTeacherId = teacherList.get(position).getTeacherID();
                fetchAssignedCourses(selectedTeacherId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!courseList.isEmpty())
                    selectedCourseId = courseList.get(position).getCourseID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAssign.setOnClickListener(v -> assignTimetable());
    }

    private void fetchTeachers() {
        progressBar.setVisibility(View.VISIBLE);
        TeacherAPI teacherAPI = new RetroFitService().getRetrofit().create(TeacherAPI.class);
        teacherAPI.getAllTeachers().enqueue(new Callback<List<TeacherInstance>>() {
            @Override
            public void onResponse(Call<List<TeacherInstance>> call, Response<List<TeacherInstance>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    teacherList = response.body();
                    List<String> names = new ArrayList<>();
                    for (TeacherInstance t : teacherList) names.add(t.getTeacherName());
                    teacherAdapter = new ArrayAdapter<>(AssignTimeTableActivity.this, android.R.layout.simple_spinner_item, names);
                    spinnerTeacher.setAdapter(teacherAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<TeacherInstance>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignTimeTableActivity.this, "Failed to load teachers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAssignedCourses(int teacherId) {
        progressBar.setVisibility(View.VISIBLE);
        TeacherAPI teacherAPI = new RetroFitService().getRetrofit().create(TeacherAPI.class);
        teacherAPI.getTeacherCourses(teacherId).enqueue(new Callback<List<CourseInstance>>() {
            @Override
            public void onResponse(Call<List<CourseInstance>> call, Response<List<CourseInstance>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    courseList = response.body();
                    List<String> names = new ArrayList<>();
                    for (CourseInstance c : courseList) names.add(c.getCourseName());
                    courseAdapter = new ArrayAdapter<>(AssignTimeTableActivity.this, android.R.layout.simple_spinner_item, names);
                    spinnerCourse.setAdapter(courseAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<CourseInstance>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignTimeTableActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignTimetable() {
        String day = spinnerDay.getSelectedItem().toString();
        String start = etStartTime.getText().toString();
        String end = etEndTime.getText().toString();

        if (selectedTeacherId == -1 || selectedCourseId == -1 || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        TimeTableEntry entry = new TimeTableEntry(0, selectedTeacherId, selectedCourseId, day, start, end);

        TimeTableAPI timeTableAPI = new RetroFitService().getRetrofit().create(TimeTableAPI.class);
        timeTableAPI.assignTimetable(entry).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AssignTimeTableActivity.this, "Timetable assigned!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AssignTimeTableActivity.this, "Failed to assign timetable", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AssignTimeTableActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
