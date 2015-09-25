package com.hardwork.fg607.floatingball.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by fg607 on 15-9-5.
 */
public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
