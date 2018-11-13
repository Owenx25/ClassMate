package com.mobileapp.classmate;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.mobileapp.classmate.db.dao.AssignmentDao;
import com.mobileapp.classmate.db.dao.CourseDao;
import com.mobileapp.classmate.db.entity.Assignment;
import com.mobileapp.classmate.db.entity.Course;

import java.util.Date;

@Database(entities = {Assignment.class, Course.class}, version = 1)
public abstract class ClassmateRoomDatabase extends RoomDatabase {
    public abstract CourseDao courseDao();
    public abstract AssignmentDao assignmentDao();

    private static volatile ClassmateRoomDatabase INSTANCE;

    static ClassmateRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ClassmateRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    ClassmateRoomDatabase.class, "classmate_database")
                    //.addCallback(sRoomDatabaseCallback)
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
