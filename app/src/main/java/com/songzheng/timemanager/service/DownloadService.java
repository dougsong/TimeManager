package com.songzheng.timemanager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.activities.HomeActivity;
import com.songzheng.timemanager.http.DownloadTask;
import com.songzheng.timemanager.listener.DownloadBinderListener;
import com.songzheng.timemanager.listener.DownloadListener;

import java.io.File;

public class DownloadService extends Service {

    private DownloadTask downloadTask;

    private String downloadUrl;

    private DownloadListener listener = new DownloadListener() {

        /**
         * 下载进度变化时回调
         * @param progress 下载进度
         */
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("下载中...", progress));
        }

        /**
         * 下载成功时回调
         */
        @Override
        public void onSuccess() {
            //下载成功时关闭前台服务通知，创建一个新的下载成功的通知
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载成功！", -1));
            Toast.makeText(DownloadService.this, "下载成功！", Toast.LENGTH_SHORT).show();
        }

        /**
         * 下载失败时回调
         */
        @Override
        public void onFailed() {
            //下载成功时关闭前台服务通知，创建一个新的下载失败的通知
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载失败！", -1));
            Toast.makeText(DownloadService.this, "下载失败！", Toast.LENGTH_SHORT).show();
        }

        /**
         * 下载暂停回调
         */
        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "下载暂停！", Toast.LENGTH_SHORT).show();
        }

        /**
         * 下载取消回调
         */
        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "下载取消！", Toast.LENGTH_SHORT).show();
        }
    };

    public DownloadService() {
    }

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class DownloadBinder extends Binder implements DownloadBinderListener{

        public void startDownload(String url) {
            if (downloadTask == null) {
                downloadUrl = url;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadUrl);
                startForeground(1, getNotification("下载中...", 0));
                Toast.makeText(DownloadService.this, "下载中...", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                //正在下载时选择了取消下载，之后回调onCanceled()，downloadTask被置为null
                downloadTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    //downloadTask为null，downloadUrl不为null，表示之前下载开始过,并且当前不处于下载状态
                    //取消下载，删除已经下载的文件
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "下载取消！", Toast.LENGTH_SHORT).show();
                } else {
                    //这里表示还没开始点击下载，就点击取消下载
                }
            }
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        //点击跳转到HomeActivity
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);

        if (progress > 0) {
            //当进度progress大于0显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }

        return builder.build();
    }
}
