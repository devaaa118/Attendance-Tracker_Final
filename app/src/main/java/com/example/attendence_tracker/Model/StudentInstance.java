package com.example.attendence_tracker.Model;

public class StudentInstance {
    private String studentID;
    private String studentName;

    public StudentInstance(String studentID, String studentName) {
        this.studentID = studentID;
        this.studentName = studentName;
    }

    public String getName() {
        return studentName;
    }

    public String getStudentId() {
        return studentID;
    }
}


