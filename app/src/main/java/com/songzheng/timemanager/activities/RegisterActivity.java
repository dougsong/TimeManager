package com.songzheng.timemanager.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.entity.User;
import com.songzheng.timemanager.listener.OnRegisterListener;
import com.songzheng.timemanager.service.UserService;
import com.songzheng.timemanager.service.impl.UserServiceImpl;
import com.songzheng.timemanager.utils.UUIDUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.iv_register_back)
    protected ImageView ivRegisterBack;

    @BindView(R.id.bt_register)
    protected Button btRegister;

    @BindView(R.id.et_register_username)
    protected EditText etUsername;

    @BindView(R.id.et_register_password)
    protected EditText etPassword;

    @BindView(R.id.et_register_name)
    protected EditText etName;

    @BindView(R.id.et_register_email)
    protected EditText etEmail;

    @BindView(R.id.et_register_phone)
    protected EditText etPhoneNumber;

    private UserService userService = null;
    private OnRegisterListener listener = new RegisterListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        userService = new UserServiceImpl(this);

        ivRegisterBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = getRegisterUser();
                if (user == null) {
                    // 须填写项没填写完
                    Toasty.warning(RegisterActivity.this, "需填写所有项！", Toast.LENGTH_SHORT).show();
                } else {
//                    register(user);
                    userService.setOnRegisterListener(listener);
                    userService.register(user);
                }
            }
        });
    }

    public User getRegisterUser() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhoneNumber.getText().toString();

        if ("".equals(username) || "".equals(password) || "".equals(name) || "".equals(email) || "".equals(phone)) {
            return null;
        }

        return new User(UUIDUtils.getId(), name, email, username, password, phone);
    }

    class RegisterListener implements OnRegisterListener {

        @Override
        public void onRegisterSuccess() {
            Toasty.success(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT, true).show();
            finish();
        }

        @Override
        public void onRegisterFailed(int msg) {
            switch (msg) {
                //对注册结果进行UI上的处理
                case UserServiceImpl.MSG_NETWORK_ERROR:
                    Toasty.error(RegisterActivity.this, "网络错误！", Toast.LENGTH_SHORT, true).show();
                    break;

                case UserServiceImpl.MSG_SERVER_ERROR:
                    Toasty.error(RegisterActivity.this, "服务器错误！", Toast.LENGTH_SHORT, true).show();
                    break;

                case UserServiceImpl.MSG_USER_EXIST:
                    Toasty.error(RegisterActivity.this, "用户名已存在！", Toast.LENGTH_SHORT, true).show();
                    break;

                case UserServiceImpl.MSG_REGISTER_FAILED:
                    Toasty.error(RegisterActivity.this, "注册失败！", Toast.LENGTH_SHORT, true).show();
                    break;

                default:
                    break;
            }
        }
    }
}
