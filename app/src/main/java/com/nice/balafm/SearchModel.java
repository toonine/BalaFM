package com.nice.balafm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TabHost;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by 23721 on 2017/7/30.
 */

public class SearchModel extends SQLiteOpenHelper{
    private static final String TAG="SearchModel";
    private static SQLiteDatabase database;
    public static final String CREATE_SEARCH="create table search(" +
            "id integer primary key autoincrement," +
            "word text," +
            "user text," +
            "time data)";
    public static Context m_contect;

    private static int user;
    private static final int HistoryNumLimit=5;

    public SearchModel(Context context, String name,SQLiteDatabase.CursorFactory factory,int version)
    {
        super(context, name, factory, version);
        m_contect=context;
        database=getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SEARCH);
        Log.d(TAG, "onCreate: create table search");
        user=AppKt.getGlobalUid();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists search");
        onCreate(db);
    }
    public void addRecord(String searchStr){
        ContentValues values=new ContentValues();
        values.put("user",user);
        values.put("word",searchStr);
        values.put("time",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(Calendar.getInstance().getTime()));
        database.insert("search",null,values);
    }
    public ArrayList<String> getHistory(){
        user=AppKt.getGlobalUid();
        String colsname[]={"word"};
        String whereArg[]={String.valueOf(user)};
        Cursor cursor=database.rawQuery("select word from search where user='"+user+"' order by time desc",null);
        ArrayList<String> result=new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                String word=cursor.getString(cursor.getColumnIndex("word"));
                result.add(word);
            }while(cursor.moveToNext());
        }
        printResult(result);
        return result;
    }
    public void removeRecord(String record){
        database.delete("search","user=? and word=?",new String[]{String.valueOf(user),record});
    }
    public void printAll()
    {
        Cursor cursor=database.query("search",null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            Log.d(TAG,"table search:\n");
            Log.d(TAG,String.format("%10s%10s%10s%20s\n","id","user","word","time"));
            do{
                String id=cursor.getString(cursor.getColumnIndex("id"));
                String user=cursor.getString(cursor.getColumnIndex("user"));
                String word=cursor.getString(cursor.getColumnIndex("word"));
                String time=cursor.getString(cursor.getColumnIndex("time"));

                Log.d(TAG,String.format("%10s%10s%10s%20s\n",id,user,word,time));
            }while(cursor.moveToNext());
        }
    }
    public void printResult(ArrayList<String> result)
    {
        for (String h:result)
        {
            Log.d(TAG,h);
        }
    }
}
