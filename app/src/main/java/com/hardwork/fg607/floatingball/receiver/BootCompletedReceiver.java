package com.hardwork.fg607.floatingball.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hardwork.fg607.floatingball.service.FloatingBallService;
import com.hardwork.fg607.floatingball.utils.FloatingBallUtils;

/**
 * Created by fg607 on 15-8-23.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    private SharedPreferences sp;

    public void onReceive(Context context, Intent intent) {


        sp = FloatingBallUtils.getSharedPreferences(context);


        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && sp.getBoolean("autostart",false) && sp.getBoolean("ballstate",false)){
            Intent newIntent = new Intent(context,FloatingBallService.class);
            newIntent.putExtra("ballstate","showball");
            context.startService(newIntent);
        }
    }
}