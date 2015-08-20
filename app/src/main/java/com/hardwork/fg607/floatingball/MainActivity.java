package com.hardwork.fg607.floatingball;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.hardwork.fg607.floatingball.service.FloatingBallService;


public class MainActivity extends ActionBarActivity {

    private Switch aSwitchStart,aSwitchMove ;
    public static SharedPreferences sp;
    private boolean serviceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("config",MODE_PRIVATE);
        aSwitchStart = (Switch) findViewById(R.id.switch_start);
        aSwitchMove = (Switch) findViewById(R.id.switch_move);

        if(sp.getBoolean("ballstate",false))
        {
            aSwitchStart.setChecked(true);

        }
        else
        {
            aSwitchStart.setChecked(false);
        }

        aSwitchStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    postMsg("ballstate", "showball");
                    saveStates("ballstate", true);
                    aSwitchMove.setEnabled(true);
                    if (!serviceState)
                        openService();


                } else {
                    postMsg("ballstate", "closeball");
                    saveStates("ballstate", false);
                    aSwitchMove.setChecked(false);
                    aSwitchMove.setEnabled(false);
                    if(serviceState)
                        exitService();
                }
            }
        });

        if(sp.getBoolean("movestate",false))
        {
            aSwitchMove.setChecked(true);

        }
        else
        {
            aSwitchMove.setChecked(false);
        }
        aSwitchMove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b) {
                    postMsg("canmove","moveallowed");
                    saveStates("movestate", true);
                }
                else {
                    postMsg("canmove","moveforbidden");
                    saveStates("movestate", false);
                }

            }
        });

    }

    /**
     * 向Ｓervice发送消息
     * @param action
     */

    public  void postMsg(String name,String action)
    {
        Intent intent = new Intent();
        intent.putExtra(name, action);
        intent.setClass(MainActivity.this, FloatingBallService.class);
        startService(intent);

    }

    /**
     * 退出Service
     */
    public void exitService()
    {
        serviceState = false;
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(MainActivity.this, FloatingBallService.class);
        stopService(serviceIntent);

    }

    /**
     * 启动Service
     */
    public void openService()
    {
        serviceState = true;
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(MainActivity.this, FloatingBallService.class);
        startService(serviceIntent);

    }
    /**
     * 将状态数据保存在sharepreferences
     * @param name
     * @param state
     */
    public void saveStates(String name ,boolean state)
    {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name, state);
        editor.commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
