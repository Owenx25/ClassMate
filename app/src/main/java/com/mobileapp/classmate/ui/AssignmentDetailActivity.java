package com.mobileapp.classmate.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Assignment;
import com.mobileapp.classmate.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
    private TextView mGrade;
    private Button setReminderButton;
    private Button completeBtn;
    private Spinner mSpinner;
    private Drawable spinnerDrawable;
    private ImageButton cancelBtn;

    private MainViewModel viewModel;
    private Assignment mAssignment;

    String courseName;
    int courseColor ;
    String assignmentName;
    Boolean isNewAssignment;

    private Date newDate;
    EditDate editDate;

    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setContentView(R.layout.activity_detail_layout);

        createNotificationChannel();

        // Get intent information
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        courseName = (String)bundle.get("courseName");
        courseColor = (int)bundle.get("courseColor");
        assignmentName = (String)bundle.get("assignmentName");
        isNewAssignment = (Boolean)bundle.get("adding");

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
                setupCompleteButton();

                setupTitles(courseColor);

                TextView days_left = findViewById(R.id.days_left);
                Date today = resetTime(new Date());
                long diffInMillies = mAssignment.dueDate.getTime() - today.getTime();
                long diff = TimeUnit.DAYS.convert(Math.abs(diffInMillies), TimeUnit.MILLISECONDS);
                if (diff == 1 && diffInMillies > 0) {
                    days_left.setText(R.string.days_left_one);
                } else if (diff == 0) {
                    days_left.setText(R.string.days_left_zero);
                } else if (diffInMillies < 0) {
                    days_left.setText(getString(R.string.days_left_lt_zero, diff));
                } else {// diff > 1
                    days_left.setText(getString(R.string.days_left_gt_one, diff));
                }

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
            Calendar c = Calendar.getInstance();
            c.setTime(resetTime(new Date()));
            Date createDate = c.getTime();
            c.add(Calendar.DATE, 1);
            Date dueDate = c.getTime();

            mAssignment = new Assignment(
                    assignmentName,
                    courseName,
                    3,
                    dueDate,
                    createDate,
                    null,
                    false,
                    null,
                    "",
                    -1, 0);
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
        cancelBtn = findViewById(R.id.button_cancel_reminder);
        completeBtn = findViewById(R.id.button_mark_complete);
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
                cancelBtn.setVisibility(View.VISIBLE);
                // No marking complete while editing
                completeBtn.setEnabled(false);
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
                completeBtn.setEnabled(true);
                cancelBtn.setVisibility(View.GONE);
                if(mAssignment.reminder != null) {
                    scheduleNotification(this, mAssignment.reminder.getTime(), (int) mAssignment.reminder.getTime());
                }
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
        // Apply the adapter to the spinner
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setSelection(mAssignment.priority);
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
        ImageButton cancelBtn = findViewById(R.id.button_cancel_reminder);

        if (mAssignment.reminder == null) {
            reminderDate.setText(R.string.reminder_null);
        } else {
            // Put current reminder datetime in TextView
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.US);
            reminderDate.setText(formatter.format(mAssignment.reminder));
        }

        // Create Date and Time Pickers when user set reminder
        setReminderButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (mAssignment.reminder != null) {
                calendar.setTime(mAssignment.reminder);
            } else {
                calendar.setTime(new Date());
            }
            DatePickerDialog reminderDatePicker = new DatePickerDialog(this, this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            editDate = EditDate.REMINDER_DATE;
            reminderDatePicker.show();

        });

        // Cancel btn will stop upcoming alarm and reset reminder date
        cancelBtn.setOnClickListener(v -> {
            // Insert magical code that stops service

            // Reset reminder
            mAssignment.reminder = null;
            reminderDate.setText("");
        });
    }

    public void setupCreateDate() {
        // Create date should never change
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        TextView createDate = findViewById(R.id.text_create_date);
        createDate.setText(formatter.format(mAssignment.createDate));
    }

    public void setupCompleteButton() {
        LayoutInflater layoutInflater = getLayoutInflater();
        View addGradeView = layoutInflater.inflate(R.layout.dialog_add_grade, null);
        final AlertDialog alertD = new AlertDialog.Builder(this)
                .setTitle("Add Grade")
                .create();
        EditText gradeInput = addGradeView.findViewById(R.id.grade_points);
        EditText maxGradeInput = addGradeView.findViewById(R.id.max_grade_points);
        Button saveBtn = addGradeView.findViewById(R.id.button_add_grade_save);
        Button cancelBtn = addGradeView.findViewById(R.id.button_add_grade_cancel);
        Button noGradeBtn = addGradeView.findViewById(R.id.button_no_grade);
        Button setGradeBtn = findViewById(R.id.button_set_grade);
        Button completeBtn = findViewById(R.id.button_mark_complete);

        alertD.setView(addGradeView);
        completeBtn.setOnClickListener(v -> alertD.show());

        if (mAssignment.isComplete) {
            // update complete button to red with date
            completeBtn.setClickable(false);
            completeBtn.setBackgroundColor(Color.RED);
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            String formattedDate = formatter.format(mAssignment.completeDate);
            completeBtn.setText(getString(R.string.button_complete, formattedDate));
            completeBtn.setTypeface(completeBtn.getTypeface(), Typeface.BOLD);

            // Make grade component visible
            TextView grade = findViewById(R.id.text_grade);
            if (mAssignment.grade != -1) {
                grade.setVisibility(View.VISIBLE);
                grade.setText(getString(R.string.grade_value, mAssignment.grade, mAssignment.maxGrade));
            }
            setGradeBtn.setVisibility(View.VISIBLE);
            findViewById(R.id.title_grade).setVisibility(View.VISIBLE);
            findViewById(R.id.underline_grade).setVisibility(View.VISIBLE);
        }

        noGradeBtn.setOnClickListener(v -> {
            if (!mAssignment.isComplete) {
                mAssignment.isComplete = true;
                mAssignment.completeDate = resetTime(new Date());
                viewModel.updateAssignment(mAssignment);
                gradeInput.setText(R.string.no_grade_yet);
            }
            alertD.dismiss();
        });

        setGradeBtn.setOnClickListener(v -> {
            alertD.show();
        });

        saveBtn.setOnClickListener(v -> {
            if (gradeInput.getText().toString().matches("") ||
                    maxGradeInput.getText().toString().matches("")) {
                Toast.makeText(this, R.string.invalid_grade, Toast.LENGTH_SHORT).show();
            } else {
                if (!mAssignment.isComplete) {
                    mAssignment.isComplete = true;
                    mAssignment.completeDate = resetTime(new Date());
                }
                mAssignment.grade = Integer.parseInt(gradeInput.getText().toString());
                mAssignment.maxGrade = Integer.parseInt(maxGradeInput.getText().toString());
                alertD.dismiss();
                // force an update to show new button
                viewModel.updateAssignment(mAssignment);
            }
        });

        // Quit on cancel press
        cancelBtn.setOnClickListener(v -> alertD.dismiss());
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
        newDate = resetTime(calendar.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        switch (editDate) {
            case DUE_DATE:
                final TextView DueDateText = findViewById(R.id.text_due_date);
                String formattedDate = formatter.format(newDate);
                DueDateText.setText(formattedDate);
                mAssignment.dueDate = newDate;

                TextView days_left = findViewById(R.id.days_left);
                Date today = resetTime(new Date());
                long diffInMillies = mAssignment.dueDate.getTime() - today.getTime();
                long diff = TimeUnit.DAYS.convert(Math.abs(diffInMillies), TimeUnit.MILLISECONDS);
                if (diff == 1 && diffInMillies > 0) {
                    days_left.setText(R.string.days_left_one);
                } else if (diff == 0) {
                    days_left.setText(R.string.days_left_zero);
                } else if (diffInMillies < 0) {
                    days_left.setText(getString(R.string.days_left_lt_zero, diff));
                } else {// diff > 1
                    days_left.setText(getString(R.string.days_left_gt_one, diff));
                }
                break;
            case REMINDER_DATE:
                // Save date pick then move on to time
                Calendar todayCal = Calendar.getInstance();
                todayCal.setTime(new Date());
                Calendar timeCal = Calendar.getInstance();
                timeCal.setTime(newDate);
                TimePickerDialog reminderTimePicker = new TimePickerDialog(this, this,
                    todayCal.get(Calendar.HOUR_OF_DAY),
                    todayCal.get(Calendar.MINUTE),
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
    }

    public static Date resetTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public void scheduleNotification(Context context, long delay, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("ASSIGNMENT REMINDER")
                .setContentText(getString(R.string.notification_message, assignmentName, courseName))
                .setSmallIcon(R.drawable.icon_pencil)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(courseColor);

        Intent intent = new Intent(context, AssignmentDetailActivity.class);
        intent.putExtra("courseName", courseName);
        intent.putExtra("courseColor", courseColor);
        intent.putExtra("assignmentName", assignmentName);
        intent.putExtra("adding", isNewAssignment);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, ReminderPublisher.class);
        notificationIntent.putExtra(ReminderPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(ReminderPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "assignment_reminder_channel";
            String description = "";
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
