package com.songzheng.timemanager.listener;

/**
 * Created by make on 2017/6/27.
 */

public interface OnSyncTasksListener {

    /**
     * 同步任务成功
     */
    void onSyncTasksSuccess();

    /**
     * 同步任务失败
     * @param msg 错误信息
     */
    void onSyncTasksFailed(int msg);

}
