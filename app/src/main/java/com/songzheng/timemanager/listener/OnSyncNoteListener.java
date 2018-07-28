package com.songzheng.timemanager.listener;

/**
 * Created by make on 2017/6/27.
 */

public interface OnSyncNoteListener {

    /**
     * 同步笔记成功回调的方法
     */
    void onSyncNoteSuccess();

    /**
     * 同步笔记失败回调的方法
     * @param msg 失败信息
     */
    void onSyncNoteFailed(int msg);

}
