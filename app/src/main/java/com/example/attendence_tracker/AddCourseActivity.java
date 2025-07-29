package com.example.attendence_tracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.view.MenuItem;
import android.widget.ProgressBar;
import com.example.attendence_tracker.Model.CourseInstance;
import com.example.attendence_tracker.RetrofitService.CourseAPI;
import com.example.attendence_tracker.RetrofitService.RetroFitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.SharedPreferences;
import android.content.Intent;
import com.example.attendence_tracker.TeacherLoginActivity;
public class AddCourseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        // Session validation - only admin can add courses
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String role = prefs.getString("role", "");
        
        if (!isLoggedIn || !"admin".equalsIgnoreCase(role)) {
            Intent intent = new Intent(AddCourseActivity.this, TeacherLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Course");
        }

        TextInputEditText etName = findViewById(R.id.etCourseName);
        TextInputEditText etSemester = findViewById(R.id.etCourseSemester);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button btnSubmit = findViewById(R.id.btnSubmitCourse);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                String semester = etSemester.getText() != null ? etSemester.getText().toString().trim() : "";
                if (name.isEmpty()) {
                    etName.setError("Course name required");
                    etName.requestFocus();
                    return;
                }
                if (semester.isEmpty()) {
                    etSemester.setError("Semester required");
                    etSemester.requestFocus();
                    return;
                }
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                // Show progress
                progressBar.setVisibility(View.VISIBLE);
                btnSubmit.setEnabled(false);

                // Get admin email from SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String adminEmail = prefs.getString("userEmail", "");

                // Connect to backend
                CourseInstance course = new CourseInstance(0, name, semester);
                CourseAPI courseAPI = new RetroFitService().getRetrofit().create(CourseAPI.class);
                courseAPI.addCourse(course, adminEmail).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        if (response.isSuccessful()) {
                            Snackbar.make(btnSubmit, "Course added!", Snackbar.LENGTH_LONG).show();
                            finish();
                        } else {
                            Snackbar.make(btnSubmit, "Failed to add course", Snackbar.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        Snackbar.make(btnSubmit, "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }); }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 