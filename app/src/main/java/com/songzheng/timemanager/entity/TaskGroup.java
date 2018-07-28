package com.songzheng.timemanager.entity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by make on 2017/5/5.
 */

public class TaskGroup extends DataSupport implements Serializable {

    private String gid;

    private String group_title;

    private Integer count;

    private String user_id;

    public TaskGroup(String gid, String group_title, Integer count, String user_id) {

        this.gid = gid;
        this.group_title = group_title;
        this.count = count;
        this.user_id = user_id;
    }

    public TaskGroup() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getGroup_title() {
        return group_title;
    }

    public void setGroup_title(String group_title) {
        this.group_title = group_title;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
