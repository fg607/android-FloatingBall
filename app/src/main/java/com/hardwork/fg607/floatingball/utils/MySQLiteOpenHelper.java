package com.hardwork.fg607.floatingball.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fg607 on 15-8-27.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public MySQLiteOpenHelper(Context context) {
        super(context, "dbconfig", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table functions (_id integer primary key autoincrement," +
                "name varchar(20),click varchar(20),doubleclick varchar(20),flipup varchar(20)," +
                "flipdown varchar(20),flipleft varchar(20),flipright varchar(20))");

        sqLiteDatabase.execSQL("create table funckey (_id integer primary key autoincrement," +
                "name varchar(20),icon varchar(20),title varchar(20),scene varchar(20))");

        sqLiteDatabase.execSQL("create table appkey (_id integer primary key autoincrement," +
                "menu varchar(20),name varchar(20),icon varchar(20),package varchar(30))");

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
