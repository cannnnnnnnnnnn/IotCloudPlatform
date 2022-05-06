package com.example.IotCloudPlatform.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private static final String DB_NAME = "smartfactory";
    private static final int DB_VERSION = 1;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table data(id integer primary key,"
                + "temperature text,"
                + "humidity text,"
                + "light text)");
        db.beginTransaction();
        try {
            db.execSQL(" create trigger trigger_delete_top "
                    + " AFTER insert on data"
                    + " BEGIN delete from data "
                    + " where (select count(id) from data) > 20 "
                    + " and id in (select id from data order by id asc "
                    + " limit (select count(id)-20 from data));"
                    + " END;");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private void read(Context context) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        db = dataBaseHelper.getReadableDatabase();
    }

    private void write(Context context) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        db = dataBaseHelper.getWritableDatabase();
    }

    public void insert(Context context, String temp, String hum, String light) {
        write(context);
        ContentValues cv = new ContentValues();
        cv.put("temperature", temp);
        cv.put("humidity", hum);
        cv.put("light", light);
        db.insert("data", null, cv);
        close();
    }

    public List<Float> search(Context context, String type) {
        read(context);
        List<Float> data = new ArrayList<Float>();
        Cursor c = db.rawQuery("select * from data order by id desc", null);
        while (c.moveToNext()) {
            float s = 0;
            switch (type) {
                case "温度":
                    s = Float.parseFloat(c.getString(1));
                    break;
                case "湿度":
                    s = Float.parseFloat(c.getString(2));
                    break;
                case "光照":
                    s = Float.parseFloat(c.getString(3));
                    break;
            }
            data.add(s);
        }
        close();
        return data;
    }

    public void close() {
        if (db != null)
            db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}