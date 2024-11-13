package com.example.newtaskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    private TaskDBHelper dbHelper;


    public TaskDAO(Context context) {
        dbHelper = new TaskDBHelper(context);
    }

    public void saveUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        db.insert("users", null, values);
        db.close();
    }

    public boolean isValidUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", null, "email=? AND password=?", new String[]{email, password}, null, null, null);
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    public void addTask(String task, String deadline, String notes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("task", task);
        values.put("deadline", deadline);
        values.put("notes", notes);
        db.insert("tasks", null, values);
        db.close();
    }

    public List<String> getTasks() {
        List<String> tasks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("tasks", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                tasks.add(cursor.getString(cursor.getColumnIndex("task")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }
}