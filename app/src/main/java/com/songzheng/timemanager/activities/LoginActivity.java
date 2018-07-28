package com.songzheng.timemanager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.entity.User;
import com.songzheng.timemanager.listener.OnLoginListener;
import com.songzheng.timemanager.listener.OnSyncNoteListener;
import com.songzheng.timemanager.service.UserService;
import com.songzheng.timemanager.service.impl.UserServiceImpl;
import com.songzheng.timemanager.utils.MD5Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.bt_user_login)
    protected Button btLogin;

    @BindView(R.id.et_user_login_name)
    protected EditText etUsername;

    @BindView(R.id.et_user_login_password)
    protected EditText etPassword;

    @BindView(R.id.tv_user_register)
    protected TextView tvRegister;

    @BindView(R.id.pb_login)
    protected ProgressBar pbLogin;

    @BindView(R.id.iv_login_back)
    protected ImageView ivLoginBack;

    private OnLoginListener listener = new LoginListener();
    private OnSyncNoteListener syncNoteListener = new SyncNoteListener();

    private UserService userService;

    private void loginSuccess(User user) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("name", user.getName());
        intent.putExtra("email", user.getEmail());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        userService = new UserServiceImpl(this);

        ivLoginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if ("".equals(username) || "".equals(password)) {
                    Toasty.error(LoginActivity.this, "用户名或密码不能为空！", Toast.LENGTH_SHORT, true).show();
                } else {
                    pbLogin.setVisibility(View.VISIBLE);
//                    login(username, password);
                    userService.setOnLoginListener(listener);
                    userService.login(username, MD5Utils.MD5Encode(password, "UTF-8", false));
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterRegisterActivity();
            }
        });
    }

    private void enterRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    class LoginListener implements OnLoginListener {

        @Override
        public void onLoginSuccess(User user) {
            Log.d("LoginActivity", "login success");
            pbLogin.setVisibility(View.INVISIBLE);
            // 同步笔记
//            userService.setOnSyncNoteListener(syncNoteListener);
//            userService.syncNote();
            // TODO: 同步清单
            loginSuccess(user);
        }

        @Override
        public void onLoginFailed(int msg) {
            switch (msg) {
                case UserServiceImpl.MSG_LOGIN_SERVER_ERROR:
                    Toasty.error(LoginActivity.this, "服务器异常！", Toast.LENGTH_SHORT, true).show();
                    break;

                case UserServiceImpl.MSG_LOGIN_NETWORK_ERROR:
                    Toasty.error(LoginActivity.this, "网络连接异常！", Toast.LENGTH_SHORT, true).show();
                    break;

                case UserServiceImpl.MSG_LOGIN_NAME_OR_PWD_ERROR:
                    Toasty.error(LoginActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT, true).show();
                    break;
                default:
            }
            pbLogin.setVisibility(View.INVISIBLE);
        }
    }

    class SyncNoteListener implements OnSyncNoteListener {

        @Override
        public void onSyncNoteSuccess() {

        }

        @Override
        public void onSyncNoteFailed(int msg) {

        }
    }
}
