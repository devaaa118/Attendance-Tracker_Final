package com.example.attendence_tracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendence_tracker.Model.AttendanceInstance;
import com.example.attendence_tracker.Model.PeriodInstance;
import com.example.attendence_tracker.Model.StudentInstance;
import com.example.attendence_tracker.RetrofitService.AttendanceAPI;
import com.example.attendence_tracker.RetrofitService.RetroFitService;
import com.example.attendence_tracker.RetrofitService.StudentAPI;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarkAttendanceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendanceMarkAdapter adapter;
    private Button btnSubmit;

    private RecyclerView recyclerViewDates;
    private DateCardAdapter dateCardAdapter;
    private int courseID;
    private ArrayList<PeriodInstance> periodList;
    private PeriodInstance selectedPeriod;
    private List<StudentInstance> studentList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String teacherName = prefs.getString("teacherName", null);
        int teacherID = prefs.getInt("teacherID", -1);

        if (!isLoggedIn || teacherName == null || teacherID == -1) {
            Intent intent = new Intent(MarkAttendanceActivity.this, TeacherLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        recyclerViewDates = findViewById(R.id.recyclerViewDates);
        recyclerViewDates.setLayoutManager(new LinearLayoutManager(this));
        dateCardAdapter = new DateCardAdapter();
        recyclerViewDates.setAdapter(dateCardAdapter);

        recyclerView = findViewById(R.id.recyclerViewAttendance);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnSubmit = findViewById(R.id.btnSubmitAttendance);
        btnSubmit.setVisibility(View.GONE);

        courseID = getIntent().getIntExtra("courseID", -1);
        periodList = getIntent().getParcelableArrayListExtra("periodList");

        if (courseID == -1 || periodList == null || periodList.isEmpty()) {
            Toast.makeText(this, "No periods available for this course", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dateCardAdapter.setData(periodList, null, period -> {
            selectedPeriod = period;
            recyclerViewDates.setVisibility(View.GONE);
            fetchAttendanceOrFreshList();
        });
    }

    private void fetchAttendanceOrFreshList() {
        RetroFitService retroFitService = new RetroFitService();
        AttendanceAPI attendanceAPI = retroFitService.getRetrofit().create(AttendanceAPI.class);

        String date = selectedPeriod.getDate();

        attendanceAPI.getAttendanceForCourseAndDate(courseID, date)
                .enqueue(new Callback<List<AttendanceInstance>>() {
                    @Override
                    public void onResponse(Call<List<AttendanceInstance>> call, Response<List<AttendanceInstance>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            adapter = new AttendanceMarkAdapter(response.body(), courseID, date);
                            recyclerView.setAdapter(adapter);
                            btnSubmit.setText("Update Attendance");
                            recyclerView.setVisibility(View.VISIBLE);
                            btnSubmit.setVisibility(View.VISIBLE);
                            setupUpdateButton();
                        } else {
                            fetchAndShowAllStudentsFresh(date);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AttendanceInstance>> call, Throwable t) {
                        Toast.makeText(MarkAttendanceActivity.this, "Error fetching attendance", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchAndShowAllStudentsFresh(String date) {
        StudentAPI studentAPI = new RetroFitService().getRetrofit().create(StudentAPI.class);

        studentAPI.getStudentsByCourse(courseID).enqueue(new Callback<List<StudentInstance>>() {
            @Override
            public void onResponse(Call<List<StudentInstance>> call, Response<List<StudentInstance>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    studentList = response.body();

                    List<AttendanceInstance> freshList = new ArrayList<>();
                    for (StudentInstance student : studentList) {
                        AttendanceInstance instance = new AttendanceInstance();
                        instance.setStudentID(student.getStudentId());
                        instance.setStudentName(student.getName());
                        instance.setCourseID(courseID);
                        instance.setAttendanceDate(date);
                        instance.setAttendanceStatus(false);
                        freshList.add(instance);
                    }

                    adapter = new AttendanceMarkAdapter(freshList, courseID, date);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    setupSubmitButton();
                } else {
                    Toast.makeText(MarkAttendanceActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<StudentInstance>> call, Throwable t) {
                Toast.makeText(MarkAttendanceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(v -> {
            List<AttendanceInstance> attendanceList = adapter.getattendanceList();
            for (AttendanceInstance instance : attendanceList) {
                instance.setAttendanceDate(selectedPeriod.getDate());
            }

            RetroFitService retroFitService = new RetroFitService();
            AttendanceAPI attendanceAPI = retroFitService.getRetrofit().create(AttendanceAPI.class);
            for (AttendanceInstance instance : attendanceList) {
                attendanceAPI.PostAttendance(instance).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        // success
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // failure
                    }
                });
            }
            Toast.makeText(this, "Attendance submitted!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setupUpdateButton() {
        btnSubmit.setOnClickListener(v -> {
            List<AttendanceInstance> attendanceList = adapter.getattendanceList();
            for (AttendanceInstance instance : attendanceList) {
                instance.setAttendanceDate(selectedPeriod.getDate());
            }

            RetroFitService retroFitService = new RetroFitService();
            AttendanceAPI attendanceAPI = retroFitService.getRetrofit().create(AttendanceAPI.class);
            attendanceAPI.updateAttendance(attendanceList).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(MarkAttendanceActivity.this, "Attendance updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(MarkAttendanceActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            });


        });
    }

    private List<LocalDate> getLastNDatesForDay(String dayOfWeekStr, int n) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate d = today;
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr.toUpperCase());
        while (dates.size() < n) {
            if (d.getDayOfWeek() == dayOfWeek && !d.isAfter(today)) {
                dates.add(d);
            }
            d = d.minusDays(1);
        }
        return dates;
    }
}
