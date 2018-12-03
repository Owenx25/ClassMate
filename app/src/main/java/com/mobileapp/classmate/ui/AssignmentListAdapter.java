package com.mobileapp.classmate.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Assignment;
import com.mobileapp.classmate.db.entity.Course;
import com.mobileapp.classmate.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AssignmentListAdapter extends RecyclerView.Adapter<AssignmentListAdapter.AssignmentViewHolder> {
    class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView assignmentItemView;
        TextView dateItemView;
        TextView priorityItemView;
        private MainViewModel viewModel;
        private Course mCourse;

        public void setupObserver(String courseName, AppCompatActivity activity) {
            // needs to cast to either ViewPagerMainActivity or AssignmentSelectionActivity
            viewModel = ViewModelProviders.of(activity)
                    .get(MainViewModel.class);
            final Observer<Course> courseObserver = course -> mCourse = course;
            viewModel.getCourse(courseName).observe(activity, courseObserver);
        }

        public void setupObserver(String courseName, FragmentActivity activity) {
            // needs to cast to either ViewPagerMainActivity or AssignmentSelectionActivity
            viewModel = ViewModelProviders.of(activity)
                    .get(MainViewModel.class);
            final Observer<Course> courseObserver = course -> mCourse = course;
            viewModel.getCourse(courseName).observe(activity, courseObserver);
        }

        private AssignmentViewHolder(View itemView) {
            super(itemView);
            assignmentItemView = itemView.findViewById(R.id.assignment_name_textView);
            dateItemView = itemView.findViewById(R.id.assignment_listItem_Due_Date);
            priorityItemView = itemView.findViewById(R.id.assignment_listItem_priority);

            // Open up assignment detail activity
            itemView.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, AssignmentDetailActivity.class);
                intent.putExtra("courseName", mCourse.courseName);
                // Color is not part of Assignment List activity...
                intent.putExtra("courseColor", mCourse.color);
                intent.putExtra("assignmentName", assignmentItemView.getText().toString());
                intent.putExtra("adding", false);
                context.startActivity(intent);
            });

            // Allow user to delete assignment on long click
            itemView.setOnLongClickListener(v -> {
                viewModel = ViewModelProviders.of((AssignmentSelectionActivity)v.getContext())
                        .get(MainViewModel.class);
                Intent intent = ((Activity)v.getContext()).getIntent();
                // show user delete class menu
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.dialog_title_delete_assignment);
                builder.setPositiveButton(R.string.button_delete, (dialog, which) -> {
                    // Delete from DB
                    // Should also delete all Course Assignments
                    viewModel.deleteAssignment(mCourse.courseName, assignmentItemView.getText().toString());
                });
                builder.setNegativeButton(R.string.button_cancel, (dialog, which) -> {});
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            });
        }
    }

    private int assigmentItemLayout;
    private List<Assignment> mAssignments;
    private AppCompatActivity mActivity;
    private FragmentActivity mFragment;

    AssignmentListAdapter(int layoutId, AppCompatActivity activity) {
        assigmentItemLayout = layoutId;
        mActivity = activity;
    }

    AssignmentListAdapter(int layoutId, FragmentActivity fragment) {
        assigmentItemLayout = layoutId;
        mFragment = fragment;
    }

    @Override
    @NonNull
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assignment_selection_item, parent, false);
        return new AssignmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        if (mAssignments != null) {
            Assignment current = mAssignments.get(position);
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd", Locale.US);
            String formattedDate = formatter.format(current.dueDate);

            int color = 0;
            if (mFragment == null) {
                color = mActivity.getResources().getColor(android.R.color.holo_red_dark);
                holder.setupObserver(current.className, mActivity);
                holder.priorityItemView.setText(
                        mActivity.getResources().getStringArray(R.array.priority_array)[current.priority]);
            } else {
                color = mFragment.getResources().getColor(android.R.color.holo_red_dark);
                holder.setupObserver(current.className, mFragment);
                holder.priorityItemView.setText(
                        mFragment.getResources().getStringArray(R.array.priority_array)[current.priority]);
            }
            // Top priority assignments should be red
            if (current.priority == 0) {
                holder.priorityItemView.setTextColor(color);
            }
            // Assignments due tommmorow should be red
            Date today = AssignmentDetailActivity.resetTime(new Date());
            long diffInMillies = current.dueDate.getTime() - today.getTime();
            long diff = TimeUnit.DAYS.convert(Math.abs(diffInMillies), TimeUnit.MILLISECONDS);
            if (diff == 1 && diffInMillies > 0) {
                holder.dateItemView.setTextColor(color);
            }
            holder.dateItemView.setText(formattedDate);
            holder.assignmentItemView.setText(current.name);

        } else {
            // If there's no data
            holder.assignmentItemView.setText(R.string.empty_assignment_list);
        }
    }

    void setAssignments(List<Assignment> assignments) {
        mAssignments = assignments;
        notifyDataSetChanged();
    }

    public boolean isAssignment(String name) {
        for (Assignment assignment: mAssignments) {
            if (assignment.name.equals(name))
                return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        if (mAssignments != null)
            return mAssignments.size();
        else return 0;
    }
}

