package com.songzheng.timemanager.listener;

/**
 * Created by make on 2017/3/25.
 */

/**
 * 下载任务的回调接口
 */
public interface DownloadListener {

    /**
     * 进度更新
     * @param progress 下载进度
     */
    void onProgress(int progress);

    /**
     * 下载成功回调
     */
    void onSuccess();

    /**
     * 下载失败
     */
    void onFailed();

    /**
     * 下载暂停
     */
    void onPaused();

    /**
     * 下载取消
     */
    void onCanceled();
}
