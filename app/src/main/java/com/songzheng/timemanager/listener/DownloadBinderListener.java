package com.songzheng.timemanager.listener;

/**
 * Created by make on 2017/4/3.
 */

public interface DownloadBinderListener {

    void startDownload(String url);

    void pauseDownload();

    void cancelDownload();
}
