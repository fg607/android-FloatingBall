package com.hardwork.fg607.floatingball.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by fg607 on 15-8-27.
 */
public class BallFunctionDao {

    private MySQLiteOpenHelper mySQLiteOpenHelper;


    public BallFunctionDao(Context context) {
        mySQLiteOpenHelper = new MySQLiteOpenHelper(context);

    }

    /**
     * 增加场景
     * @param name
     * @param click
     * @param doubleclick
     * @param flipup
     * @param flipdown
     * @param flipleft
     * @param flipright
     */
    public void addFuncs(String name,String click,String doubleclick,String flipup,String flipdown,
                    String flipleft,String flipright) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("insert into functions (name,click,doubleclick,flipup,flipdown," +
                        "flipleft,flipright) values (?,?,?,?,?,?,?)",new Object[]{name,click,doubleclick,
                flipup,flipdown,flipleft,flipright});
        database.close();

    }

    public void addFuncsDefault(String name) {
       addFuncs(name, "返回键", "菜单键", "菜单键", "Home键", "最近任务键", "电源键");

    }

    /**
     * 删除
     * @param name
     */
    public void deleteFuncs(String name) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("delete from functions where name = ?",new Object[]{name});
        database.close();

    }

    /**
     * 更新
     * @param name
     * @param click
     * @param doubleclick
     * @param flipup
     * @param flipdown
     * @param flipleft
     * @param flipright
     */
    public void updateFuncs(String name,String click,String doubleclick,String flipup,String flipdown,
                    String flipleft,String flipright) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("update functions set click = ?,doubleclick = ?,flipup = ?," +
                "flipdown = ?,flipleft = ?,flipright = ? where name = ?",
                new Object[]{click,doubleclick, flipup,flipdown,flipleft,flipright,name});
        database.close();

    }

    public void updateClickFuncs(String name,String click) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("update functions set click = ?  where name = ?",
                new Object[]{click,name});
        database.close();

    }

    public void updateDoubleClickFuncs(String name,String doubleclick) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("update functions set doubleclick = ?  where name = ?",
                new Object[]{doubleclick,name});
        database.close();

    }

    public void updateFlipUpFuncs(String name,String flipup) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("update functions set flipup = ?  where name = ?",
                new Object[]{flipup,name});
        database.close();

    }

    public void updateFlipDownFuncs(String name,String filpdown) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("update functions set flipdown = ?  where name = ?",
                new Object[]{filpdown,name});
        database.close();

    }

    public void updateFlipLeftFuncs(String name,String flipleft) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("update functions set flipleft = ?  where name = ?",
                new Object[]{flipleft,name});
        database.close();

    }
    public void updateFlipRightFuncs(String name,String flipright) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("update functions set flipright = ?  where name = ?",
                new Object[]{flipright, name});
        database.close();
    }

    /**
     * 查询
     * @param name
     * @return
     */
    public ArrayList<String> findFuncs(String name) {
        SQLiteDatabase database = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from functions where name = ?", new String[]{name});
        ArrayList<String> arrayList = new ArrayList<String>();

        while (cursor.moveToNext()) {
            //返回clic....filpright
            arrayList.add(cursor.getString(2));
            arrayList.add(cursor.getString(3));
            arrayList.add(cursor.getString(4));
            arrayList.add(cursor.getString(5));
            arrayList.add(cursor.getString(6));
            arrayList.add(cursor.getString(7));

        }
        cursor.close();
        database.close();

        return arrayList;
    }

    public ArrayList<String> findFuncsAllName() {
        SQLiteDatabase database = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from functions", null);
        ArrayList<String> arrayList = new ArrayList<String>();

        while (cursor.moveToNext()) {
            //返回name
            arrayList.add(cursor.getString(1));
        }
        cursor.close();
        database.close();

        return arrayList;
    }

    /**
     * 增加功能键
     * @param name
     * @param icon
     * @param title
     * @param scene
     */
    public void addFuncKey(String name,String icon,String title,String scene) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("insert into funckey (name,icon,title,scene) " +
                "values (?,?,?,?)", new Object[]{name, icon, title, scene});
        database.close();

    }

    /**
     * 删除功能键
     * @param name
     */

    public void deleteFuncKey(String name) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("delete from funckey where name = ?",new Object[]{name});
        database.close();

    }

    /**
     * 更新功能键
     * @param name
     * @param icon
     * @param title
     * @param scene
     */
    public void updateFuncKey(String name,String icon,String title,String scene) {
        SQLiteDatabase database = mySQLiteOpenHelper.getWritableDatabase();
        database.execSQL("update funckey set icon = ?,title = ?,scene = ?  where name = ?",
                new Object[]{icon,title, scene,name});
        database.close();
    }

    /**
     * 查找功能键内容
     * @param name
     * @return
     */
    public ArrayList<String> findFuncKey(String name) {
        SQLiteDatabase database = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from funckey where name = ?", new String[]{name});
        ArrayList<String> arrayList = new ArrayList<String>();

        while (cursor.moveToNext()) {
            arrayList.add(cursor.getString(2));
            arrayList.add(cursor.getString(3));
            arrayList.add(cursor.getString(4));
        }
        cursor.close();
        database.close();

        return arrayList;

    }

    public ArrayList<String> findAllKeyName() {
        SQLiteDatabase database = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from funckey", null);
        ArrayList<String> arrayList = new ArrayList<String>();

        while (cursor.moveToNext()) {
            //返回name
            arrayList.add(cursor.getString(1));
        }
        cursor.close();
        database.close();

        return arrayList;
    }
}
