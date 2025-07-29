package com.example.attendence_tracker.RetrofitService;

import com.example.attendence_tracker.Model.CourseInstance;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CourseAPI {
    @GET("Course/GetCourse")
    Call<List<CourseInstance>> getCourse();

    @POST("Course/AddCourse")
    Call<Void> addCourse(@Body CourseInstance course, @Query("adminEmail") String adminEmail);
} 