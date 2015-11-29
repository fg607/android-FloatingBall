package com.hardwork.fg607.floatingball.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hardwork.fg607.floatingball.R;
import com.hardwork.fg607.floatingball.adapter.AppAdapter;
import com.hardwork.fg607.floatingball.model.AppInfo;
import com.hardwork.fg607.floatingball.utils.AppUtils;
import com.hardwork.fg607.floatingball.utils.ImageUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseAppActivity extends Activity {

    @Bind(R.id.lv_app) ListView mListView;
    private ArrayList<AppInfo> mAppList;
    private AppAdapter mAppAdapter;
    private String mChoosedAppName = null;
    public static final int CHOOSE_APPCODE = 1<<0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_app);
        ButterKnife.bind(this);
        init();
    }

    public void init(){
        mAppList = AppUtils.getAppInfos(this);
        mAppAdapter = new AppAdapter(this);
        mAppAdapter.addList(mAppList);
        Intent intent = getIntent();
        mChoosedAppName = intent.getStringExtra("app_name");
        mAppAdapter.setAppChecked(mChoosedAppName);
        mListView.setAdapter(mAppAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mAppAdapter.setAppChecked(mAppList.get(i).getAppName());

                Intent intent = new Intent();

                intent.putExtra("icon", ImageUtils.Drawable2Bytes(mAppList.get(i).getAppIcon()));
                intent.putExtra("name", mAppList.get(i).getAppName());
                intent.putExtra("package", mAppList.get(i).getAppPackage());

                ChooseAppActivity.this.setResult(CHOOSE_APPCODE, intent);
                ChooseAppActivity.this.finish();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_app, menu);
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
