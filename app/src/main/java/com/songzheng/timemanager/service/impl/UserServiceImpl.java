package com.songzheng.timemanager.service.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.songzheng.timemanager.constant.Constant;
import com.songzheng.timemanager.dao.UserDao;
import com.songzheng.timemanager.dao.impl.UserDaoImpl;
import com.songzheng.timemanager.entity.Feedback;
import com.songzheng.timemanager.entity.Note;
import com.songzheng.timemanager.entity.Task;
import com.songzheng.timemanager.entity.TaskGroup;
import com.songzheng.timemanager.entity.User;
import com.songzheng.timemanager.listener.OnLoginListener;
import com.songzheng.timemanager.listener.OnRegisterListener;
import com.songzheng.timemanager.listener.OnSyncNoteListener;
import com.songzheng.timemanager.listener.OnSyncTasksListener;
import com.songzheng.timemanager.service.UserService;
import com.songzheng.timemanager.utils.MD5Utils;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by make on 2017/6/14.
 */

public class UserServiceImpl implements UserService {

    private Context mContext;

    private OnLoginListener loginListener;
    private OnRegisterListener registerListener;
    private OnSyncTasksListener syncTasksListener;
    private OnSyncNoteListener syncNoteListener;

    private UserDao userDao = new UserDaoImpl();

    private static final String SERVER_LOGIN_URL = Constant.LOGIN_URL;
    private static final String SERVER_REGISTER_URL = Constant.REGISTER_URL;
    private static final String SERVER_SYNC_NOTE_URL = Constant.SYNC_NOTE_URL;

    public static final int MSG_USER_EXIST = 10;
    public static final int MSG_REGISTER_SUCCESS = 11;
    public static final int MSG_REGISTER_FAILED = 12;
    public static final int MSG_NETWORK_ERROR = 13;
    public static final int MSG_SERVER_ERROR = 14;

    public static final int MSG_LOGIN_SERVER_ERROR = 20;       // 服务器错误
    public static final int MSG_LOGIN_NETWORK_ERROR = 21;      // 网络连接错误
    public static final int MSG_LOGIN_NAME_OR_PWD_ERROR = 22;  // 用户名或密码错误
    public static final int MSG_LOGIN_SUCCESS = 23;

    public static final int MSG_SYNC_TASK_SUCCESS = 30;
    public static final int MSG_SYNC_TASK_FAILED = 31;

    public static final int MSG_SYNC_NOTE_SUCCESS = 40;
    public static final int MSG_SYNC_NOTE_FAILED = 41;
    public static final int MSG_SYNC_NOTE_NOT_LOGIN = 42;

    @Override
    public void setOnLoginListener(OnLoginListener loginListener) {
        this.loginListener = loginListener;
    }

    @Override
    public void setOnRegisterListener(OnRegisterListener registerListener) {
        this.registerListener = registerListener;
    }

    @Override
    public void setOnSyncTasksListener(OnSyncTasksListener syncTasksListener) {
        this.syncTasksListener = syncTasksListener;
    }

