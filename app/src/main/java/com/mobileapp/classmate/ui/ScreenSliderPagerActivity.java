package com.mobileapp.classmate.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.mobileapp.classmate.R;

// Need to implement tab layout for tabs
public class ScreenSliderPagerActivity extends FragmentActivity {
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

        });
        mFab.setOnClickListener(v ->
                startActivity(new Intent(ScreenSliderPagerActivity.this,
                        NewCourseActivity.class)));
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
