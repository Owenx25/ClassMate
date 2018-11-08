package com.mobileapp.classmate;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

// Need to implement tab layout for tabs
public class ScreenSliderPagerActivity extends FragmentActivity {
    // handles animation and swiping
    private ViewPager viewPager;

    // provides pages to view pager widget
    private TabAdapter adapter;

    // sets up a tab layout for viewpagers
    private TabLayout tablayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tablayout = (TabLayout) findViewById(R.id.tabLayout);

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new ClassSelectionPageFragment(), "Class Selection");
        adapter.addFragment(new DailyPageFragment(), "Daily");

        viewPager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewPager);
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
