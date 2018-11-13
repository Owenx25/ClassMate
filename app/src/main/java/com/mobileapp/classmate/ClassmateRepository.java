package com.mobileapp.classmate;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.mobileapp.classmate.db.entity.Assignment;
import com.mobileapp.classmate.db.dao.AssignmentDao;
import com.mobileapp.classmate.db.entity.Course;
import com.mobileapp.classmate.db.dao.CourseDao;

import java.util.List;

public class ClassmateRepository {
    private CourseDao mCourseDao;
    private AssignmentDao mAssignmentDao;

    private LiveData<List<Course>> mAllClasses;
    private LiveData<List<Assignment>> mAllAssignments;

    public ClassmateRepository(Application application) {
        ClassmateRoomDatabase db = ClassmateRoomDatabase.getDatabase(application);
        mCourseDao = db.courseDao();
        mAssignmentDao = db.assignmentDao();
        mAllAssignments = mAssignmentDao.loadAllAssignments();
        mAllClasses = mCourseDao.loadAllClasses();
    }

    public LiveData<List<Assignment>> getAllAssignments() {
        return mAllAssignments;
    }

    public LiveData<List<Course>> getAllCourses() {
        return mAllClasses;
    }

    public void insertAssignment(Assignment assignment) {
        new insertAssignmentAsyncTask(mAssignmentDao).execute();
    }

    public void insertCourse(Course course) {
        new insertCourseAsyncTask(mCourseDao).execute(course);
    }

    public void deleteCourse(String name) {
        new deleteCourseAsyncTask(mCourseDao).execute(name);
    }

    public void deleteAssignment(String courseName, String assignName) {
        new deleteAssignmentAsyncTask(mAssignmentDao).execute(courseName, assignName);
    }


    // Insert an Assignment on a separate thread
    private static class insertAssignmentAsyncTask extends AsyncTask<Assignment, Void, Void> {
        private AssignmentDao mAsyncTaskDao;

        insertAssignmentAsyncTask(AssignmentDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Assignment... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    // Delete an Assignment on a separate thread
    private static class deleteAssignmentAsyncTask extends AsyncTask<String, Void, Void> {
        private AssignmentDao asyncAssignmentDao;

        deleteAssignmentAsyncTask(AssignmentDao dao){
            asyncAssignmentDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            asyncAssignmentDao.delete(params[0],params[1]);
            return null;
        }
    }

    // Insert a Course on a separate thread
    private static class insertCourseAsyncTask extends AsyncTask<Course, Void, Void> {
        private CourseDao mAsyncTaskDao;

        insertCourseAsyncTask(CourseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Course... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    // Delete a course on a separate thread
    private static class deleteCourseAsyncTask extends AsyncTask<String, Void, Void> {
        private CourseDao asyncCourseDao;

        deleteCourseAsyncTask(CourseDao dao) {
            asyncCourseDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            asyncCourseDao.delete(params[0]);
            return null;
        }
    }
}
