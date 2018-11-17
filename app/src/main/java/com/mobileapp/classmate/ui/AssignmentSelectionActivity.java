package com.mobileapp.classmate.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.mobileapp.classmate.R;

// In this activity only need toolbar, menu, assignment list, and FAB

public class AssignmentSelectionActivity extends AppCompatActivity {
    // Floating action button for adding assignments
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_selection_layout);

        // Add Class Floating action button
        mFab = (FloatingActionButton) findViewById(R.id.fab_add_assignment);
        mFab.show();
        mFab.setOnClickListener(v -> showAddAssignmentDialog());
    }

    private void showAddAssignmentDialog() {

    }
}
