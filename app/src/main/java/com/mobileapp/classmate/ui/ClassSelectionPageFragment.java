package com.mobileapp.classmate.ui;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Course;
import com.mobileapp.classmate.viewmodel.MainViewModel;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.Date;
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

    public void showAddCourseDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View AddCourseView = layoutInflater.inflate(R.layout.dialog_addcourse, null);
        final AlertDialog alertD = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_course_dialog)
                .create();
        EditText courseInput = (EditText) AddCourseView.findViewById(R.id.course_name);
        Button colorBtn = (Button) AddCourseView.findViewById(R.id.button_color);
        Button saveBtn = (Button) AddCourseView.findViewById(R.id.button_addcourse_save);
        Button cancelBtn = (Button) AddCourseView.findViewById(R.id.button_addcourse_cancel);
        // OnClick Callbacks
        final ColorPicker cp = new ColorPicker(getActivity(), 0,0,0);
        cp.enableAutoClose();

        alertD.setView(AddCourseView);
        alertD.show();

        // Show Course add dialog after color is picked
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                alertD.show();
                View colorSquare = alertD.findViewById(R.id.color_square);
                colorSquare.setBackgroundColor(color);
            }
        });

        // Show Color picker on color button press
        colorBtn.setOnClickListener(v -> {
            alertD.hide();
            cp.show();
        });

        // Validate Course inputs and add to DB
        saveBtn.setOnClickListener(v -> {
            // Don't let user save if
            //  - Course is empty
            //  - Course already exists(NOT YET IMPLEMENTED)
            int color = Color.TRANSPARENT;
            View colorSquare = alertD.findViewById(R.id.color_square);
            Drawable background = colorSquare.getBackground();
            if (background instanceof ColorDrawable)
                color = ((ColorDrawable)background).getColor();

            if (courseInput.getText().toString().matches("")) {
                Toast.makeText(getActivity(), R.string.invalid_course, Toast.LENGTH_SHORT).show();
            } else {
                viewModel.insertCourse(new Course(
                        courseInput.getText().toString(),
                        new Date(),
                        color));
                alertD.dismiss();
            }
        });

        // Quit on cancel press
        cancelBtn.setOnClickListener(v -> alertD.dismiss());
    }
}
