package com.songzheng.timemanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.entity.User;
import com.songzheng.timemanager.fragments.AboutFragment;
import com.songzheng.timemanager.fragments.ListFragment;
import com.songzheng.timemanager.fragments.NoteFragment;
import com.songzheng.timemanager.fragments.SettingFragment;
import com.songzheng.timemanager.service.impl.UserServiceImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * 应用程序主界面
 */
public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN_USER_INFO = 1;
    private static final int REQUEST_LOGOUT_INFO = 2;

    private User currentUser;

    @BindView(R.id.drawer_layout_home)
    protected DrawerLayout mDrawerLayoutInHome;

    @BindView(R.id.nav_view)
    protected NavigationView mNavigationView;

    protected TextView tvUserName;

    protected TextView tvUserEmail;

    private UserServiceImpl userService = new UserServiceImpl(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        //初次进入界面主界面都是笔记界面，要设置FrameLayout内容为NoteFragment
        replaceFragment(new NoteFragment());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home_activity);
        setSupportActionBar(toolbar);

//        mDrawerLayoutInHome = (DrawerLayout) findViewById(R.id.drawer_layout_home);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

//        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setCheckedItem(R.id.nav_note);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //用Fragment替换主界面
                switch (item.getItemId()) {
                    case R.id.nav_note:
                        //切换到笔记界面
                        replaceFragment(new NoteFragment());
                        break;
                    case R.id.nav_list:
                        //切换到清单界面
                        replaceFragment(new ListFragment());
                        break;
                    case R.id.nav_settings:
                        //切换到设置界面
                        replaceFragment(new SettingFragment());
                        break;
                    case R.id.nav_about:
                        //切换到关于界面
                        replaceFragment(new AboutFragment());
                        break;
                    default:
                }
                mDrawerLayoutInHome.closeDrawers();
                return true;
            }
        });

        //给NavigationView加载HeaderView，并且给NavigationView中Header中的头像设置点击事件
        View headerView = mNavigationView.inflateHeaderView(R.layout.nav_header);
        headerView.findViewById(R.id.ci_user_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO：这里要添加功能：
                // TODO：1、未登录点击头像登录；
                // TODO：2、已登录点击头像进入个人信息修改界面（头像、姓名、Email、密码、电话....）
                // TODO：3、一次登录，以后使用就不再登录（SharedPreferences）
                // 从SharedPreferences中获取用户信息，如果登陆过，这里点击进入个人信息修改界面
                User user = userService.getUserInfo();
                if (user != null) {
                    //表示登录过
                    enterPersonalActivity(user);
                } else {
                    Log.d("HomeActivity", "never login");
                    enterLoginActivity();
                }
            }
        });

        tvUserName = (TextView) headerView.findViewById(R.id.tv_user_name);
        tvUserEmail = (TextView) headerView.findViewById(R.id.tv_user_email);


        //在这里获取之前的登录信息，如果登陆过，则直接显示登录信息
        User userInfo = userService.getUserInfo();
        if (userInfo != null) {
            //表示之前登录过，显示个人信息
            tvUserName.setText(userInfo.getName());
            tvUserEmail.setText(userInfo.getEmail());
            currentUser = userInfo;
        }

        // TODO:通过userInfo从服务器端获取用户笔记信息
    }

    private void enterPersonalActivity(User user) {
        Intent intent = new Intent(HomeActivity.this, PersonalActivity.class);
        intent.putExtra("name", user.getName());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("phoneNumber", user.getPhone());
        startActivityForResult(intent, REQUEST_LOGOUT_INFO);
    }

    private void enterLoginActivity() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN_USER_INFO);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout_main, fragment).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayoutInHome.openDrawer(GravityCompat.START);
                break;
            case R.id.remind_message:
                Toasty.normal(this, "进入提醒界面", Toast.LENGTH_SHORT).show();
                break;
            case R.id.search_message:
                Toasty.normal(this, "搜索界面", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings_message:
                Toasty.normal(this, "设置", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN_USER_INFO:
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra("name");
                    String email = data.getStringExtra("email");
                    Log.d("HomeActivity测试", name);
                    tvUserName.setText(name);
                    tvUserEmail.setText(email);
                }
                break;

            case REQUEST_LOGOUT_INFO:
                if (resultCode == RESULT_OK) {
                    //注销回来了，清空下显示出来的个人信息
                    tvUserName.setText("");
                    tvUserEmail.setText("您还未登录，请点击头像登录");
                }
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayoutInHome.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayoutInHome.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
