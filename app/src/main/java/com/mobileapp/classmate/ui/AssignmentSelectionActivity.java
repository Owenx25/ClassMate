package com.mobileapp.classmate.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Assignment;
import com.mobileapp.classmate.db.entity.Course;
import com.mobileapp.classmate.viewmodel.MainViewModel;

import java.util.Calendar;
import java.util.Date;

// This activity needs to:
//      - receive intent data about selected class and color
//      - call a new adapter
//      - link to assignment specific pages

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

        // Validate Course inputs and add to DB
        saveBtn.setOnClickListener(v -> {
            // Don't let user save if
            //  - Assignment name is empty
            //  - Assignment already exists IN CURRENT COURSE
            if (assignmentInput.getText().toString().matches("") ||
                    adapter.isCourse(assignmentInput.getText().toString())) {
                Toast.makeText(this, R.string.invalid_course, Toast.LENGTH_SHORT).show();
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
