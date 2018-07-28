package com.songzheng.timemanager.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.entity.TaskGroup;
import com.songzheng.timemanager.utils.UUIDUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddListGroupActivity extends AppCompatActivity {

    @BindView(R.id.tv_add_list_group_complete)
    protected TextView tvAddListGroupComplete;

    @BindView(R.id.tv_add_list_group_cancel)
    protected TextView tvAddListGroupCancel;

    @BindView(R.id.et_new_list_group_name)
    protected EditText etNewListGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_group);
        ButterKnife.bind(this);

        tvAddListGroupComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击完成按钮
                String groupTitle = etNewListGroupName.getText().toString();
                addGroupToLocalDB(groupTitle);
                finish();
            }
        });

        tvAddListGroupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击取消按钮
                finish();
            }
        });
    }

    private void addGroupToLocalDB(String groupTitle) {
        TaskGroup group = new TaskGroup();

        group.setGroup_title(groupTitle);
        group.setGid(UUIDUtils.getId());
        group.setCount(0);
        // TODO:可以为了保证不登录使用，在这里不添加user_id，而是在上传服务器时添加

        group.save();
    }
}
