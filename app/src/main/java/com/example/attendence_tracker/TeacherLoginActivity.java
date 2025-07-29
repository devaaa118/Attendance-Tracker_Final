package com.example.attendence_tracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendence_tracker.Model.TeacherInstance;
import com.example.attendence_tracker.RetrofitService.TeacherAPI;
import com.example.attendence_tracker.RetrofitService.RetroFitService;
import com.example.attendence_tracker.TeacherDataStore;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.SharedPreferences;
public class TeacherLoginActivity extends AppCompatActivity {
    // This activity handles login for both teachers and admins
    // The role is determined by the backend response and user is redirected accordingly

    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        // Check persistent login
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String role = prefs.getString("role", "");
        if (isLoggedIn) {
            if ("admin".equalsIgnoreCase(role)) {
                startActivity(new Intent(this, AdminPanelActivity.class));
            } else {
                startActivity(new Intent(this, HomeActivity.class));
            }

            finish();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> performLogin());
    }
    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use backend login endpoint
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        RetroFitService retroFitService = new RetroFitService();
        TeacherAPI teacherAPI = retroFitService.getRetrofit().create(TeacherAPI.class);

        teacherAPI.loginTeacher(credentials).enqueue(new Callback<TeacherInstance>() {
            @Override
            public void onResponse(Call<TeacherInstance> call, Response<TeacherInstance> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeacherInstance teacher = response.body();
                    TeacherDataStore.setCurrentTeacher(teacher);
                    // Save login state
                    SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("userEmail", email);
                    editor.putString("role", teacher.getRole());
                    editor.putString("teacherName", teacher.getTeacherName()); // ✅ ADD THIS
                    editor.putInt("teacherID", teacher.getTeacherID());         // ✅ AND THIS
                    editor.apply();

                    if ("admin".equalsIgnoreCase(teacher.getRole())) {
                        Intent intent = new Intent(TeacherLoginActivity.this, AdminPanelActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(TeacherLoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                    finish();
                } else if (response.code() == 401) {
                    Toast.makeText(TeacherLoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TeacherLoginActivity.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeacherInstance> call, Throwable t) {
                Toast.makeText(TeacherLoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
} 