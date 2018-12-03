package com.mobileapp.classmate.ui;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileapp.classmate.R;
import com.mobileapp.classmate.db.entity.Course;
import com.mobileapp.classmate.viewmodel.MainViewModel;

import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseViewHolder> {
    class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseItemView;
        LinearLayout courseItemViewBorder;
        private int mColor;
        private MainViewModel viewModel;

        public void setColor(int color) {
            mColor = color;
        }

        private CourseViewHolder(View itemView) {
            super(itemView);
            courseItemView = itemView.findViewById(R.id.class_name_textView);
            courseItemViewBorder = itemView.findViewById(R.id.class_name_textView_border);
            itemView.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, AssignmentSelectionActivity.class);
                intent.putExtra("courseName", courseItemView.getText().toString());
                intent.putExtra("courseColor", mColor);
                context.startActivity(intent);
            });

            itemView.setOnLongClickListener(v -> {
                viewModel = ViewModelProviders.of((ViewPagerMainActivity) v.getContext())
                        .get(MainViewModel.class);
                // show user delete class menu
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.dialog_title_delete_course);
                builder.setPositiveButton(R.string.button_delete, (dialog, which) -> {
                    // Delete from DB
                    // Should also delete all Course Assignments
                    viewModel.deleteCourse(courseItemView.getText().toString());
                });
                builder.setNegativeButton(R.string.button_cancel, (dialog, which) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            });
        }
    }

    private int courseItemLayout;
    private List<Course> mCourses;

    CourseListAdapter(int layoutId) {
        courseItemLayout = layoutId;
    }

    @Override
    @NonNull
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_selection_item, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        if (mCourses != null) {
            Course current = mCourses.get(position);
            holder.courseItemView.setText(current.courseName);
            holder.courseItemViewBorder.setBackgroundColor(current.color);
            holder.setColor(current.color);

            Integer[] images = new Integer[]{R.drawable.icon_pencil, R.drawable.icon_art,
                    R.drawable.icon_english, R.drawable.icon_history,
                    R.drawable.icon_lang, R.drawable.icon_math, R.drawable.icon_music,
                    R.drawable.icon_read, R.drawable.icon_science};

            holder.courseItemView.setCompoundDrawablesWithIntrinsicBounds(images[current.icon], 0, 0, 0);
        } else {
            // If there's no data
            holder.courseItemView.setText("No Courses!");
        }
    }

    void setCourses(List<Course> courses) {
        mCourses = courses;
        notifyDataSetChanged();
    }

    public boolean isCourse(String courseName) {
        for (Course course : mCourses) {
            if (course.courseName.equals(courseName))
                return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        if (mCourses != null)
            return mCourses.size();
        else return 0;
    }
}
