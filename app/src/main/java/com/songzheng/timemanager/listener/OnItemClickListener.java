package com.songzheng.timemanager.listener;

import android.view.View;

/**
 * Created by make on 2017/4/29.
 */

public interface OnItemClickListener {

    /**
     * item点击回调
     * @param view
     * @param position
     */
    void onItemClick(View view, int position);

    /**
     * 删除按钮回调
     * @param position
     */
    void onDeleteClick(int position);
}
