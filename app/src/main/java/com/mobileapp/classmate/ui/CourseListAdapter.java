package com.mobileapp.classmate.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Course;

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseViewHolder> {
    class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseItemView;

        private CourseViewHolder(View itemView) {
            super(itemView);
            courseItemView = itemView.findViewById(R.id.textView);
        }
    }

    private int courseItemLayout;
    private List<Course> mCourses;

    CourseListAdapter(int layoutId) {
        courseItemLayout = layoutId;
    }

    @Override
    @NonNull
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(courseItemLayout, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        if (mCourses != null) {
            Course current = mCourses.get(position);
            holder.courseItemView.setText(current.courseName);
        } else {
            // If there's no data
            holder.courseItemView.setText("No Courses!");
        }
    }

    void setCourses(List<Course> courses) {
        mCourses = courses;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mCourses != null)
            return mCourses.size();
        else return 0;
    }
}
