package com.mobileapp.classmate.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mobileapp.classmate.R;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

// Need to implement tab layout for tabs
public class ScreenSliderPagerActivity extends FragmentActivity
        implements AddCourseDialogFragment.AddCourseDialogListener {
    // handles animation and swiping
    private ViewPager viewPager;

    // provides pages to view pager widget
    private TabAdapter adapter;

    // sets up a tab layout for viewpagers
    private TabLayout tablayout;

    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tablayout = (TabLayout) findViewById(R.id.tabLayout);

        adapter = new TabAdapter(getSupportFragmentManager());
        ClassSelectionPageFragment classSelectionFragment = new ClassSelectionPageFragment();
        DailyPageFragment dailyPageFragment = new DailyPageFragment();
        adapter.addFragment(classSelectionFragment, "Class Selection");
        adapter.addFragment(dailyPageFragment, "Daily");

        viewPager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewPager);

//        final ColorPicker cp = new ColorPicker(this, 0,0,0);
//        cp.enableAutoClose();


        mFab = (FloatingActionButton) findViewById(R.id.fab);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mFab.show();
                        break;
                    default:
                        mFab.hide();
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });
        mFab.setOnClickListener(v -> {
            showAddCourseDialog();
        });
    }

    public void showAddCourseDialog() {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View AddCourseView = layoutInflater.inflate(R.layout.dialog_addcourse, null);
        final AlertDialog alertD = new AlertDialog.Builder(this)
                .setTitle(R.string.add_course_dialog)
                .create();
        EditText courseInput = (EditText) AddCourseView.findViewById(R.id.course_name);
        Button colorBtn = (Button) AddCourseView.findViewById(R.id.button_color);
        Button saveBtn = (Button) AddCourseView.findViewById(R.id.button_addcourse_save);
        Button cancelBtn = (Button) AddCourseView.findViewById(R.id.button_addcourse_cancel);
        // OnClick Callbacks
        final ColorPicker cp = new ColorPicker(this, 0,0,0);
        cp.enableAutoClose();
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                alertD.show();
                View colorSquare = alertD.findViewById(R.id.color_square);
                colorSquare.setBackgroundColor(color);
            }
        });
        colorBtn.setOnClickListener(v -> {
            alertD.hide();
            cp.show();
        });
        saveBtn.setOnClickListener(v -> {

        });
        cancelBtn.setOnClickListener(v -> {
            alertD.hide();
        });
        alertD.setView(AddCourseView);
        alertD.show();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }
    @Override
    public void OnDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
}
