package com.example.newtaskmanager;

public class Task {
    private String task;
    private String deadline;
    private String notes;

    public Task(String task, String deadline, String notes) {
        this.task = task;
        this.deadline = deadline;
        this.notes = notes;
    }

    public String getTask() {
        return task;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getNotes() {
        return notes;
    }
}