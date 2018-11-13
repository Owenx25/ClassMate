package com.mobileapp.classmate.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Course;
import com.mobileapp.classmate.viewmodel.MainViewModel;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.List;

public class ClassSelectionPageFragment extends Fragment {
    private MainViewModel viewModel;
    private CourseListAdapter adapter;

    private TextView courseName;
    private TextView courseColor;

    @Override
    public void onActivityCreated(@Nullable Bundle saveIntstanceState) {
        super.onActivityCreated(saveIntstanceState);
        RecyclerView recyclerView = (RecyclerView)getActivity().findViewById(R.id.class_selection_recycler);
        final CourseListAdapter adapter = new CourseListAdapter(R.id.class_selection_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getAllCourses().observe(this, new Observer<List<Course>>() {
            @Override
            public void onChanged(@Nullable List<Course> courses) {
                adapter.setCourses(courses);
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(
                R.layout.class_selection_layout, container, false);
    }
}
