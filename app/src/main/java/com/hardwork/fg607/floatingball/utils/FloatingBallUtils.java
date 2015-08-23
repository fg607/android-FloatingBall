package com.hardwork.fg607.floatingball.utils;

/**
 * Created by fg607 on 15-8-20.
 *
 */

import android.hardware.input.InputManager;
import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.view.IWindowManager;
import android.view.KeyEvent;


public class FloatingBallUtils {


    /**
     * 模拟全局按键
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
        if (Build.VERSION.SDK_INT < 16)
        {
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
}
