package com.mobileapp.classmate.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Course;
import com.mobileapp.classmate.viewmodel.MainViewModel;

import java.util.List;

public class ClassSelectionPageFragment extends Fragment {
    private MainViewModel viewModel;

    private TextView courseName;
    private TextView courseColor;

    @Override
    public void onActivityCreated(@Nullable Bundle saveIntstanceState) {
        super.onActivityCreated(saveIntstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.class_selection_layout, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView
                .findViewById(R.id.class_selection_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final CourseListAdapter adapter = new CourseListAdapter(R.layout.class_selection_layout);

        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getAllCourses().observe(this, courses -> adapter.setCourses(courses));

        return rootView;
    }
}
