package com.songzheng.timemanager.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.activities.AddListGroupActivity;
import com.songzheng.timemanager.activities.ListGroupActivity;
import com.songzheng.timemanager.adapter.ListGroupAdapter;
import com.songzheng.timemanager.entity.TaskGroup;
import com.songzheng.timemanager.entity.Task;
import com.songzheng.timemanager.listener.OnItemClickListener;
import com.songzheng.timemanager.listener.OnSyncTasksListener;
import com.songzheng.timemanager.service.UserService;
import com.songzheng.timemanager.service.impl.UserServiceImpl;
import com.songzheng.timemanager.ui.ItemRemoveRecyclerView;

import org.litepal.crud.DataSupport;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class ListFragment extends Fragment {

    private List<TaskGroup> groups;

    private SwipeRefreshLayout refreshList;
    private FloatingActionButton fabAddListGroup;
    private ItemRemoveRecyclerView rvListGroups;

    private UserService userService;

    private Context mContext;

    private OnSyncTasksListener listener = new SyncTaskListener();

    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mContext = container.getContext();
        refreshList = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_list_group);
        fabAddListGroup = (FloatingActionButton) view.findViewById(R.id.fab_add_list_group);
        rvListGroups = (ItemRemoveRecyclerView) view.findViewById(R.id.rv_list_group_fragment);

        userService = new UserServiceImpl(mContext);

        loadListGroup();
        rvListGroups.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //点击进入ListGroupActivity
                enterLists(position);
            }

            @Override
            public void onDeleteClick(int position) {
                deleteListGroup(position);
                loadListGroup();
            }
        });

        refreshList.setColorSchemeResources(R.color.colorPrimary);
        refreshList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO：从服务器获取清单信息
//                refreshListFromCloud();
                userService.setOnSyncTasksListener(listener);
                userService.syncTasks();
            }
        });

        fabAddListGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addListGroup();
            }
        });

        return view;
    }

    private void enterLists(int position) {
        TaskGroup group = groups.get(position);
        Intent intent = new Intent(mContext, ListGroupActivity.class);
        intent.putExtra("group", group);
        mContext.startActivity(intent);
    }

    private void addListGroup() {
        Intent intent = new Intent(mContext, AddListGroupActivity.class);
        mContext.startActivity(intent);
    }

    private void deleteListGroup(int position) {
        TaskGroup group = groups.get(position);
        // TODO:删除一个group，要删除该group下的所有Tasks，因此这里要有一个提醒
        DataSupport.deleteAll(Task.class, "task_group_id = ?", group.getGid());
        DataSupport.deleteAll(TaskGroup.class, "gid = ?", group.getGid());
    }

    private void loadListGroup() {
        //从数据库获取list
        groups = DataSupport.findAll(TaskGroup.class);

        ListGroupAdapter adapter = new ListGroupAdapter(groups);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);

        rvListGroups.setLayoutManager(manager);
        rvListGroups.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadListGroup();
        Log.d("ListFragment", "onStart");
    }

    class SyncTaskListener implements OnSyncTasksListener {

        @Override
        public void onSyncTasksSuccess() {
            refreshList.setRefreshing(false);
            // TODO:重新从本地数据库加载任务信息
        }

        @Override
        public void onSyncTasksFailed(int msg) {
            switch (msg) {
                case UserServiceImpl.MSG_SYNC_TASK_FAILED:
                    Toasty.error(mContext, "同步失败！", Toast.LENGTH_SHORT, true).show();
                    refreshList.setRefreshing(false);
                    break;
            }
        }
    }
}
