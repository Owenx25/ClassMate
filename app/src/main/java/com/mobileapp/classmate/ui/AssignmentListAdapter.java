package com.mobileapp.classmate.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Assignment;

import java.util.List;

public class AssignmentListAdapter extends RecyclerView.Adapter<AssignmentListAdapter.AssignmentViewHolder> {
    class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView assignmentItemView;


        private AssignmentViewHolder(View itemView) {
            super(itemView);
            assignmentItemView = itemView.findViewById(R.id.assignment_name_textView);
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

