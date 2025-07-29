package com.example.attendence_tracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendence_tracker.Model.StudentInstance;
import com.example.attendence_tracker.Model.TeacherInstance;
import com.example.attendence_tracker.Model.CourseInstance;
import com.example.attendence_tracker.RetrofitService.RetroFitService;
import com.example.attendence_tracker.RetrofitService.TeacherAPI;
import com.example.attendence_tracker.RetrofitService.CourseAPI;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import java.util.HashSet;
import java.util.Set;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import android.content.Intent;
import android.content.SharedPreferences;

public class AssignCoursesActivity extends AppCompatActivity {
    private Spinner spinnerTeacher;
    private RecyclerView recyclerCourses;
    private Button btnAssign;
    private ProgressBar progressBar;
    private List<TeacherInstance> teacherList = new ArrayList<>();
    private List<CourseInstance> courseList = new ArrayList<>();
    private ArrayAdapter<String> teacherAdapter;
    private CourseAdapter courseAdapter;
    private Set<Integer> selectedCourseIds = new HashSet<>();
    private int selectedTeacherId = -1;

    private List<StudentInstance> students = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_courses);
        
        // Session validation - only admin can assign courses
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String role = prefs.getString("role", "");
        
        if (!isLoggedIn || !"admin".equalsIgnoreCase(role)) {
            Intent intent = new Intent(AssignCoursesActivity.this, TeacherLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        progressBar = findViewById(R.id.progressBar);
        spinnerTeacher = findViewById(R.id.spinnerTeacher);
        recyclerCourses = findViewById(R.id.recyclerCourses);
        btnAssign = findViewById(R.id.btnAssign);

        recyclerCourses.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseAdapter(courseList, selectedCourseIds);
        recyclerCourses.setAdapter(courseAdapter);

        fetchTeachers();
        fetchCourses();
        loadStudents();

        spinnerTeacher.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (teacherList.size() > position) {
                    selectedTeacherId = teacherList.get(position).getTeacherID();
                    fetchAssignedCourses(selectedTeacherId);
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnAssign.setOnClickListener(v -> {
            if (selectedTeacherId == -1) {
                Snackbar.make(btnAssign, "Please select a teacher", Snackbar.LENGTH_SHORT).show();
                return;
            }
            assignCoursesToTeacher(selectedTeacherId, new ArrayList<>(selectedCourseIds));
        });
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
                    List<String> teacherNames = new ArrayList<>();
                    for (TeacherInstance t : teacherList) {
                        teacherNames.add(t.getTeacherName());
                    }
                    teacherAdapter = new ArrayAdapter<>(AssignCoursesActivity.this, android.R.layout.simple_spinner_item, teacherNames);
                    teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTeacher.setAdapter(teacherAdapter);
                } else {
                    Toast.makeText(AssignCoursesActivity.this, "Failed to load teachers", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<TeacherInstance>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignCoursesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCourses() {
        progressBar.setVisibility(View.VISIBLE);
        CourseAPI courseAPI = new RetroFitService().getRetrofit().create(CourseAPI.class);
        courseAPI.getCourse().enqueue(new Callback<List<CourseInstance>>() {
            @Override
            public void onResponse(Call<List<CourseInstance>> call, Response<List<CourseInstance>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    courseList.clear();
                    courseList.addAll(response.body());
                    courseAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AssignCoursesActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<CourseInstance>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignCoursesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                selectedCourseIds.clear();
                if (response.isSuccessful() && response.body() != null) {
                    for (CourseInstance c : response.body()) {
                        selectedCourseIds.add(c.getCourseID());
                    }
                }
                courseAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<CourseInstance>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AssignCoursesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignCoursesToTeacher(int teacherId, List<Integer> courseIds) {
        progressBar.setVisibility(View.VISIBLE);
        TeacherAPI teacherAPI = new RetroFitService().getRetrofit().create(TeacherAPI.class);
        teacherAPI.assignCoursesToTeacher(teacherId, courseIds).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Snackbar.make(btnAssign, "Courses assigned successfully!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(btnAssign, "Failed to assign courses", Snackbar.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(btnAssign, "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void loadStudents() {
        progressBar.setVisibility(View.VISIBLE);
        StudentRepository.getInstance().getStudents(this, new StudentRepository.StudentCallback() {
            @Override
            public void onSuccess(List<StudentInstance> loadedStudents) {
                students.clear();
                students.addAll(loadedStudents);
                // Update UI if needed
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(AssignCoursesActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // RecyclerView Adapter for courses with checkboxes
    private class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
        private List<CourseInstance> courses;
        private Set<Integer> selectedIds;
        CourseAdapter(List<CourseInstance> courses, Set<Integer> selectedIds) {
            this.courses = courses;
            this.selectedIds = selectedIds;
        }
        @Override
        public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_checkbox, parent, false);
            return new CourseViewHolder(view);
        }
        @Override
        public void onBindViewHolder(CourseViewHolder holder, int position) {
            CourseInstance course = courses.get(position);
            holder.checkBox.setText(course.getCourseName() + " (" + course.getCourseSemester() + ")");
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(selectedIds.contains(course.getCourseID()));
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) selectedIds.add(course.getCourseID());
                else selectedIds.remove(course.getCourseID());
            });
        }
        @Override
        public int getItemCount() { return courses.size(); }
        class CourseViewHolder extends RecyclerView.ViewHolder {
            CheckBox checkBox;
            CourseViewHolder(View itemView) {
                super(itemView);
                checkBox = itemView.findViewById(R.id.checkboxCourse);
            }
        }
    }
}