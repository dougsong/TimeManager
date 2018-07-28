package com.songzheng.timemanager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.entity.Note;
import com.songzheng.timemanager.service.impl.UserServiceImpl;
import com.songzheng.timemanager.utils.UUIDUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditNoteActivity extends AppCompatActivity {

    @BindView(R.id.tv_edit_note_complete)
    protected TextView tvEditNoteComplete;

    @BindView(R.id.et_edit_note_title)
    protected EditText etNoteTitle;

    @BindView(R.id.et_edit_note_content)
    protected EditText etNoteContent;

    @BindView(R.id.tv_edit_note_cancel)
    protected TextView tvEditNoteCancle;

    //判断此次是新建笔记还是修改笔记
    private String modifyId;

    private UserServiceImpl userService = new UserServiceImpl(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);

        modifyId = null;

        //如果从现有条目点进来的，要把标题和内容放进去
        setData();

        tvEditNoteComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etNoteTitle.getText().toString();
                String content = etNoteContent.getText().toString();
                if (modifyId != null) {
                    //修改笔记
                    Log.d("EditNoteActivity", "Modify");
                    updateToLocalDB(title, content, modifyId);
                } else {
                    //新建笔记
                    Log.d("EditNoteActivity", "Add");
                    addNoteToLocalDB(title, content);
                }
                finish();
            }
        });

        tvEditNoteCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateToLocalDB(String title, String content, String id) {
        Note note = new Note();

        note.setNote_title(title);
        note.setNote_content(content);
        note.setNid(id);
        note.setNote_lastModifiedTime(getCurrentTime());
        // 由于创建的时候已经有user_id了，所以这里更新的时候数据库中的note都是有user_id的，这里就不用添加user_id了

        note.updateAll("nid = ?", id);
    }

    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        Date currentDate = new Date(System.currentTimeMillis());
        return formatter.format(currentDate);
    }

    private void setData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        modifyId = intent.getStringExtra("id");

        if (title != null && content != null && modifyId != null) {
            etNoteTitle.setText(title);
            etNoteContent.setText(content);
        }

    }

    private void addNoteToLocalDB(String title, String content) {
        if ("".equals(title) && "".equals(content)) {
            return;
        }
        Note note = new Note();

        note.setNote_title(title);
        note.setNote_content(content);
        note.setNid(UUIDUtils.getId());
        note.setNote_lastModifiedTime(getCurrentTime());
        // TODO:这里可以不添加user_id属性，保证没有登录也可以使用笔记功能，可以在上传服务器的时候要求用户登录，并且添加user_id
//        note.setUser_id(userService.getUserInfo().getUid());

        note.save();
    }

}
