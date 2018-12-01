package com.mobileapp.classmate.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mobileapp.classmate.db.entity.Course;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Course course);

    @Insert
    void insertCourses(Course... courses);

    @Update
    void updateCourse(Course course);

    @Update
    void updateCourses(Course... courses);

    @Query("DELETE FROM Classes WHERE courseName LIKE '%' || :name || '%'")
    void delete(String name);

    @Query("DELETE FROM Classes")
    void deleteAll();

    @Query("SELECT * FROM Classes")
    LiveData<List<Course>> loadAllClasses();

    @Query("SELECT * FROM Classes WHERE courseName LIKE '%' || :course || '%'")
    LiveData<Course> getCourseLive(String course);

    @Query("SELECT * FROM Classes WHERE courseName LIKE '%' || :course || '%'")
    Course getCourse(String course);

    @Query("SELECT * FROM Classes WHERE color =:color")
    List<Course> getCourseViaColor(int color);

    @Query("SELECT color FROM Classes WHERE courseName LIKE '%' || :course || '%'")
    int getCourseColor(String course);
}
