package com.example.attendence_tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendence_tracker.Model.CourseInstance;

import java.util.List;
import java.util.Set;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<CourseInstance> courseList;
    private Set<Integer> selectedCourseIds;

    public CourseAdapter(List<CourseInstance> courseList, Set<Integer> selectedCourseIds) {
        this.courseList = courseList;
        this.selectedCourseIds = selectedCourseIds;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_checkbox, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseInstance course = courseList.get(position);
        String displayText = course.getCourseName() + " (Sem: " + course.getCourseSemester() + ")";
        holder.tvCourseName.setText(displayText);
        holder.checkBox.setChecked(selectedCourseIds.contains(course.getCourseID()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedCourseIds.add(course.getCourseID());
            } else {
                selectedCourseIds.remove(course.getCourseID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName;
        CheckBox checkBox;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            checkBox = itemView.findViewById(R.id.checkboxCourse);
        }
    }
}
