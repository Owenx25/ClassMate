package com.mobileapp.classmate.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.mobileapp.classmate.ClassmateRepository;
import com.mobileapp.classmate.db.entity.Assignment;
import com.mobileapp.classmate.db.entity.Course;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private ClassmateRepository repository;
    private LiveData<List<Assignment>> allAssignments;
    private LiveData<List<Course>> allCourses;

    public MainViewModel(Application application) {
        super(application);
        repository = new ClassmateRepository(application);
        allAssignments = repository.getAllAssignments();
        allCourses = repository.getAllCourses();
    }

    public LiveData<List<Assignment>> getAllAssignments() {
        return allAssignments;
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public void insertCourse(Course course){
        repository.insertCourse(course);
    }

    public void deleteCourse(String courseName) {
        repository.deleteCourse(courseName);
    }

    public void insertAssignment(Assignment assignment) {
        repository.insertAssignment(assignment);
    }

    public void deleteAssignment(String courseName, String name) {
        repository.deleteAssignment(courseName, name);
    }
}
