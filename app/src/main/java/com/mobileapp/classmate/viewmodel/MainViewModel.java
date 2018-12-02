package com.mobileapp.classmate.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.mobileapp.classmate.ClassmateRepository;
import com.mobileapp.classmate.db.entity.Assignment;
import com.mobileapp.classmate.db.entity.Course;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private ClassmateRepository repository;
    private LiveData<List<Assignment>> allAssignments;
    private LiveData<List<Course>> allCourses;
    private LiveData<List<Assignment>> tomorrowAssignments;
    private MutableLiveData<Assignment> currentAssignment;

    public MainViewModel(Application application) {
        super(application);
        repository = new ClassmateRepository(application);
        allAssignments = repository.getAllAssignments();
        tomorrowAssignments = repository.getTomorrowAssignments();
        allCourses = repository.getAllCourses();
    }

    public LiveData<List<Assignment>> getAllAssignments() {
        return allAssignments;
    }

    public LiveData<List<Assignment>> getTomorrowAssignments() {
        return tomorrowAssignments;
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public LiveData<Course> getCourse(String name) { return repository.getCourse(name); }

    public LiveData<Assignment> getAssignment(String courseName, String name) {
        return repository.getAssignment(courseName, name);
    }

    public LiveData<List<Assignment>> getCourseAssignments(String course) {
        return repository.getCourseAssignments(course);
    }

    public void updateAssignment(Assignment assignment) {
        repository.updateAssignment(assignment);
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
