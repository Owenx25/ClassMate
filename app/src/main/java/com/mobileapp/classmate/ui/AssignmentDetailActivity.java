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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
        final String courseColor = (String)bundle.get("courseColor");
        final String assignmentName = (String)bundle.get("assignmentName");

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // Pull Assignment Information by using entry with
        // matching courseName AND assignmentName
        final Observer<Assignment> assignmentObserver = assignment -> {
            if (assignment != null) {
                // Data should get updated when an assignment is added
                mAssignment = assignment;
            }
        };
        viewModel.getCurrentAssignment()
                .observe(this, assignmentObserver);
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
        viewModel.getCurrentAssignment().setValue(mAssignment);

        mDesc = findViewById(R.id.etext_desc);
        mDesc.setFocusable(false);
        mGrade = findViewById(R.id.text_grade);
        mGrade.setFocusable(false);
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
                isEditing = true;
            }
            else {
                // Change FAB back to edit(pencil)
                mFab.setImageResource(R.drawable.ic_edit_white_24dp);
                // Disable Description
                mDesc.setFocusable(false);
                mGrade.setFocusable(false);
                /* !!! NEED TO SEND NEW DATA TO DB HERE !!! */
                viewModel.updateAssignment(mAssignment);
                isEditing = false;
            }
        });
        mFab.show();

        setupDescription();
        // Need Dialogs
        setupDueDate();
        setupPriority();
        setupReminder();
        setupCreateDate();
        setupGrade();
    }

    private void setupDescription() {
        // Set Text from DB
        mDesc.setText(mAssignment.description);
        if (mDesc.getText().toString().matches("")) {
            mDesc.setText(R.string.empty_description);
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.US);
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
        // Set Priority from Database
        Spinner spinner = (Spinner) findViewById(R.id.spinner_priority);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
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
        Button setBtn = findViewById(R.id.button_set_reminder);
        TextView reminderDate = findViewById(R.id.text_reminder_date);
        Button cancelBtn = findViewById(R.id.button_cancel_reminder);

        // Create Date and Time Pickers when user set reminder
        setBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mAssignment.reminder);
            DatePickerDialog reminderDatePicker = new DatePickerDialog(this, this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            editDate = EditDate.REMINDER_DATE;
            reminderDatePicker.show();
        });

        // Put current reminder datetime in TextView
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.US);
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
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
        mAssignment.priority = 5 - pos;
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy", Locale.US);
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        mAssignment.reminder = calendar.getTime();

        /**** NEED TO START ALARM SERVICE HERE ****/
    }

    private void CreateNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "reminder_channel";
            String description = "Channel for all assignment reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
