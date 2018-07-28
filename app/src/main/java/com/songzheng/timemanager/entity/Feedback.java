package com.songzheng.timemanager.entity;

/**
 * Created by make on 2017/7/4.
 */

public class Feedback {

    private String uid;

    private String content;

    public Feedback() {
    }

    public Feedback(String uid, String content) {

        this.uid = uid;
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
