package com.mobileapp.classmate.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.TextView;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Assignment;
import com.mobileapp.classmate.viewmodel.MainViewModel;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DailyPageFragment extends Fragment {

    private MainViewModel viewModel;

    private TextView courseName;
    private TextView courseColor;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.daily_layout, container, false);



        RecyclerView recyclerView = (RecyclerView) rootView
                .findViewById(R.id.assignment_selection_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final AssignmentListAdapter adapter = new AssignmentListAdapter(R.layout.daily_layout, getActivity());

        // Setup ViewModel and Adapter
        recyclerView.setAdapter(adapter);
        /* Currently will display all assignments, need
            to finish filtering by date*/

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getAllAssignments().observe(this, assignments -> {
            assignments.removeIf(assignment -> {
                Date today = AssignmentDetailActivity.resetTime(new Date());
                long diffInMillies = assignment.dueDate.getTime() - today.getTime();
                long diff = TimeUnit.DAYS.convert(Math.abs(diffInMillies), TimeUnit.MILLISECONDS);
                return diff != 1 && assignment.priority != 0;
            });
            adapter.setAssignments(assignments);
        });
        return rootView;

    }
}

