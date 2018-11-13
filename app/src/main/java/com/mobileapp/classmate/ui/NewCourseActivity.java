package com.mobileapp.classmate.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mobileapp.classmate.R;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

public class NewCourseActivity extends AppCompatActivity {
    private EditText mEditCourseNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);

        mEditCourseNameView = findViewById(R.id.edit_course);

        final ColorPicker cp = new ColorPicker(NewCourseActivity.this, 0,0,0);
        cp.enableAutoClose();
        final Button colorButton = findViewById(R.id.color_button);
        colorButton.setOnClickListener(v -> cp.show());

        final Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(mEditCourseNameView.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String courseName = mEditCourseNameView.getText().toString();
                String color = Integer.toString(cp.getRed());
                color += Integer.toString(cp.getBlue());
                color += Integer.toString(cp.getGreen());
                replyIntent.putExtra("courseName", courseName);
                replyIntent.putExtra("color", color);
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}
