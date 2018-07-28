package com.songzheng.timemanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.listener.OnSyncNoteListener;
import com.songzheng.timemanager.service.UserService;
import com.songzheng.timemanager.service.impl.UserServiceImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalActivity extends AppCompatActivity {

    private static final String TAG = "PersonalActivity";

    @BindView(R.id.toolbar_info)
    protected Toolbar tbInfo;

    @BindView(R.id.collapsing_personal_info)
    protected CollapsingToolbarLayout collapsingToolbarInfo;

    @BindView(R.id.ci_user_info_image)
    protected CircleImageView ciInfoUserImage;

    @BindView(R.id.bt_logout)
    protected Button btLogout;

    @BindView(R.id.rl_setting_vip)
    protected RelativeLayout rlSettingVIP;

    private OnSyncNoteListener listener = new SyncNoteListener();

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        ButterKnife.bind(this);

        setSupportActionBar(tbInfo);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        userService = new UserServiceImpl(this);

        collapsingToolbarInfo.setTitle("个人信息");

        //Glide.with(this).load(R.drawable.nav_icon).into(ciInfoUserImage);
        ciInfoUserImage.setImageResource(R.drawable.nav_icon);

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击注销按钮，清空用户已登录信息，并且退出到主界面,并且要让主界面的用户信息消失（清空SharedPreferences中的数据重启主界面即可）
                userService.setOnSyncNoteListener(listener);
                userService.syncNote();
                enterHomeActivity();
            }
        });

        rlSettingVIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PersonalActivity.this, "转我支付宝", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enterHomeActivity() {
        Intent intent = new Intent(PersonalActivity.this, HomeActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void clearUserLoginInfo() {
        SharedPreferences prefs = getSharedPreferences("user.info", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("uid", null);
        editor.putString("name", null);
        editor.putString("email", null);
        editor.putString("phone", null);
        editor.putString("username", null);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class SyncNoteListener implements OnSyncNoteListener {

        @Override
        public void onSyncNoteSuccess() {
            clearUserLoginInfo();
        }

        @Override
        public void onSyncNoteFailed(int msg) {
            clearUserLoginInfo();
        }
    }
}
