package com.mobileapp.classmate.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Course;
import com.mobileapp.classmate.viewmodel.MainViewModel;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.Date;

public class AssignmentSelectionActivity extends AppCompatActivity {
    // Floating action button for adding assignments
    private FloatingActionButton mFab;
    private MainViewModel viewModel;
    private Course mCourse;
    private final AssignmentListAdapter adapter =
            new AssignmentListAdapter(R.layout.activity_assignment_selection_layout, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_selection_layout);

        // Get intent information
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String courseName = (String)bundle.get("courseName");
        final int color = (int)bundle.get("courseColor");

        // Add Assignment Floating action button
        mFab = (FloatingActionButton) findViewById(R.id.fab_add_assignment);
        mFab.setOnClickListener(v -> showAddAssignmentDialog(courseName));
        mFab.show();
        // Init Recyclerview
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.assignment_selection_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup ViewModel and Adapter
        recyclerView.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getCourseAssignments(courseName).observe(this, assignments ->
                adapter.setAssignments(assignments));

        // Set title and colors to match class
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(courseName);
        final Observer<Course> courseObserver = course -> {
            if (course != null) {
                mCourse = course;
                actionBar.setBackgroundDrawable(new ColorDrawable(course.color));
                mFab.setBackgroundTintList(ColorStateList.valueOf(course.color));
            }
        };
        viewModel.getCourse(courseName).observe(this, courseObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.assignment_selection_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename_course:
                // Fire a dialog for new name
                showRenameCourseDialog();
                return true;
            case R.id.recolor_course:
                // fire a dialog for new course color
                showRecolorDialog();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showRenameCourseDialog() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View renameCourseView = layoutInflater.inflate(R.layout.dialog_rename_course, null);
        final AlertDialog alertD = new AlertDialog.Builder(this)
                .setTitle("Rename Course")
                .create();
        Button saveBtn = renameCourseView.findViewById(R.id.button_rename_course_save);
        Button cancelBtn = renameCourseView.findViewById(R.id.button_rename_course_cancel);
        EditText courseInput = renameCourseView.findViewById(R.id.course_rename);

        alertD.setView(renameCourseView);
        alertD.show();

        // Validate Course inputs and add to DB
        saveBtn.setOnClickListener(v -> {
            if (courseInput.getText().toString().matches("")) {
                Toast.makeText(this, R.string.invalid_course, Toast.LENGTH_SHORT).show();
            } else {
                String oldName = mCourse.courseName;
                mCourse.courseName = courseInput.getText().toString();
                viewModel.updateAssignCourseName(oldName, mCourse.courseName);
                viewModel.updateCourse(mCourse);
                // Also need to update all assignments in course
                getSupportActionBar().setTitle(mCourse.courseName);
                alertD.dismiss();

            }
        });

        // Quit on cancel press
        cancelBtn.setOnClickListener(v -> alertD.dismiss());
    }

    private void showRecolorDialog() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View recolorCourseView = layoutInflater.inflate(R.layout.dialog_recolor_course, null);
        final AlertDialog alertD = new AlertDialog.Builder(this)
                .setTitle("Recolor Course")
                .create();
        Button saveBtn = recolorCourseView.findViewById(R.id.button_recolor_save);
        Button cancelBtn = recolorCourseView.findViewById(R.id.button_recolor_cancel);
        Button colorBtn = recolorCourseView.findViewById(R.id.button_recolor);
        View colorBox = recolorCourseView.findViewById(R.id.recolor_square);
        colorBox.setBackgroundColor(mCourse.color);

        final ColorPicker cp = new ColorPicker(this, 0, 0, 0);
        cp.enableAutoClose();

        alertD.setView(recolorCourseView);
        alertD.show();

        // Show Course add dialog after color is picked
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                alertD.show();
                View colorSquare = alertD.findViewById(R.id.recolor_square);
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
            Drawable background = colorBox.getBackground();
            int color = 0;
            if (background instanceof ColorDrawable)
                color = ((ColorDrawable) background).getColor();
            mCourse.color = color;
            viewModel.updateCourse(mCourse);
            alertD.dismiss();
        });

        // Quit on cancel press
        cancelBtn.setOnClickListener(v -> alertD.dismiss());
    }

    private void showAddAssignmentDialog(String courseName) {
    LayoutInflater layoutInflater = getLayoutInflater();
    View addAssignmentView = layoutInflater.inflate(R.layout.dialog_add_assignment, null);
    final AlertDialog alertD = new AlertDialog.Builder(this)
            .setTitle("Add Assignment")
            .create();
    EditText assignmentInput = (EditText) addAssignmentView.findViewById(R.id.assignment_name);
    assignmentInput.setBackgroundTintList(ColorStateList.valueOf(mCourse.color));
    assignmentInput.setTextColor(mCourse.color);
    Button saveBtn = (Button) addAssignmentView.findViewById(R.id.button_add_assignment_save);
    Button cancelBtn = (Button) addAssignmentView.findViewById(R.id.button_add_assignment_cancel);

    alertD.setView(addAssignmentView);
    alertD.show();

    // Validate Assignment inputs and add to DB
    saveBtn.setOnClickListener(v -> {
        // Don't let user save if
        //  - Assignment name is empty
        //  - Assignment already exists IN CURRENT COURSE
        if (assignmentInput.getText().toString().matches("") ||
                adapter.isAssignment(assignmentInput.getText().toString())) {
            Toast.makeText(this, R.string.invalid_assignment, Toast.LENGTH_SHORT).show();
        } else {

            alertD.dismiss();

            // Show assignment activity after creating assignment
            Context context = this;
            Intent intent = new Intent(this, AssignmentDetailActivity.class);
            intent.putExtra("courseName", courseName);
            intent.putExtra("courseColor", mCourse.color);
            intent.putExtra("assignmentName", assignmentInput.getText().toString());
            intent.putExtra("adding", true);
            context.startActivity(intent);
        }
    });

        // Quit on cancel press
        cancelBtn.setOnClickListener(v -> alertD.dismiss());
    }
}
