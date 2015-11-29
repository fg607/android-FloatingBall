package com.hardwork.fg607.floatingball.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.hardwork.fg607.floatingball.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fg607 on 15-11-26.
 */
public class AppUtils {

    public static ArrayList<AppInfo> getAppInfos(Context context){

        ArrayList<AppInfo> list = new ArrayList<>();

        Drawable icon;
        String name;
        String packageName;

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);

        for(PackageInfo info:packageInfos){

            //判断是否为用户应用
            if((ApplicationInfo.FLAG_SYSTEM & info.applicationInfo.flags) == 0){

                icon = info.applicationInfo.loadIcon(pm);
                name = info.applicationInfo.loadLabel(pm).toString();
                packageName = info.packageName;

                AppInfo appInfo = new AppInfo(icon,name,packageName);

                list.add(appInfo);
            }


        }

        return list;
    }

    public static void startApplication(Context context,String packageName){

        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);


    }
}
