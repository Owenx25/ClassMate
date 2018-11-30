package com.mobileapp.classmate.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Assignment;
import com.mobileapp.classmate.viewmodel.MainViewModel;

import java.util.List;

public class AssignmentListAdapter extends RecyclerView.Adapter<AssignmentListAdapter.AssignmentViewHolder> {
    class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView assignmentItemView;
        private MainViewModel viewModel;


        private AssignmentViewHolder(View itemView) {
            super(itemView);
            assignmentItemView = itemView.findViewById(R.id.assignment_name_textView);

            itemView.setOnLongClickListener(v -> {
                viewModel = ViewModelProviders.of((AssignmentSelectionActivity)v.getContext())
                        .get(MainViewModel.class);
                Intent intent = ((Activity)v.getContext()).getIntent();
                String courseName = intent.getStringExtra("courseName");
                // show user delete class menu
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.dialog_title_delete_assignment);
                builder.setPositiveButton(R.string.button_delete, (dialog, which) -> {
                    // Delete from DB
                    // Should also delete all Course Assignments
                    viewModel.deleteAssignment(courseName, assignmentItemView.getText().toString());
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

    AssignmentListAdapter(int layoutId) {
        assigmentItemLayout = layoutId;
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

    public boolean isCourse(String courseName) {
        for (Assignment assignment: mAssignments) {
            if (assignment.className.equals(courseName))
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

