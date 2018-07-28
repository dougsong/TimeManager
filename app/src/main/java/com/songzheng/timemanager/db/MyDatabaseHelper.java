package com.songzheng.timemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by make on 2017/4/29.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper{

    public static final String CREATE_NOTE = "create table note("
            + "id integer primary key autoincrement, "
            + "title text, "
            + "content text, "
            + "lastModified text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
