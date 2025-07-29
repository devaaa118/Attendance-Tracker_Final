package com.example.attendence_tracker;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.attendence_tracker.Model.TeacherInstance;
import com.example.attendence_tracker.RetrofitService.TeacherAPI;
import com.example.attendence_tracker.RetrofitService.RetroFitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.SharedPreferences;
import android.content.Intent;
import com.example.attendence_tracker.TeacherLoginActivity;
public class AddTeacherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        // Session validation - only admin can add teachers
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String role = prefs.getString("role", "");
        
        if (!isLoggedIn || !"admin".equalsIgnoreCase(role)) {
            Intent intent = new Intent(AddTeacherActivity.this, TeacherLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Teacher");
        }

        TextInputEditText etName = findViewById(R.id.etTeacherName);
        TextInputEditText etEmail = findViewById(R.id.etTeacherEmail);
        TextInputEditText etPassword = findViewById(R.id.etTeacherPassword);
        Spinner spinnerRole = findViewById(R.id.spinnerRole);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button btnSubmit = findViewById(R.id.btnSubmitTeacher);

        // Set up spinner for role
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"teacher", "admin"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
                String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
                String role = spinnerRole.getSelectedItem().toString();
                if (name.isEmpty()) {
                    etName.setError("Teacher name required");
                    etName.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    etEmail.setError("Email required");
                    etEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    etPassword.setError("Password required");
                    etPassword.requestFocus();
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
                TeacherInstance teacher = new TeacherInstance(0, name, email, password, role);
                TeacherAPI teacherAPI = new RetroFitService().getRetrofit().create(TeacherAPI.class);
                teacherAPI.addTeacher(teacher, adminEmail).enqueue(new Callback<TeacherInstance>() {
                    @Override
                    public void onResponse(Call<TeacherInstance> call, Response<TeacherInstance> response) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        if (response.isSuccessful()) {
                            Snackbar.make(btnSubmit, "Teacher added!", Snackbar.LENGTH_LONG).show();
                            finish();
                        } else {
                            Snackbar.make(btnSubmit, "Failed to add teacher", Snackbar.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<TeacherInstance> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        Snackbar.make(btnSubmit, "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 