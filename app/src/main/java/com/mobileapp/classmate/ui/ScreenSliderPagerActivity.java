package com.mobileapp.classmate.ui;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
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

// Need to implement tab layout for tabs
public class ScreenSliderPagerActivity extends FragmentActivity {
    // handles animation and swiping
    private ViewPager viewPager;

    // Floating action button for adding classes on class selection fragment
    private FloatingActionButton mFab;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);


        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        // sets up a tab layout for viewpagers
        TabLayout tablayout = (TabLayout) findViewById(R.id.tabLayout);

        // Set up viewPager tabs
        // provides pages to view pager widget
        TabAdapter mTabAdapter = new TabAdapter(getSupportFragmentManager());
        ClassSelectionPageFragment classSelectionFragment = new ClassSelectionPageFragment();
        DailyPageFragment dailyPageFragment = new DailyPageFragment();
        mTabAdapter.addFragment(classSelectionFragment, "Class Selection");
        mTabAdapter.addFragment(dailyPageFragment, "Daily");

        viewPager.setAdapter(mTabAdapter);
        tablayout.setupWithViewPager(viewPager);



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
        mFab.setOnClickListener(v -> showAddCourseDialog());
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
            //  - Course already exists***
            int color = Color.TRANSPARENT;
            View colorSquare = alertD.findViewById(R.id.color_square);
            Drawable background = colorSquare.getBackground();
            if (background instanceof ColorDrawable)
                color = ((ColorDrawable)background).getColor();

            if (courseInput.getText().toString().matches("")) {
                Toast.makeText(this, R.string.invalid_course, Toast.LENGTH_SHORT).show();
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
