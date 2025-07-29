package com.example.attendence_tracker;

import android.content.SharedPreferences;
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
import android.widget.ProgressBar;import com.example.attendence_tracker.Model.StudentInstance;
import com.example.attendence_tracker.RetrofitService.StudentAPI;
import com.example.attendence_tracker.RetrofitService.RetroFitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
import com.example.attendence_tracker.TeacherLoginActivity;

public class AddStudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Student");
        }

        TextInputEditText etName = findViewById(R.id.etStudentName);
        TextInputEditText etID = findViewById(R.id.etStudentID);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button btnSubmit = findViewById(R.id.btnSubmitStudent);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                String id = etID.getText() != null ? etID.getText().toString().trim() : "";
                if (name.isEmpty()) {
                    etName.setError("Student name required");
                    etName.requestFocus();
                    return;
                }
                if (id.isEmpty()) {
                    etID.setError("Student ID required");
                    etID.requestFocus();
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

                // Connect to backend
                StudentInstance student = new StudentInstance(id, name);
                StudentAPI studentAPI = new RetroFitService().getRetrofit().create(StudentAPI.class);
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String adminEmail = prefs.getString("userEmail", "");
                studentAPI.addstudent(student, adminEmail).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        if (response.isSuccessful()) {
                            Snackbar.make(btnSubmit, "Student added!", Snackbar.LENGTH_LONG).show();
                            finish();
                        } else {
                            Snackbar.make(btnSubmit, "Failed to add student", Snackbar.LENGTH_LONG).show();
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

    // Removed course-related code

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}