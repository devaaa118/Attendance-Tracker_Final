package com.example.attendence_tracker.RetrofitService;

import com.example.attendence_tracker.Model.TeacherInstance;
import com.example.attendence_tracker.Model.CourseInstance;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TeacherAPI {

    @POST("/teacher/login")
    Call<TeacherInstance> loginTeacher(@Body java.util.Map<String, String> credentials);

    @GET("/teacher/{teacherID}/teacherCourses")
    Call<List<CourseInstance>> getTeacherCourses(@Path("teacherID") int teacherID);

    @GET("/teacher/getTeacherByEmail/{email}")
    Call<TeacherInstance> getTeacherByEmail(@Path("email") String email);

    @GET("/teacher/getTeacherWithCourses/{teacherID}")
    Call<TeacherInstance> getTeacherWithCourses(@Path("teacherID") int teacherID);

    @GET("/teacher/getAllTeachers")
    Call<List<TeacherInstance>> getAllTeachers();

    @POST("/teacher/add")
    Call<TeacherInstance> addTeacher(@Body TeacherInstance teacher, @Query("adminEmail") String adminEmail);

    @POST("/teacher/{teacherId}/assignCourses")
    Call<Void> assignCoursesToTeacher(@Path("teacherId") int teacherId, @Body List<Integer> courseIds);

}