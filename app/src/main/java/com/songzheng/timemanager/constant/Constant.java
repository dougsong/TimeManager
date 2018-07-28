package com.songzheng.timemanager.constant;

/**
 * Created by make on 2017/6/14.
 */

/**
 * 定义的一些常量
 */
public class Constant {

    private static final String SERVER_HOST = "REMOTE_SERVER_IP";// Exp: "http://192.168.56.1:8080";

    private static final String SERVER_PROJECT_CONTEXT = "/tm";//"/TimeManager";

    private static final String SERVER_MODULE_LOGIN = "/user_login";

    private static final String SERVER_MODULE_REGISTER = "/user_register";

    private static final String SERVER_MODULE_SYNC_NOTE = "/user_syncNote";

    private static final String SERVER_UPDATE_INFO = "/updateinfo.json";

    private static final String SERVER_FEEDBACK = "/feedback";

    public static final String UPDATE_INFO = SERVER_HOST + SERVER_PROJECT_CONTEXT + SERVER_UPDATE_INFO;//SERVER_HOST + SERVER_UPDATE_INFO;

    public static final String LOGIN_URL = SERVER_HOST + SERVER_PROJECT_CONTEXT + SERVER_MODULE_LOGIN;

    public static final String REGISTER_URL = SERVER_HOST + SERVER_PROJECT_CONTEXT + SERVER_MODULE_REGISTER;

    public static final String SYNC_NOTE_URL = SERVER_HOST + SERVER_PROJECT_CONTEXT + SERVER_MODULE_SYNC_NOTE;

    public static final String SERVER_FEEDBACK_URL = SERVER_HOST + SERVER_PROJECT_CONTEXT + SERVER_FEEDBACK;

    public static final int REGISTER_USERNAME_EXIST = 0;

    public static final int REGISTER_SERVER_ERROR = 1;

    public static final int REGISTER_SUCCESS = 2;

    public static final int LOGIN_USER_NOT_EXIST = 3;

    public static final int SAVE_USER_SUCCESS = 4;

    public static final int SAVE_USER_ERROR = 5;

}