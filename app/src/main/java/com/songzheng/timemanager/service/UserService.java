package com.songzheng.timemanager.service;

import com.songzheng.timemanager.entity.Feedback;
import com.songzheng.timemanager.entity.Note;
import com.songzheng.timemanager.entity.Task;
import com.songzheng.timemanager.entity.TaskGroup;
import com.songzheng.timemanager.entity.User;
import com.songzheng.timemanager.listener.OnLoginListener;
import com.songzheng.timemanager.listener.OnRegisterListener;
import com.songzheng.timemanager.listener.OnSyncNoteListener;
import com.songzheng.timemanager.listener.OnSyncTasksListener;

/**
 * Created by make on 2017/6/9.
 */

public interface UserService {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     */
    void login(String username, String password);

    /**
     * 注册
     * @param user 用户
     */
    void register(User user);

    /**
     * 添加笔记
     * @param note 笔记对象
     * @param uid 用户ID
     */
    void addNote(Note note, String uid);

    /**
     * 删除笔记
     * @param note 笔记对象
     */
    void deleteNote(Note note);

    /**
     * 同步笔记
     */
    void syncNote();

    /**
     * 添加清单
     * @param taskGroup 清单对象
     * @param uid 用户id
     */
    void addTaskGroup(TaskGroup taskGroup, String uid);

    /**
     * 添加任务
     * @param task 任务对象
     * @param groupId 清单ID
     */
    void addTask(Task task, String groupId);

    /**
     * 删除任务
     * @param task 任务对象
     */
    void deleteTask(Task task);

    /**
     * 删除清单
     * @param taskGroup 清单对象
     */
    void deleteTaskGroup(TaskGroup taskGroup);

    /**
     * 同步任务
     */
    void syncTasks();

    /**
     * 反馈
     * @param feedback 反馈的实体对象
     */
    void feedBack(Feedback feedback);

    /**
     * 设置登录回调接口
     * @param loginListener
     */
    void setOnLoginListener(OnLoginListener loginListener);

    /**
     * 设置注册回调接口
     * @param registerListener
     */
    void setOnRegisterListener(OnRegisterListener registerListener);

    /**
     * 设置同步任务回调接口
     * @param syncTasksListener
     */
    void setOnSyncTasksListener(OnSyncTasksListener syncTasksListener);

    /**
     * 设置同步笔记回调接口
     * @param syncNoteListener
     */
    void setOnSyncNoteListener(OnSyncNoteListener syncNoteListener);
}
