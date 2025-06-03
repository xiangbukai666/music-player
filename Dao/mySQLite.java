package com.example.mymusic.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mymusic.Bean.User;

import java.util.ArrayList;

public class mySQLite extends SQLiteOpenHelper {
    private SQLiteDatabase db;//创建数据库对象19220337
    public mySQLite(Context context) {
        super(context,"MyDB",null,3);//初始化数据库
        db=getReadableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS user(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE," +
                "password TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }
    public void add(String name,String password ){
        db.execSQL("INSERT INTO user(name,password)VALUES(?,?)",new Object[]{name,password});
    }
    public void deleteUser(String username){
        db=getWritableDatabase();
    db.delete("user","name=?",new String[]{username});
    }
    public ArrayList<User> getAllDATA(){
        ArrayList<User> list = new ArrayList<User>();
        Cursor cursor=db.query("user",null,null,null,null,null,"id ASC");
        while (cursor.moveToNext()) {
            int nameIndex=cursor.getColumnIndex("name");
            int passwordIndex=cursor.getColumnIndex("password");
            if (nameIndex==-1||passwordIndex==-1) {
                //处理列索引无效的情况
                Log.e("DatabaseQuery", "Column name or password not found.");
                continue;
            }
            String name=cursor.getString(nameIndex);
            String password=cursor.getString(passwordIndex);
            list.add(new User(name, password));
        }
        return list;
    }
}

