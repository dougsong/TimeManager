package com.songzheng.timemanager.activities;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.adapter.TasksAdapter;
import com.songzheng.timemanager.entity.TaskGroup;
import com.songzheng.timemanager.entity.Task;
import com.songzheng.timemanager.listener.OnItemClickListener;
import com.songzheng.timemanager.ui.ItemRemoveRecyclerView;
import com.songzheng.timemanager.utils.UUIDUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 点击ListGroup，显示该清单组中所有List的Activity
 */
public class ListGroupActivity extends AppCompatActivity {

    private TaskGroup currentGroup;

    //保存当前ListGroup中的所有任务
    private List<Task> tasks;

    @BindView(R.id.tv_lists_back)
    protected TextView tvListsBack;

    @BindView(R.id.tv_list_group_name)
    protected TextView tvListGroupName;

    @BindView(R.id.tv_list_group_edit)
    protected TextView tvListGroupEdit;

    @BindView(R.id.et_new_task_content)
    protected EditText etTaskContent;

    @BindView(R.id.cb_task_edit_star)
    protected CheckBox cbTaskStar;

    @BindView(R.id.swipe_refresh_list)
    protected SwipeRefreshLayout swipeRefreshTasks;

    @BindView(R.id.rv_lists)
    protected ItemRemoveRecyclerView rvTasks;

    public static final int REFRESH_TASKS = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_TASKS:
                    swipeRefreshTasks.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_group);
        ButterKnife.bind(this);

        currentGroup = getCurrentListGroup();

        //从本地数据库加载任务
        loadTasks();

        tvListGroupName.setText(currentGroup.getGroup_title());

        tvListsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etTaskContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    addTaskToLocalDB();
                    loadTasks();
                    return true;
                }
                return false;
            }
        });

        swipeRefreshTasks.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshTasks.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO：从服务器获取清单信息
                refreshTasksFromCloud();
            }
        });

        rvTasks.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //进入ListActivity(任务界面)
            }

            @Override
            public void onDeleteClick(int position) {
                //从本地数据库中删除选中任务
                deleteTask(position);
                loadTasks();
            }
        });
    }

    private void deleteTask(int position) {
        Task task = tasks.get(position);
        DataSupport.deleteAll(Task.class, "tid = ?", task.getTid());

        changeTaskCount(-1);
    }

    private void loadTasks() {
        tasks = DataSupport.where("task_group_id = ?", currentGroup.getGid()).find(Task.class);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        TasksAdapter adapter = new TasksAdapter(tasks);

        rvTasks.setLayoutManager(manager);
        rvTasks.setAdapter(adapter);
    }

    private void refreshTasksFromCloud() {
        final Message msg = Message.obtain();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    msg.what = REFRESH_TASKS;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void addTaskToLocalDB() {
        // 添加一个Task到数据库，记得增加所在Group的条目数
        String content = etTaskContent.getText().toString();
        etTaskContent.setText("");

        if ("".equals(content)) {
            return;
        }

        boolean isStar = cbTaskStar.isChecked();

        Task task = new Task();

        task.setTid(UUIDUtils.getId());
        task.setTask_group_id(currentGroup.getGid());
        task.setTask_content(content);
        task.setComplete(false);
        task.setStar(isStar);
        task.save();

        changeTaskCount(1);
    }

    private TaskGroup getCurrentListGroup() {
        Intent intent = getIntent();
        TaskGroup group = (TaskGroup) intent.getSerializableExtra("group");
        return group;
    }

    private void changeTaskCount(int cnt) {
        currentGroup.setCount(currentGroup.getCount() + cnt);
        ContentValues values = new ContentValues();
        values.put("count", currentGroup.getCount());
        DataSupport.updateAll(TaskGroup.class, values, "gid = ?", currentGroup.getGid());
    }
}
