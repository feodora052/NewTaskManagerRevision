package com.example.newtaskmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private List<Task> taskList;
    private Context context;

    public TaskAdapter(Context context) {
        this.context = context;
        taskList = new ArrayList<>();
    }

    public void updateTaskList(List<Task> tasks) {
        taskList.clear();
        taskList.addAll(tasks);
        notifyDataSetChanged();
    }

    public void addTask(Task task) {
        taskList.add(task);
        notifyDataSetChanged();
    }

    public void deleteTask(int position) {
        taskList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Task getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false);
        }

        Task task = getItem(position);
        TextView tvTask = view.findViewById(R.id.tvTask);
        TextView tvDeadline = view.findViewById(R.id.tvDeadline);
        TextView tvNotes = view.findViewById(R.id.tvNotes);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        tvTask.setText(task.getTask());
        tvDeadline.setText("Deadline: " + task.getDeadline());
        tvNotes.setText("Notes: " + task.getNotes());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(position);
            }
        });

        return view;
    }
}
