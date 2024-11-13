package com.example.newtaskmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tasks (_id INTEGER PRIMARY KEY, task_name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }
}


public class TaskService {
    private DatabaseHelper dbHelper;

    public TaskService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void createTask(String taskName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO tasks (task_name) VALUES (?)", new Object[]{taskName});
        db.close();
    }

    public List<String> getTasks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT task_name FROM tasks", null);
        List<String> tasks = new ArrayList<>();
        while (cursor.moveToNext()) {
            tasks.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return tasks;
    }

    public void updateTask(int taskId, String newTaskName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE tasks SET task_name = ? WHERE _id = ?", new Object[]{newTaskName, taskId});
        db.close();
    }

    public void deleteTask(int taskId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM tasks WHERE _id = ?", new Object[]{taskId});
        db.close();
    }
}
