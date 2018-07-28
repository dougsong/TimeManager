package com.songzheng.timemanager.entity;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by make on 2017/5/5.
 */

public class Task extends DataSupport {

    private String tid;

    private String task_content;

    private Boolean complete;

    private Boolean star;

    private Date expireTime;

    private String task_group_id;

    public String getTask_group_id() {
        return task_group_id;
    }

    public void setTask_group_id(String task_group_id) {
        this.task_group_id = task_group_id;
    }

    public Task(String tid, String task_content, Boolean complete, Boolean star, Date expireTime, String task_group_id) {

        this.tid = tid;
        this.task_content = task_content;
        this.complete = complete;
        this.star = star;
        this.expireTime = expireTime;
        this.task_group_id = task_group_id;
    }

    public Task() {
    }


    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTask_content() {
        return task_content;
    }

    public void setTask_content(String task_content) {
        this.task_content = task_content;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Boolean getStar() {
        return star;
    }

    public void setStar(Boolean star) {
        this.star = star;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
