package com.example.attendence_tracker;

import android.app.Application;

public class STUDENTLOADER extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Removed preloadStudents(); to avoid loading students globally at app start
    }

    // Remove the entire preloadStudents() method
}