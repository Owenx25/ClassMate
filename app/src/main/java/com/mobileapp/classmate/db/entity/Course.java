package com.mobileapp.classmate.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.mobileapp.classmate.db.converter.DateConverter;

import java.util.Date;

@TypeConverters(DateConverter.class)
@Entity(tableName = "Classes")
public class Course {
    public Course(@NonNull String courseName, Date createDate, int color) {
        this.courseName = courseName;
        this.color = color;
        this.createDate = createDate;
    }

    public Course() {
        courseName = "";
        color = 0;
        createDate = new Date();
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String courseName;
    public Date createDate;
    public int color;
}
