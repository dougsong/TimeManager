package com.songzheng.timemanager.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.songzheng.timemanager.activities.EditNoteActivity;
import com.songzheng.timemanager.adapter.NoteAdapter;
import com.songzheng.timemanager.entity.Note;
import com.songzheng.timemanager.entity.User;
import com.songzheng.timemanager.listener.OnItemClickListener;
import com.songzheng.timemanager.listener.OnSyncNoteListener;
import com.songzheng.timemanager.service.UserService;
import com.songzheng.timemanager.service.impl.UserServiceImpl;
import com.songzheng.timemanager.ui.ItemRemoveRecyclerView;

import org.litepal.crud.DataSupport;

import java.util.List;

import es.dmoral.toasty.Toasty;


public class NoteFragment extends Fragment {

    private List<Note> notes;

    private FloatingActionButton fabAddNote;
    private ItemRemoveRecyclerView rvNotes;
    private SwipeRefreshLayout refreshNote;

    private Context mContext;

    private UserService userService;

    private OnSyncNoteListener listener = new SyncNoteListener();

    public static final int ADD_NOTE = 1;

    public static final int REFRESH_RECYCLERVIEW = 0;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_RECYCLERVIEW:
                    refreshNote.setRefreshing(false);
                    break;
                default:
            }
        }
    };

    public NoteFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        mContext = container.getContext();
        rvNotes = (ItemRemoveRecyclerView) view.findViewById(R.id.rv_note_fragment);
        fabAddNote = (FloatingActionButton) view.findViewById(R.id.fab_add_note);
        refreshNote = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_note);

        userService = new UserServiceImpl(mContext);

        // TODO:从本地数据库获取笔记信息，并加载出来
        loadNotes();
        rvNotes.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                editNote(position);
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });

        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });

        refreshNote.setColorSchemeResources(R.color.colorPrimary);
        refreshNote.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO：从服务器获取笔记信息
//                refreshNoteFromCloud();
                userService.setOnSyncNoteListener(listener);
                userService.syncNote();

            }
        });

        return view;
    }

    private void removeItem(int position) {
        Note note = notes.get(position);
        DataSupport.deleteAll(Note.class, "nid = ?", note.getNid());
        loadNotes();
    }

    private void editNote(int position) {
        Note note = notes.get(position);
        Intent intent = new Intent(mContext, EditNoteActivity.class);
        intent.putExtra("title", note.getNote_title());
        intent.putExtra("content", note.getNote_content());
        intent.putExtra("id", note.getNid());
        mContext.startActivity(intent);
    }

    private void loadNotes() {
        // TODO:从本地数据库中获取笔记信息
        notes = DataSupport.findAll(Note.class);

        NoteAdapter adapter = new NoteAdapter(notes);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        rvNotes.setLayoutManager(manager);
        rvNotes.setAdapter(adapter);
    }

    private void addNote() {
        Intent intent = new Intent(mContext, EditNoteActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadNotes();
        Log.d("NoteFragment", "onStart");
    }

    class SyncNoteListener implements OnSyncNoteListener {

        @Override
        public void onSyncNoteSuccess() {
            refreshNote.setRefreshing(false);
            Toasty.success(mContext, "同步笔记成功!", Toast.LENGTH_SHORT, true).show();
            // TODO:重新从本地数据库加载笔记信息
            loadNotes();
        }

        @Override
        public void onSyncNoteFailed(int msg) {
            switch (msg) {
                case UserServiceImpl.MSG_SYNC_NOTE_FAILED:
                    Toasty.error(mContext, "同步笔记失败！", Toast.LENGTH_SHORT, true).show();
                    refreshNote.setRefreshing(false);
                    break;

                case UserServiceImpl.MSG_SYNC_NOTE_NOT_LOGIN:
                    Toasty.warning(mContext, "您还没有登录!", Toast.LENGTH_SHORT, true).show();
                    refreshNote.setRefreshing(false);
                    break;

                default:
                    break;
            }
        }
    }
}
