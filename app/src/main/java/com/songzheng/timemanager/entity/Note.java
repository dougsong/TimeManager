package com.songzheng.timemanager.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by make on 2017/4/8.
 */

public class Note extends DataSupport{

    private String nid;

    private String note_title;

    private String note_lastModifiedTime;

    private String note_content;

    private String user_id;

    public Note() {

    }

    public Note(String nid, String note_title, String note_lastModifiedTime, String note_content, String user_id) {
        this.nid = nid;
        this.note_title = note_title;
        this.note_lastModifiedTime = note_lastModifiedTime;
        this.note_content = note_content;
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }



    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public String getNote_lastModifiedTime() {
        return note_lastModifiedTime;
    }

    public void setNote_lastModifiedTime(String note_lastModifiedTime) {
        this.note_lastModifiedTime = note_lastModifiedTime;
    }

    public String getNote_content() {
        return note_content;
    }

    public void setNote_content(String note_content) {
        this.note_content = note_content;
    }

    @Override
    public String toString() {
        return "Note{" +
                "nid='" + nid + '\'' +
                ", note_title='" + note_title + '\'' +
                ", note_lastModifiedTime='" + note_lastModifiedTime + '\'' +
                ", note_content='" + note_content + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
