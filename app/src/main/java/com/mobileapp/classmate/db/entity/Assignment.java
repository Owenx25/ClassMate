package com.mobileapp.classmate.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.mobileapp.classmate.db.converter.DateConverter;

import java.util.Date;

@TypeConverters(DateConverter.class)
@Entity(tableName = "Assignments")
public class Assignment {
    public Assignment(@NonNull String name, @NonNull String className, int priority,
                      Date dueDate,Date createDate, boolean isComplete, @NonNull String reminder,
                      @NonNull String description){
        this.name = name;
        this.className = className;
        this.priority = priority;
        this.dueDate = dueDate;
        this.createDate = createDate;
        this.isComplete = isComplete;
        this.reminder = reminder;
        this.description = description;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String className;
    public int priority;
    public Date dueDate;
    public Date createDate;
    public Boolean isComplete;
    public String reminder;
    public String description;
}
