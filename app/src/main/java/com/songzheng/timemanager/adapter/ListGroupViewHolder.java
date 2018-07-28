package com.songzheng.timemanager.adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songzheng.timemanager.R;

/**
 * Created by make on 2017/5/5.
 */

public class ListGroupViewHolder extends MyViewHolder {

    public TextView tvListGroupTitle;
    public TextView tvListCount;

    public ListGroupViewHolder(View itemView) {
        super(itemView);
        layout = (LinearLayout) itemView.findViewById(R.id.linear_layout_list_group_item);
        tvListCount = (TextView) itemView.findViewById(R.id.tv_list_group_list_count);
        tvListGroupTitle = (TextView) itemView.findViewById(R.id.tv_list_group_title);
    }
}
