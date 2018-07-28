package com.songzheng.timemanager.entity;

/**
 * Created by make on 2017/4/8.
 */

public class User {

    private String uid;

    private String name;

    private String email;

    private String username;

    private String password;

    private String phone;

    public User() {
    }

    public User(String uid, String name, String email, String username, String password, String phone) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phone = phone;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
