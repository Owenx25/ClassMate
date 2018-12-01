package com.mobileapp.classmate.ui;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Assignment;
import com.mobileapp.classmate.viewmodel.MainViewModel;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AssignmentDetailActivity extends AppCompatActivity
    implements  DatePickerDialog.OnDateSetListener,
                AdapterView.OnItemSelectedListener,
                TimePickerDialog.OnTimeSetListener
    {

    enum EditDate {DUE_DATE, REMINDER_DATE}
    final String CHANNEL_ID = "REMINDERS";

    private FloatingActionButton mFab;
    boolean isEditing = false;
    private EditText mDesc;
    private EditText mGrade;
    private Button setReminderButton;
    private Spinner mSpinner;

    private MainViewModel viewModel;
    private Assignment mAssignment;

    private Date newDate;
    EditDate editDate;



    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setContentView(R.layout.activity_detail_layout);

        // Get intent information
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String courseName = (String)bundle.get("courseName");
        final int courseColor = (int)bundle.get("courseColor");
        final String assignmentName = (String)bundle.get("assignmentName");
        final Boolean isNewAssignment = (Boolean)bundle.get("adding");

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // Pull Assignment Information by using entry with
        // matching courseName AND assignmentName
        final Observer<Assignment> assignmentObserver = assignment -> {
            if (assignment != null) {
                // Data should get updated when an assignment is added
                mAssignment = assignment;
                setupDescription();
                // Need Dialogs
                setupDueDate();
                setupPriority();
                setupReminder();
                setupCreateDate();
                setupGrade();

                setupTitles(courseColor);

                // set action bar
                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle(mAssignment.name);
                actionBar.setBackgroundDrawable(new ColorDrawable(courseColor));
            }
        };
        viewModel.getAssignment(courseName, assignmentName)
                .observe(this, assignmentObserver);

        // Should only create when coming from add dialog
        if (isNewAssignment) {
            Date createDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(createDate);
            c.add(Calendar.DATE, 1);
            Date dueDate = c.getTime();
            mAssignment = new Assignment(
                    assignmentName,
                    courseName,
                    3,
                    dueDate,
                    createDate,
                    false,
                    new Date(),
                    "",
                    0);
            viewModel.insertAssignment(mAssignment);
        } else {
            viewModel.getAssignment(courseName, assignmentName);
        }

        mDesc = findViewById(R.id.etext_desc);
        mDesc.setFocusable(false);
        mGrade = findViewById(R.id.text_grade);
        mGrade.setFocusable(false);
        mSpinner = (Spinner) findViewById(R.id.spinner_priority);
        mSpinner.setEnabled(false);
        setReminderButton = findViewById(R.id.button_set_reminder);
        setReminderButton.setEnabled(false);
        // Add Class Floating action button
        mFab = (FloatingActionButton) findViewById(R.id.fab_edit_assignment);
        mFab.setOnClickListener(v -> {
            /*Open up all assignment fields for editing*/
            // Toggle FAB icon
            if (!isEditing) {
                // Change FAB to check
                mFab.setImageResource(R.drawable.ic_check_white_24dp);
                // Enable Desc EditText
                mDesc.setFocusableInTouchMode(true);
                mGrade.setFocusableInTouchMode(true);
                setReminderButton.setEnabled(true);
                mSpinner.setEnabled(true);
                isEditing = true;
            }
            else {
                // Change FAB back to edit(pencil)
                mFab.setImageResource(R.drawable.ic_edit_white_24dp);
                // Disable Description
                mDesc.clearFocus();
                mDesc.setFocusable(false);
                mGrade.clearFocus();
                mGrade.setFocusable(false);
                setReminderButton.setEnabled(false);
                mSpinner.setEnabled(false);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mFab.getWindowToken(), 0);
                /* !!! NEED TO SEND NEW DATA TO DB HERE !!! */
                viewModel.updateAssignment(mAssignment);
                isEditing = false;
            }
        });
        mFab.show();


    }

    private void setupTitles(int courseColor) {
        // set title colors
        TextView title = findViewById(R.id.title_create_date);
        title.setTextColor(courseColor);
        View underline = findViewById(R.id.underline_create_date);
        underline.setBackgroundColor(courseColor);

        title = findViewById(R.id.title_description);
        title.setTextColor(courseColor);
        underline = findViewById(R.id.underline_description);
        underline.setBackgroundColor(courseColor);

        title = findViewById(R.id.title_due_date);
        title.setTextColor(courseColor);
        underline = findViewById(R.id.underline_due_date);
        underline.setBackgroundColor(courseColor);

        title = findViewById(R.id.title_grade);
        title.setTextColor(courseColor);
        underline = findViewById(R.id.underline_grade);
        underline.setBackgroundColor(courseColor);

        title = findViewById(R.id.title_priority);
        title.setTextColor(courseColor);
        underline = findViewById(R.id.underline_priority);
        underline.setBackgroundColor(courseColor);

        title = findViewById(R.id.title_reminder);
        title.setTextColor(courseColor);
        underline = findViewById(R.id.underline_reminder);
        underline.setBackgroundColor(courseColor);
    }

    private void setupDescription() {
        // Set Text from DB
        mDesc.setText(mAssignment.description);
        if (mDesc.getText().toString().matches("")) {
            mDesc.setHint(R.string.empty_description);
        }
        mDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().matches(mAssignment.description)) {
                    mAssignment.description = s.toString();
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        // Editing is enabled in the FAB listener
    }

    private void setupDueDate() {
        // Set Due Date from database
        final TextView DueDateText = findViewById(R.id.text_due_date);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String formattedDate = formatter.format(mAssignment.dueDate);
        DueDateText.setText(formattedDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mAssignment.dueDate);

        DatePickerDialog dueDatePicker = new DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Bring up date picker if editing
        DueDateText.setOnClickListener(view -> {
            if (isEditing) {
                editDate = EditDate.DUE_DATE;
                dueDatePicker.show();
            }
        });
    }

    private void setupPriority() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setSelection(mAssignment.priority);
        // Apply the adapter to the spinner
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);
    }

    public void setupReminder() {
        // NEED to come back to this later
        // Notification needs to live in a service
//        CreateNotificationChannel();
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
//                CHANNEL_ID)
//                .setContent("ASSIGNMENT REMINDER")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        // Reminders need a Date AND Time selector

        TextView reminderDate = findViewById(R.id.text_reminder_date);
        Button cancelBtn = findViewById(R.id.button_cancel_reminder);

        // Create Date and Time Pickers when user set reminder
        setReminderButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mAssignment.reminder);
            DatePickerDialog reminderDatePicker = new DatePickerDialog(this, this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            editDate = EditDate.REMINDER_DATE;
            reminderDatePicker.show();

        });

        // Put current reminder datetime in TextView
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.US);
        reminderDate.setText(formatter.format(mAssignment.reminder));

        // Cancel btn will stop upcoming alarm and reset reminder date
        cancelBtn.setOnClickListener(v -> {
            // Insert magical code that stops service

            // Reset reminder
            mAssignment.reminder = new Date();
            reminderDate.setText("");
        });
    }

    public void setupCreateDate() {
        // Create date should never change
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        TextView createDate = findViewById(R.id.text_create_date);
        createDate.setText(formatter.format(mAssignment.createDate));
    }

    public void setupGrade() {
        // Set Text from DB
        mGrade.setText(String.valueOf(mAssignment.grade));
        mGrade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Integer.parseInt(s.toString()) != mAssignment.grade) {
                    mAssignment.grade = Integer.parseInt(s.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        // Editing is enabled in the FAB listener
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // There are only 5 priority options
        mAssignment.priority = pos;
        parent.setSelection(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        newDate = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        switch (editDate) {
            case DUE_DATE:
                final TextView DueDateText = findViewById(R.id.text_due_date);
                String formattedDate = formatter.format(newDate);
                DueDateText.setText(formattedDate);
                mAssignment.dueDate = newDate;
                break;
            case REMINDER_DATE:
                // Save date pick then move on to time
                Calendar timeCal = Calendar.getInstance();
                timeCal.setTime(mAssignment.reminder);
                TimePickerDialog reminderTimePicker = new TimePickerDialog(this, this,
                    timeCal.get(Calendar.HOUR_OF_DAY),
                    timeCal.get(Calendar.MINUTE),
                    false);
                reminderTimePicker.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Set new reminder time
        // if Time pick cancels reminder will stay unchanged
        TextView reminderDate = findViewById(R.id.text_reminder_date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        mAssignment.reminder = calendar.getTime();

        // Put current reminder datetime in TextView
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.US);
        reminderDate.setText(formatter.format(mAssignment.reminder));

        /**** NEED TO START ALARM SERVICE HERE ****/
    }

//    private void CreateNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "reminder_channel";
//            String description = "Channel for all assignment reminders";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
}
