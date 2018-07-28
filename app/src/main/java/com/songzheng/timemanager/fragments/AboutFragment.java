package com.songzheng.timemanager.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.songzheng.timemanager.R;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AboutFragment extends Fragment {

    public static final String SERVER_FEEDBACK_URL = "http://192.168.56.1:8080/TimeManager/feedback";

    public static final int FEEDBACK_ERROR = 1;

    private EditText etFeedback;
    private Button btFeedback;

    private Context mContext;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FEEDBACK_ERROR:
                    Toasty.error(mContext, "反馈失败！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public AboutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        if (mContext == null) {
            mContext = container.getContext();
        }

        etFeedback = (EditText) view.findViewById(R.id.et_feedback);
        btFeedback = (Button) view.findViewById(R.id.bt_feedback);

        btFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedBack();
            }
        });

        return view;
    }

    private void sendFeedBack() {
        String feedback = etFeedback.getText().toString();
        if ("".equals(feedback) || feedback == null) {
            Toasty.warning(mContext, "反馈内容不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            sendFeedBackToServer(feedback);
        }
    }

    private void sendFeedBackToServer(final String feedback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();

                OkHttpClient client = new OkHttpClient();

                RequestBody body = new FormBody.Builder()
                        .add("feedback", feedback)
                        .build();

                Request request = new Request.Builder()
                        .url(SERVER_FEEDBACK_URL)
                        .post(body)
                        .build();

                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = FEEDBACK_ERROR;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

}
