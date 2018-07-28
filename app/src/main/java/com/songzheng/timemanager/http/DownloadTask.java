package com.songzheng.timemanager.http;

import android.os.AsyncTask;
import android.os.Environment;

import com.songzheng.timemanager.listener.DownloadListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by make on 2017/3/25.
 */


/**
 * AsyncTask<String, Integer, Integer>
 *     第一个参数表示要传入的参数，保存在doInBackground(String... params)方法的params[0],传入下载URL
 *     第二个参数表示进度条的单位，即onProgressUpdate(Integer... progress)方法的参数
 *     第三个参数表示返回执行结果的类型，即doInBackground(String... params)方法的返回值类型，
 *     并且还会作为onPostExecute(Integer integer)方法的参数类型
 *
 *
 *      通过AsyncTask实现断点续传下载操作，通过new DownloadTask().execute(url)开始下载操作
 */
public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private boolean isCanceled = false;
    private boolean isPaused = false;

    private int lastProgress;

    private DownloadListener listener;

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    /**
     * 执行下载操作（断点续传）
     * @param params params[0]:传入的下载的URL地址
     * @return 返回下载结果作为onPostExecute()方法的参数
     */
    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;

        try {
            long downloadedLength = 0;  //已经下载的文件长度
            String downloadUrl = params[0];     //获取下载URL
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

            file = new File(directory + fileName);
            if (file.exists()) {
                //文件存在，说明此次下载是续传
                downloadedLength = file.length();
            }

            long contentLength = getContentLength(downloadUrl); //获取文件总字节数

            if (contentLength == 0) {
                //获取的文件总字节数为0，说明有错误
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength) {
                //下载已经完成
                return TYPE_SUCCESS;
            }

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();

            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength);   //跳过已经下载的字节
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    if (isCanceled) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        int progress = (int)((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled && file != null) {
                    //取消下载并且还没下载完，删除已下载的部分
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return TYPE_FAILED;
    }


    /**
     * 更新下载进度
     * @param values values[0]:下载的进度，在doInBackground()方法中
     *               调用publishProgress(int progress)方法之后会调用该方法
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }


    /**
     * 下载完成后调用该方法
     * @param status 下载结果状态
     */
    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
        }
    }


    /**
     * 获取下载的文件的长度
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }
}