    @Override
    public void setOnSyncNoteListener(OnSyncNoteListener syncNoteListener) {
        this.syncNoteListener = syncNoteListener;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 对登录结果进行处理
                case MSG_LOGIN_SERVER_ERROR:
                    loginListener.onLoginFailed(MSG_LOGIN_SERVER_ERROR);
                    break;

                case MSG_LOGIN_NETWORK_ERROR:
                    loginListener.onLoginFailed(MSG_LOGIN_NETWORK_ERROR);
                    break;

                case MSG_LOGIN_NAME_OR_PWD_ERROR:
                    loginListener.onLoginFailed(MSG_LOGIN_NAME_OR_PWD_ERROR);
                    break;

                case MSG_LOGIN_SUCCESS:
                    User user = (User) msg.obj;
                    saveLoginInfo(user);
                    userDao.deleteAllNote();
                    loginListener.onLoginSuccess(user);
                    break;

                // 对注册结果进行UI上的处理
                case MSG_NETWORK_ERROR:
                    registerListener.onRegisterFailed(MSG_NETWORK_ERROR);
                    break;

                case MSG_SERVER_ERROR:
                    registerListener.onRegisterFailed(MSG_SERVER_ERROR);
                    break;

                case MSG_USER_EXIST:
                    registerListener.onRegisterFailed(MSG_USER_EXIST);
                    break;

                case MSG_REGISTER_FAILED:
                    registerListener.onRegisterFailed(MSG_REGISTER_FAILED);
                    break;

                case MSG_REGISTER_SUCCESS:
                    registerListener.onRegisterSuccess();
                    break;

                // 对同步任务进行处理
                case MSG_SYNC_TASK_SUCCESS:
                    syncTasksListener.onSyncTasksSuccess();
                    break;

                case MSG_SYNC_TASK_FAILED:
                    syncTasksListener.onSyncTasksFailed(MSG_SYNC_TASK_FAILED);
                    break;

                // 对同步笔记进行处理
                case MSG_SYNC_NOTE_SUCCESS:
                    List<Note> notes = (List<Note>) msg.obj;

                    if (notes.size() > 0) {
                        // 有笔记
                        for (Note note : notes) {
                            note.setUser_id(getUserInfo().getUid());
                            Log.d("note:", note.toString());
                        }
                        userDao.saveNotes(notes);
                    }

                    syncNoteListener.onSyncNoteSuccess();
                    break;

                case MSG_SYNC_NOTE_FAILED:
                    syncNoteListener.onSyncNoteFailed(MSG_SYNC_NOTE_FAILED);
                    break;

                case MSG_SYNC_NOTE_NOT_LOGIN:
                    syncNoteListener.onSyncNoteFailed(MSG_SYNC_NOTE_NOT_LOGIN);
                    break;

                default:
                    break;
            }
        }
    };

    public UserServiceImpl(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void login(final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                //将用户名和密码提交到服务器
                OkHttpClient client = new OkHttpClient();
                //使用POST方式提交
                RequestBody requestBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();
                Request request = new Request.Builder()
                        .url(SERVER_LOGIN_URL)
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String loginResult = response.body().string();
                    Log.d("LoginActivity:Result", loginResult);
                    User user = handleLoginResult(loginResult);
                    if (user == null) {
                        //用户名或密码错误
                        msg.what = MSG_LOGIN_NAME_OR_PWD_ERROR;
                    } else {
                        //user不为空，登陆成功
                        msg.what = MSG_LOGIN_SUCCESS;
                        //这里传不过去就用成员变量
                        msg.obj = user;
                        Log.d("LoginActivity姓名", user.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = MSG_LOGIN_NETWORK_ERROR;
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void register(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();

                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("uid", user.getUid())
                        .add("name", user.getName())
                        .add("email", user.getEmail())
                        .add("username", user.getUsername())
                        .add("password", MD5Utils.MD5Encode(user.getPassword(), "UTF-8", false))
                        .add("phone", user.getPhone())
                        .build();

                Request request = new Request.Builder()
                        .url(SERVER_REGISTER_URL)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String result = response.body().string();

                    Log.d("RegisterActivity：注册结果", result);

                    if (String.valueOf(Constant.REGISTER_SUCCESS).equals(result)) {
                        //注册成功
                        Log.d("RegisterActivity", "注册成功！");
                        msg.what = MSG_REGISTER_SUCCESS;
                    } else if (result.equals(String.valueOf(Constant.REGISTER_SERVER_ERROR).equals(result))){
                        //注册失败
                        Log.d("RegisterActivity", "Failed");
                        msg.what = MSG_REGISTER_FAILED;
                    } else if (String.valueOf(Constant.REGISTER_USERNAME_EXIST).equals(result)) {
                        Log.d("RegisterActivity", "用户名已经存在！");
                        msg.what = MSG_USER_EXIST;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = MSG_SERVER_ERROR;
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void addNote(Note note, String uid) {

    }

    @Override
    public void deleteNote(Note note) {

    }

    /**
     * 同步任务，将从服务器获取的数据保存在本地数据库
     */
    @Override
    public void syncTasks() {
        final Message msg = Message.obtain();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    msg.what = MSG_SYNC_TASK_SUCCESS;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void feedBack(Feedback feedback) {

    }

    /**
     * 同步笔记，将从服务器获取的数据保存在本地数据库
     */
    @Override
    public void syncNote() {
        final Message msg = Message.obtain();
        final User user = getUserInfo();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (user == null) {
                    // 还没登录
                    Log.d("Sync Note: ", "Not Login");
                    msg.what = MSG_SYNC_NOTE_NOT_LOGIN;
                } else {
                    // 同步
                    try {
                        String uid = user.getUid();
//                        Gson gson = new Gson();

                        userDao.setNoteUid(uid);
                        List<Note> notes = userDao.findNotes(uid);
                        String notesJson = JSON.toJSONString(notes);

                        Log.d("notesJson", notesJson);

                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = new FormBody.Builder()
                                .add("note_Json", notesJson)
                                .add("user_id", uid)
                                .build();

                        Request request = new Request.Builder()
                                .post(body)
                                .url(SERVER_SYNC_NOTE_URL)
                                .build();

                        String serverNotesJson = client.newCall(request).execute().body().string();

                        Log.d("serverNotesJson", serverNotesJson);

                        msg.obj = JSON.parseArray(serverNotesJson, Note.class);
                        msg.what = MSG_SYNC_NOTE_SUCCESS;
                    } catch (IOException e) {
                        e.printStackTrace();
                        msg.what = MSG_SYNC_NOTE_FAILED;
                    }
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void addTaskGroup(TaskGroup taskGroup, String uid) {

    }

    @Override
    public void addTask(Task task, String groupId) {

    }

    @Override
    public void deleteTask(Task task) {

    }

    @Override
    public void deleteTaskGroup(TaskGroup taskGroup) {

    }

    private void saveLoginInfo(User user) {
        // 使用SharedPreferences保存登录成功后的用户信息
        SharedPreferences prefs = mContext.getSharedPreferences("user.info", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("uid", user.getUid());
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("username", user.getUsername());
        editor.putString("phone", user.getPhone());
        editor.apply();
    }

    /**
     * 获取当前SharedPreferences中保存的用户信息，即当前登录的用户信息
     * @return
     */
    public User getUserInfo() {
        SharedPreferences prefs = mContext.getSharedPreferences("user.info", MODE_PRIVATE);

        String uid = prefs.getString("uid", null);
        String name = prefs.getString("name", null);
        String phone = prefs.getString("phone", null);
        String email = prefs.getString("email", null);
        String username = prefs.getString("username", null);

        Log.d("UserService", "uid:" + uid);

        if (uid == null) {
            // 用户还没登录
            return null;
        }

        return new User(uid, name, email, username, null, phone);
    }

    private User handleLoginResult(String loginJsonInfo) {
        Gson gson = new Gson();
        Log.d("handleLoginResult", loginJsonInfo);
        if (String.valueOf(Constant.LOGIN_USER_NOT_EXIST).equals(loginJsonInfo)) {
            //用户名或密码错误，服务器返回的用户数据为null
            return null;
        } else {
            User user = gson.fromJson(loginJsonInfo, User.class);
            return user;
        }
    }



}
