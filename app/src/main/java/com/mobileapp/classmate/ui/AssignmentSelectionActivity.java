package com.mobileapp.classmate.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Assignment;
import com.mobileapp.classmate.viewmodel.MainViewModel;

import java.util.Calendar;
import java.util.Date;

// This activity needs to:
//      - recieve intent data about selected class and color
//      - call a new adapter
//      - link to assignment specific pages

public class AssignmentSelectionActivity extends AppCompatActivity {
    // Floating action button for adding assignments
    private FloatingActionButton mFab;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_selection_layout);

        // Get intent information
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String courseName = (String)bundle.get("courseName");

        // Set toolbar title to course name
        //android.support.v7.widget.Toolbar mActionBarToolbar =
        //        (android.support.v7.widget.Toolbar) findViewById(R.id.assignment_selection_toolbar);
        //setSupportActionBar(mActionBarToolbar);
        //getSupportActionBar().setTitle(courseName);
        getSupportActionBar().setTitle(courseName);

        // Add Class Floating action button
        mFab = (FloatingActionButton) findViewById(R.id.fab_add_assignment);
        mFab.show();
        mFab.setOnClickListener(v -> showAddAssignmentDialog(courseName));
        // Init Recyclerview
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.assignment_selection_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Needs new adapter
        final AssignmentListAdapter adapter =
                new AssignmentListAdapter(R.layout.activity_assignment_selection_layout);

        recyclerView.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getAllAssignments().observe(this, assignments ->
                adapter.setAssignments(assignments));
    }

    private void showAddAssignmentDialog(String courseName) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View addAssignmentView = layoutInflater.inflate(R.layout.dialog_add_assignment, null);
        final AlertDialog alertD = new AlertDialog.Builder(this)
                .setTitle("Add Assignment")
                .create();
        EditText assignmentInput = (EditText) addAssignmentView.findViewById(R.id.assignment_name);
        Button saveBtn = (Button) addAssignmentView.findViewById(R.id.button_add_assignment_save);
        Button cancelBtn = (Button) addAssignmentView.findViewById(R.id.button_add_assignment_cancel);

        // Validate Course inputs and add to DB
        saveBtn.setOnClickListener(v -> {
            // Don't let user save if
            //  - Assignment name is empty
            //  - Assignment already exists IN CURRENT COURSE(NOT YET IMPLEMENTED)
            if (assignmentInput.getText().toString().matches("")) {
                Toast.makeText(this, R.string.invalid_course, Toast.LENGTH_SHORT).show();
            } else {
                Date createDate = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(createDate);
                c.add(Calendar.DATE, 1);
                Date dueDate = c.getTime();

                viewModel.insertAssignment(new Assignment(
                        assignmentInput.getText().toString(),
                        courseName,
                        3,
                        dueDate,
                        createDate,
                        false,
                        "",
                        ""));
                alertD.dismiss();
            }
        });

        // Quit on cancel press
        cancelBtn.setOnClickListener(v -> alertD.dismiss());
    }
}
