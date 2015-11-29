package com.hardwork.fg607.floatingball;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hardwork.fg607.floatingball.utils.ChooseKeyItem;

import java.util.ArrayList;


public class ChooseKeyActivity extends ActionBarActivity {

    private ListView mLv_Key;
    private ArrayList<ChooseKeyItem> mArrayList;
    private String mIntentValue;
    private String mResultValue;
    public static final int CHOOSE_KEYCODE = 119;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_key);

        mLv_Key = (ListView) findViewById(R.id.lv_key);
        mArrayList = new ArrayList<ChooseKeyItem>();

        final Intent intent = getIntent();

        if(intent != null) {
            mIntentValue = intent.getStringExtra("value");
        }

        //初始化功能列表
        init();

        //选中传递来的功能
        findAndSetChecked(mIntentValue);

        mLv_Key.setAdapter(new MyAdapter());
        mLv_Key.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                //清空功能列表（用列表的动态更新方法更好）
                mArrayList.clear();
                init();
                mArrayList.get(position).setIsChecked(true);
                mResultValue = mArrayList.get(position).getKey();

                mLv_Key.setAdapter(new MyAdapter());

                Intent intent1 = new Intent();
                intent1.putExtra("resultvalue",mResultValue);
                ChooseKeyActivity.this.setResult(CHOOSE_KEYCODE, intent1);
                ChooseKeyActivity.this.finish();


            }
        });
    }


    /**
     * 初始化功能列表
     */
   public void init() {
    mArrayList.add(new ChooseKeyItem(R.drawable.ic_home, "移动(固定)悬浮球", false));
    mArrayList.add(new ChooseKeyItem(R.drawable.ic_back, "返回键", false));
    mArrayList.add(new ChooseKeyItem(R.drawable.ic_menu, "菜单键", false));
    mArrayList.add(new ChooseKeyItem(R.drawable.ic_home,"Home键",false));
    mArrayList.add(new ChooseKeyItem(R.drawable.ic_recent_apps,"最近任务键",false));
    mArrayList.add(new ChooseKeyItem(R.drawable.ic_power,"电源键",false));
    mArrayList.add(new ChooseKeyItem(R.drawable.ic_volume_add,"音量键加",false));
    mArrayList.add(new ChooseKeyItem(R.drawable.ic_volume_sub, "音量键减", false));
    mArrayList.add(new ChooseKeyItem(R.drawable.ic_reboot_system, "重启", false));
    mArrayList.add(new ChooseKeyItem(R.drawable.ic_shutdown_system, "关机", false));

  }

    /**
     * 选中传递的功能
     * @param key
     */
    public void findAndSetChecked(String key) {
        for (int i = 0;i<mArrayList.size();i++) {
            if( mArrayList.get(i).getKey().equals(key)) {
                mArrayList.get(i).setIsChecked(true);
            }
        }

    }

    /**
     * 功能列表适配器
     */
    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            View itemView = null;

            if(view == null) {
                itemView = View.inflate(ChooseKeyActivity.this,R.layout.choosekey_item,null);
            }
            else {
                itemView = view;
            }

            ImageView imgview = (ImageView) itemView.findViewById(R.id.image);
            TextView textView = (TextView) itemView.findViewById(R.id.text);
            CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            ChooseKeyItem chooseKeyItem = mArrayList.get(position);
            imgview.setImageResource(chooseKeyItem.getDrawableId());
            textView.setText(chooseKeyItem.getKey());
            checkBox.setChecked(chooseKeyItem.getIsChecked());
            return itemView;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_key, menu);
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
