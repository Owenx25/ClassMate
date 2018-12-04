package com.mobileapp.classmate.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Course;
import com.mobileapp.classmate.viewmodel.MainViewModel;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.Date;

public class ClassSelectionPageFragment extends Fragment{
    final String CHANNEL_ID = "REMINDERS";
    private MainViewModel viewModel;
    private int courseColor;

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

    public void showAddCourseDialog(Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View AddCourseView = layoutInflater.inflate(R.layout.dialog_add_course, null);
        final AlertDialog alertD = new AlertDialog.Builder(activity)
                .setTitle(R.string.add_course_dialog)
                .create();
        EditText courseInput = (EditText) AddCourseView.findViewById(R.id.course_name);
        Button colorBtn = (Button) AddCourseView.findViewById(R.id.button_color);
        Spinner spinner = (Spinner) AddCourseView.findViewById(R.id.spinner_icon);
        SpinnerAdapter adapter = new SpinnerAdapter(activity,
                new Integer[]{R.drawable.icon_pencil, R.drawable.icon_art, R.drawable.icon_english, R.drawable.icon_history,
                        R.drawable.icon_lang, R.drawable.icon_math, R.drawable.icon_music,
                        R.drawable.icon_read, R.drawable.icon_science});
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getSelectedPosition());
        Button saveBtn = (Button) AddCourseView.findViewById(R.id.button_addcourse_save);
        Button cancelBtn = (Button) AddCourseView.findViewById(R.id.button_addcourse_cancel);
        // OnClick Callbacks
        final ColorPicker cp = new ColorPicker(activity, 0, 0, 0);
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
                color = ((ColorDrawable) background).getColor();

            if (courseInput.getText().toString().matches("")) {
                Toast.makeText(activity, R.string.invalid_course, Toast.LENGTH_SHORT).show();
            } else {
                viewModel.insertCourse(new Course(
                        courseInput.getText().toString(),
                        new Date(),
                        color, spinner.getSelectedItemPosition()));
                alertD.dismiss();
            }
        });

        // Quit on cancel press
        cancelBtn.setOnClickListener(v -> alertD.dismiss());
    }
}
