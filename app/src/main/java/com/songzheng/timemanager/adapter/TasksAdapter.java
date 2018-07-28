package com.songzheng.timemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.entity.Task;

import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by make on 2017/5/12.
 */

public class TasksAdapter extends RecyclerView.Adapter<TasksViewHolder> {

    private List<Task> list;

    private Context mContext;

    public TasksAdapter(List<Task> list) {
        this.list = list;
    }

    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.list_group_task_item, parent, false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder holder, int position) {
        Task task = list.get(position);

        holder.cbTaskStar.setChecked(task.getStar());
        holder.tvTaskContent.setText(task.getTask_content());

        holder.cbTaskStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.success(mContext, "添加星标！", Toast.LENGTH_SHORT, true).show();
                // TODO:添加星标，把数据更新
            }
        });

        holder.cbTaskComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.success(mContext, "任务完成....->隐藏！", Toast.LENGTH_SHORT, true).show();
                // TODO:完成任务，更新数据并隐藏
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
