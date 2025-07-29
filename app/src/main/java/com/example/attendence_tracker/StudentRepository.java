package com.example.attendence_tracker;

import android.content.Context;
import com.example.attendence_tracker.Model.StudentInstance;
import com.example.attendence_tracker.RetrofitService.RetroFitService;
import com.example.attendence_tracker.RetrofitService.StudentAPI;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentRepository {
    private static StudentRepository instance;
    private List<StudentInstance> cachedStudents;

    private StudentRepository() {}

    public static synchronized StudentRepository getInstance() {
        if (instance == null) {
            instance = new StudentRepository();
        }
        return instance;
    }

    public void getStudents(Context context, StudentCallback callback) {
        if (cachedStudents != null) {
            callback.onSuccess(cachedStudents);
            return;
        }

        StudentAPI studentAPI = new RetroFitService().getRetrofit().create(StudentAPI.class);
        studentAPI.GetStudents().enqueue(new Callback<List<StudentInstance>>() {
            @Override
            public void onResponse(Call<List<StudentInstance>> call, Response<List<StudentInstance>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cachedStudents = response.body();
                    callback.onSuccess(cachedStudents);
                } else {
                    callback.onFailure(new Exception("Failed to load students"));
                }
            }

            @Override
            public void onFailure(Call<List<StudentInstance>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public interface StudentCallback {
        void onSuccess(List<StudentInstance> students);
        void onFailure(Throwable t);
    }
}
