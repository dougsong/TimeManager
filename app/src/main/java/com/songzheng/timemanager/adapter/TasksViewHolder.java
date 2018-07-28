package com.songzheng.timemanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songzheng.timemanager.R;

/**
 * Created by make on 2017/5/12.
 */

public class TasksViewHolder extends MyViewHolder{

    public CheckBox cbTaskComplete;
    public TextView tvTaskContent;
    public CheckBox cbTaskStar;

    public TasksViewHolder(View itemView) {
        super(itemView);
        layout = (LinearLayout) itemView.findViewById(R.id.task_item_layout);
        cbTaskComplete = (CheckBox) itemView.findViewById(R.id.cb_task_complete);
        tvTaskContent = (TextView) itemView.findViewById(R.id.tv_task_content);
        cbTaskStar = (CheckBox) itemView.findViewById(R.id.cb_task_star);
    }
}
