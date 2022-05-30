package com.example.IotCloudPlatform.tools;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LoginDataBaseHelper extends SQLiteOpenHelper {

    // activity句柄，数据库名，空，数据库版本
    public LoginDataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建存登录用户表
        db.execSQL("create table users(account text,password text,phone text,email text,sex text,login text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}