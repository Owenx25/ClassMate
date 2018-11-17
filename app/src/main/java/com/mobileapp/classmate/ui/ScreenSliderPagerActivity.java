package com.mobileapp.classmate.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.viewmodel.MainViewModel;

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

        // Add Class Floating action button
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
        mFab.setOnClickListener(v -> classSelectionFragment.showAddCourseDialog());
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
