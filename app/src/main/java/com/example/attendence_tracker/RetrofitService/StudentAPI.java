package com.example.attendence_tracker.RetrofitService;

import com.example.attendence_tracker.Model.StudentInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StudentAPI {

    @GET("/Students/GetStudents")
    Call<List<StudentInstance>> GetStudents();

    @POST("/Students/AddStudents")
    Call<Void> addstudent(@Body StudentInstance student, @Query("adminEmail") String adminEmail);

    @GET("Student/GetByCourse/{courseID}")
    Call<List<StudentInstance>> getStudentsByCourse(@Path("courseID") int courseID);


}

