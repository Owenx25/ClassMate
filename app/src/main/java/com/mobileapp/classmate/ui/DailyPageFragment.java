package com.mobileapp.classmate.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;

import com.mobileapp.classmate.R;

public class DailyPageFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.daily_layout, container, false);
    }
}
