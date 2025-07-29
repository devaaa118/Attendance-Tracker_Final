package com.example.attendence_tracker.Model;


public class TimeTableEntry {
    private int timetableID;
    private int teacherID;
    private int courseID;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String courseName; // Add this field if you want to display course name

    public TimeTableEntry(int timetableID, int teacherID, int courseID, String dayOfWeek, String startTime, String endTime) {
        this.timetableID = timetableID;
        this.teacherID = teacherID;
        this.courseID = courseID;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        // courseName is not set here, set it via setter if needed
    }

    public int getTimetableID() { return timetableID; }
    public void setTimetableID(int timetableID) { this.timetableID = timetableID; }

    public int getTeacherID() { return teacherID; }
    public void setTeacherID(int teacherID) { this.teacherID = teacherID; }

    public int getCourseID() { return courseID; }
    public void setCourseID(int courseID) { this.courseID = courseID; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}