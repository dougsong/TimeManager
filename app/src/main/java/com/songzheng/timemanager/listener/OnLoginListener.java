package com.songzheng.timemanager.listener;

import com.songzheng.timemanager.entity.User;

/**
 * Created by make on 2017/6/9.
 */

public interface OnLoginListener {

    /**
     * 登陆成功时回调
     * @param user 从服务器返回的登录的用户的信息
     */
    void onLoginSuccess(User user);

    /**
     * 登录失败时回调
     * @param msg 错误信息
     */
    void onLoginFailed(int msg);

}
