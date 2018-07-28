package com.songzheng.timemanager.dao.impl;

import android.content.ContentValues;

import com.songzheng.timemanager.dao.UserDao;
import com.songzheng.timemanager.entity.Note;
import com.songzheng.timemanager.entity.Task;
import com.songzheng.timemanager.entity.TaskGroup;


import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by make on 2017/6/27.
 */

public class UserDaoImpl implements UserDao {

    @Override
    public List<Note> findNotes(String uid) {
        return DataSupport.where("user_id = ?", uid).find(Note.class);
    }

    @Override
    public List<TaskGroup> findGroups(String uid) {
        return DataSupport.where("user_id = ?", uid).find(TaskGroup.class);
    }

    @Override
    public List<Task> findTasks(String gid) {
        return DataSupport.where("task_group_id = ?", gid).find(Task.class);
    }

    @Override
    public void setNoteUid(String uid) {
        ContentValues values = new ContentValues();
        values.put("user_id", uid);
        DataSupport.updateAll(Note.class, values, "user_id is null");

    }

    @Override
    public void saveNotes(List<Note> notes) {
        deleteAllNote(notes.get(0).getUser_id());
        for (Note note : notes) {
            if (!isExist(note)) {
                note.save();
            }
        }
    }

    @Override
    public void deleteAllNote(String uid) {
        DataSupport.deleteAll(Note.class, "user_id = ?", uid);
    }

    @Override
    public void deleteAllNote() {
        DataSupport.deleteAll(Note.class);
    }


    private boolean isExist(Note note) {
        List<Note> notes = DataSupport.where("nid = ?", note.getNid()).find(Note.class);

        if (notes.size() <= 0) {
            return false;
        }

        return true;
    }
}
