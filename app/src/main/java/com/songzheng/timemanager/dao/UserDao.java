package com.songzheng.timemanager.dao;

import com.songzheng.timemanager.entity.Note;
import com.songzheng.timemanager.entity.Task;
import com.songzheng.timemanager.entity.TaskGroup;

import java.util.List;

/**
 * Created by make on 2017/6/27.
 */

/**
 * 用户的DAO层
 */
public interface UserDao {

    /**
     * 从客户端数据库查找笔记
     * @param uid 用户id
     * @return
     */
    List<Note> findNotes(String uid);

    /**
     * 从客户端数据库查找清单
     * @param uid 用户id
     * @return
     */
    List<TaskGroup> findGroups(String uid);

    /**
     * 从客户端数据库查找任务
     * @param gid 清单id
     * @return
     */
    List<Task> findTasks(String gid);

    /**
     * 设置当前数据库中所有笔记的uid
     * @param uid
     */
    void setNoteUid(String uid);

    /**
     * 保存笔记到数据库
     * @param notes
     */
    void saveNotes(List<Note> notes);

    /**
     * 删除所有笔记
     * @param uid 用户id
     */
    void deleteAllNote(String uid);

    /**
     * 删除所有笔记
     */
    void deleteAllNote();

}
