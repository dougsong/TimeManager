package com.songzheng.timemanager.adapter;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songzheng.timemanager.R;

/**
 * Created by make on 2017/4/29.
 */

public class NoteViewHolder extends MyViewHolder {

    //ItemRemoveRecyclerView用这个layout实现点击事件
//    public LinearLayout layout;

    //这个view用来设置点击事件
    public CardView cardView;

    //条目内容
    public TextView tvModifiedTime;
    public TextView tvTitle;
    public TextView tvContent;

    public NoteViewHolder(View itemView) {
        super(itemView);
        cardView = (CardView) itemView;
        tvModifiedTime = (TextView) itemView.findViewById(R.id.tv_note_item_modified_time);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_note_item_title);
        tvContent = (TextView) itemView.findViewById(R.id.tv_note_item_content);
        layout = (LinearLayout) itemView.findViewById(R.id.note_item_layout);
    }
}
