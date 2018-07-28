package com.songzheng.timemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.entity.TaskGroup;

import java.util.List;

/**
 * Created by make on 2017/5/5.
 */

public class ListGroupAdapter extends RecyclerView.Adapter<ListGroupViewHolder> {

    private List<TaskGroup> list;

    //用来使用Glide加载图片、跳转界面
    private Context mContext;

    public ListGroupAdapter(List<TaskGroup> list) {
        this.list = list;
    }

    @Override
    public ListGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.list_group_item, parent, false);
        return new ListGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListGroupViewHolder holder, int position) {
        TaskGroup group = list.get(position);
        holder.tvListGroupTitle.setText(group.getGroup_title());
        holder.tvListCount.setText(group.getCount() + "");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
