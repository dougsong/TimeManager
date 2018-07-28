package com.songzheng.timemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songzheng.timemanager.R;
import com.songzheng.timemanager.entity.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder>{

    /**
     * Created by make on 2017/4/22.
     */
    private List<Note> notes;

    //用来使用Glide加载图片、跳转界面
    private Context mContext;

    public NoteAdapter(List<Note> mNotes) {
        this.notes = mNotes;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.note_fragment_item, parent, false);
        final NoteViewHolder holder = new NoteViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.tvModifiedTime.setText(note.getNote_lastModifiedTime());
        holder.tvTitle.setText(note.getNote_title());
        holder.tvContent.setText(note.getNote_content());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void removeItem(int position) {
        notes.remove(position);
        notifyDataSetChanged();
    }
}
