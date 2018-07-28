package com.songzheng.timemanager.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.songzheng.timemanager.R;
import com.songzheng.timemanager.constant.Constant;
import com.songzheng.timemanager.listener.DownloadBinderListener;
import com.songzheng.timemanager.service.DownloadService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * SplashActivity:进入应用程序的欢迎界面
 */
public class SplashActivity extends AppCompatActivity {

    private TextView tvSplashVersionName;

    private static final String TAG = "SplashActivity";

    private final String CLOUD_UPDATE_ADD = Constant.UPDATE_INFO;

    private String code;
    private String apkUrl;
    private String des;

    private ImageView ivBackGround;

    private DownloadBinderListener mBinder;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (DownloadBinderListener) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Message msg;

    //定义一组常量用来区别Handler的操作
    public static final int MSG_SHOW_UPDATE = 0;
    public static final int MSG_NETWORK_ERROR = 1;
    public static final int MSG_SERVER_ERROR = 2;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_UPDATE:
                    showUpdateDialog();
                    break;
                case MSG_NETWORK_ERROR:
                    Toasty.error(getApplicationContext(), "网络连接异常！", Toast.LENGTH_SHORT, true).show();
                    enterHomeActivity();
                    break;
                case MSG_SERVER_ERROR:
                    Toasty.error(getApplicationContext(), "服务器异常！", Toast.LENGTH_SHORT, true).show();
                    enterHomeActivity();
                    break;
            }
        }
    };

    /**
     * 显示提示更新的对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("有新版本：" + code);
        builder.setCancelable(false);
        builder.setMessage(des);
        builder.setIcon(R.mipmap.ic_launcher);

        //升级按钮
        builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mBinder.startDownload(apkUrl);
                enterHomeActivity();
            }
        });

        //取消升级按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHomeActivity();
            }
        });

        builder.show();
    }

    private void bindDownloadService() {
        Intent intent = new Intent(SplashActivity.this, DownloadService.class);
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);//绑定服务，绑定成功会调用conn的onServiceConnected()方法
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //使用Glide加载Splash图片
        ivBackGround = (ImageView) findViewById(R.id.iv_splash_background);
        Glide.with(this).load(R.drawable.splash).into(ivBackGround);

        //设置显示版本号
//        tvSplashVersionName = (TextView) findViewById(R.id.tv_splash_version_name);
//        tvSplashVersionName.setText("版本号：" + getVersionName());

        //请求存储写权限
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        //检查更新
        checkUpdate(CLOUD_UPDATE_ADD);
        bindDownloadService();
    }

    /**
     * 检查更新
     *
     * @param updateAddress 获取更新信息的URL
     */
    private void checkUpdate(final String updateAddress) {
        msg = Message.obtain();
        new Thread(new Runnable() {
            @Override
            public void run() {
                long lastTime = System.currentTimeMillis();
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(updateAddress)
                            .build();
                    Response response = client.newCall(request).execute();
                    String updateInfo = response.body().string();
                    Log.d("checkUpdate", updateInfo);
                    Log.d("updateAdd", CLOUD_UPDATE_ADD);

                    handleUpdateInfo(updateInfo);   //处理获取的更新信息

                    if (code.equals(getVersionName())) {
                        //没有更新,直接进入主界面
                        enterHomeActivity();
                    } else {
                        //有更新，请求更新（UI），由于在子线程中，所以使用Handler发送消息
                        msg.what = MSG_SHOW_UPDATE;
                    }

                } catch (IOException e) {
                    //IO异常
                    msg.what = MSG_NETWORK_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    //JSON数据解析异常
                    msg.what = MSG_SERVER_ERROR;
                    e.printStackTrace();
                }
                long currentTime = System.currentTimeMillis();

                if ((currentTime - lastTime) < 2500) {
                    try {
                        Thread.sleep(2500 - (currentTime - lastTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 进入主界面
     */
    private void enterHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 处理服务器返回的Json更新文件
     *
     * @param updateInfo 从服务器上获取的Json更新信息文件
     */
    private void handleUpdateInfo(String updateInfo) throws JSONException {

        JSONObject obj = new JSONObject(updateInfo);

        code = obj.getString("code");
        apkUrl = obj.getString("apkurl");
        des = obj.getString("des");

        Log.d("code", code);
        Log.d("apkUrl", apkUrl);
        Log.d("des", des);
    }

    /**
     * 获取应用程序版本名
     *
     * @return 应用程序版本名
     */
    private String getVersionName() {
        //包的管理者，获取清单文件和build.gradle中的信息
        PackageManager pm = getPackageManager();

        try {
            //根据包名返回信息，其实是返回一个保存有清单文件和build.gradle的Javabean
            //packageName：应用程序包名 ：getPackageName()
            //flags : 指定信息的标签，0：获取基础信息，比如包名、版本号，要获取权限等信息，必须通过指定其他flags
            //GET_PERMISSIONS : 标签的含义：除获取基础信息之外，还会获取权限的信息
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SplashActivity.this, "拒绝权限将可能无法正常使用程序！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
