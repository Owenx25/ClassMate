package com.mobileapp.classmate.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.TextView;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.viewmodel.MainViewModel;

public class DailyPageFragment extends Fragment {

    private MainViewModel viewModel;

    private TextView courseName;
    private TextView courseColor;
    private final AssignmentListAdapter adapter =
            new AssignmentListAdapter(R.layout.daily_layout);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.daily_layout, container, false);



        RecyclerView recyclerView = (RecyclerView) rootView
                .findViewById(R.id.assignment_selection_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final AssignmentListAdapter adapter = new AssignmentListAdapter(R.layout.daily_layout);

        // Setup ViewModel and Adapter
        recyclerView.setAdapter(adapter);
        /* Currently will display all assignments, need
            to finish filtering by date*/

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTomorrowAssignments().observe(this, assignments -> adapter.setAssignments(assignments));
        return rootView;

    }

}

