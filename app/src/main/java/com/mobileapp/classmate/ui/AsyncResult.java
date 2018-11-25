package com.mobileapp.classmate.ui;

import com.mobileapp.classmate.db.entity.Assignment;

import java.util.List;

public interface AsyncResult {
    void asyncFinished(Assignment result);
}
