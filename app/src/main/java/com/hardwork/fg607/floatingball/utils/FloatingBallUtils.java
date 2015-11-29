package com.hardwork.fg607.floatingball.utils;

/**
 * Created by fg607 on 15-8-20.
 *
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.View;

import com.hardwork.fg607.floatingball.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;


public class FloatingBallUtils {

    public static OutputStream os;
    public static Bitmap bitmap;

    /**
     * 获取MainActivity的SharedPreferences共享数据
     * @param context
     * @return
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        Context mainActivityContext = null;
        SharedPreferences sp = null;
        try {
            //创建上下文，指定宿主应用的包名以及 忽略系统的安全过滤，这点切勿忽视！
            mainActivityContext=context.createPackageContext("com.hardwork.fg607.floatingball", Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(mainActivityContext != null) {
             sp = mainActivityContext.getSharedPreferences("config",Context.MODE_MULTI_PROCESS);
        }

        return sp;


    }

    /**
     * 将状态数据保存在sharepreferences
     * @param name
     * @param state
     */
    public static void saveState(SharedPreferences sp,String name ,boolean state) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name, state);
        editor.commit();

    }

    public static void saveState(SharedPreferences sp,String name ,String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static void saveState(SharedPreferences sp,String name ,int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public static void saveState(SharedPreferences sp,String name,Set<String> value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(name, value);
        editor.commit();
    }

    /**
     * 以超级用户权限运行adb命令
     * @param cmd
     */
    public static void runCmd(String cmd) {

        try {
            if(os == null) {
                os = Runtime.getRuntime().exec("su").getOutputStream();
            }

            cmd = cmd +" "+"\n";
            os.write(cmd.getBytes());
            os.flush();

        } catch (IOException e) {

            e.printStackTrace();
        }

    }


    /**
     * 无延时模拟全局按键
     *
     * @param keyCode
     *            键值
     */
    public static void simulateKey(int keyCode) {

        //使用KeyEvent模拟按键按下与弹起
        long l = SystemClock.uptimeMillis();
        KeyEvent localKeyEvent = new KeyEvent(l,l,KeyEvent.ACTION_DOWN,keyCode,0);
        KeyEvent localKeyEvent1 = new KeyEvent(l,l,KeyEvent.ACTION_UP,keyCode,0);



        //判断sdk版本，老版本使用IWindowManager注入按键事件，新版本使用InputManager注入按键事件
        //*******IWindowManager和InputManager都是隐藏类，必须在重新生成sdk中的android.jar，并包含两个类及其依赖*****
        if (Build.VERSION.SDK_INT < 16) {
            try {

                IWindowManager.Stub.asInterface(ServiceManager.getService("window")).injectKeyEvent(localKeyEvent, true);
                IWindowManager.Stub.asInterface(ServiceManager.getService("window")).injectKeyEvent(localKeyEvent1, true);
                return;
            } catch (RemoteException e) {
                e.printStackTrace();
                return;
            }
        }

        InputManager.getInstance().injectInputEvent(localKeyEvent, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
        InputManager.getInstance().injectInputEvent(localKeyEvent1, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);

    }

    /**
     * 按下back
     */

    public static  void  keyBack(){

        simulateKey(KeyEvent.KEYCODE_BACK);
    }
    /**
     * 按下menu
     */

    public static void  keyMenu(){

      simulateKey(KeyEvent.KEYCODE_MENU);
    }
    /**
     * 按下HOME
     */

    public static void  keyHome(){

     simulateKey(KeyEvent.KEYCODE_HOME);
    }
    /**
     *按下电源键
     */

    public static void  pressPower(){


        simulateKey(KeyEvent.KEYCODE_POWER);
    }
    /**
     * 打开任务面板
     */

    public static void  openRecnetTask(){


        simulateKey(KeyEvent.KEYCODE_APP_SWITCH);
    }

    /**
     * 长按电源键
     */
    public static void longPressPower() {

    }

    /**
     * 音量上键
     */
    public static void volumeUp() {

      simulateKey(KeyEvent.KEYCODE_VOLUME_UP);

    }

    /**
     * 音量下键
     */
    public static void vloumeDown() {

        simulateKey(KeyEvent.KEYCODE_VOLUME_DOWN);

    }

    /**
     * 重启
     */
    public static void reboot() {

        runCmd("reboot");

    }

    /**
     * 关机
     */
    public static void shutdown() {

       runCmd("poweroff");

    }

    /**
     * 根据图标信息获取图标
     * @param iconName
     * @return
     */
    public static Bitmap getBitmap(String iconName) {
        Bitmap bitmapicon = null;
        switch (iconName) {
            case "nor":
                bitmapicon =   ((BitmapDrawable) (MyApplication.getAppContext().getResources().getDrawable(R.drawable.nor))).getBitmap();
                break;
            case "iphone":
                bitmapicon =   ((BitmapDrawable) (MyApplication.getAppContext().getResources().getDrawable(R.drawable.iphone))).getBitmap();
                break;
            case "superman":
                bitmapicon =   ((BitmapDrawable) (MyApplication.getAppContext().getResources().getDrawable(R.drawable.superman))).getBitmap();
                break;
            case "american":
                bitmapicon =   ((BitmapDrawable) (MyApplication.getAppContext().getResources().getDrawable(R.drawable.american))).getBitmap();
                break;
            case "clover":
                bitmapicon =   ((BitmapDrawable) (MyApplication.getAppContext().getResources().getDrawable(R.drawable.clover))).getBitmap();
                break;
            default:

                bitmapicon = scaleBitmap(iconName,100);
                break;

        }

        return bitmapicon;


    }

    /**
     * 保存图标到sd卡
     * @param bitmap
     * @param fileName
     * @throws IOException
     */
    public static String saveBitmap(Bitmap bitmap,String fileName) throws IOException {

        String rootdir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/hwfb";

        File dir = new File(rootdir);

        if (dir.isDirectory() && !dir.exists()) {
            dir.mkdir();
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        InputStream isBm = new ByteArrayInputStream(baos.toByteArray());

        File file = new File(rootdir+"/"+fileName);

        if(!file.exists()) {
            file.createNewFile();
        }

        byte[] buffer = new byte[1024];

        OutputStream os = new FileOutputStream(file);

        int count = 0;
        while ((count = isBm.read(buffer) ) != -1) {
            os.write(buffer,0,count);
        }

        os.flush();
        os.close();
        isBm.close();

        return file.getAbsolutePath();
    }

    /**
     * 缩放图标
     * @param filename
     * @param size
     * @return
     */
    public static Bitmap scaleBitmap(String filename,float size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filename,options);

        int be = (int)(options.outHeight / (float)size);

        if (be <= 0) {
            be = 1;
        }

        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inSampleSize = be;
        options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options1.inPurgeable = true;
        options1.inInputShareable = true;
        options1.inJustDecodeBounds = false;
        Bitmap outputbitmap = BitmapFactory.decodeFile(filename,options1);

        return outputbitmap;
    }

    /**
     * 缩放图标
     * @param bitmap
     * @param size
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap,float size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        InputStream isBm = null;
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        isBm = new ByteArrayInputStream(baos.toByteArray());

        BitmapFactory.decodeStream(isBm, null, options);


        if(isBm != null) {
        try {
            isBm.close();
        } catch (IOException e) {
            e.printStackTrace();
           }
        }

        int be = (int)(options.outHeight / (float)size);

        if (be <= 0) {
            be = 1;
        }

        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inSampleSize = be;
        options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options1.inPurgeable = true;
        options1.inInputShareable = true;
        options1.inJustDecodeBounds = false;

        isBm = new ByteArrayInputStream(baos.toByteArray());

        Bitmap outputbitmap = BitmapFactory.decodeStream(isBm, null, options1);

        if(isBm != null) {
            try {
                isBm.close();
                isBm = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outputbitmap;
    }


    /**
     * 显示对话框
     * @param context
     * @param title
     * @param view
     * @return
     */
    public static AlertDialog showDlg(Context context,String title,View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title);
        builder.setView(view);
        AlertDialog Dialog =  builder.show();

        return Dialog;
    }
}

