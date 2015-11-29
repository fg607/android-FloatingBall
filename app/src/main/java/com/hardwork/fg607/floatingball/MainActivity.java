package com.hardwork.fg607.floatingball;

/**
 * Created by fg607 on 15-8-20.
 *
 */

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.hardwork.fg607.floatingball.service.FloatingBallService;
import com.hardwork.fg607.floatingball.utils.BallFunctionDao;
import com.hardwork.fg607.floatingball.utils.FloatingBallUtils;
import com.hardwork.fg607.floatingball.utils.ImageUtils;
import com.hardwork.fg607.floatingball.utils.MyPagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.hardwork.fg607.floatingball.R.id.add;
import static com.hardwork.fg607.floatingball.R.id.lv_scene;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

    private Switch mSwitchOnOff,mSwitchMove,mSwitchAutoStart ;
    public  SharedPreferences sp;
    private ViewPager mViewPager;
    private List<View> viewList;
    private ActionBar mActionBar;
    private ArrayList<ActionBar.Tab> mTabs;
    public Spinner spinner;
    public Button sceneAdd;
    public ListView lv_action;
    private List<String> mFuncNameList;
    private List<String> mActionList;
    private LayoutInflater mInflater;
    private BallFunctionDao mBallFunctionDao;
    private ActionListViewAdapter mActionListViewAdapter;
    private int mChangePositon;
    public ListView lvFuncKey;
    public ArrayList<String> funcKeyList;
    public static final int SETUPKEY = 1377;
    public static final int MAX_TEXT = 7;
    public static final int SETUP_BASE_ACTIVITY = 0;
    public static final int SETUP_GESTURE_ACTIVITY = 3;
    public static final int SETUP_ACTION = 933;
    public String currentKey;
    public ArrayList<String> currentKeyData;
    private ArrayList<String> mSceneList;
    private ListView  mSceneListView;
    private AlertDialog mSceneAddDlg;
    private SceneAdapter mSceneAdapter;
    private ArrayAdapter mSpinnerAdapter;
    private SeekBar mSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //生成存储读写工具
        sp = getSharedPreferences("config",MODE_WORLD_READABLE);
        mBallFunctionDao = new BallFunctionDao(this);

        //初次打开程序初始化
        boolean isFirstStart = sp.getBoolean("firststart",true);
        if(isFirstStart) {

            initConfig();
            FloatingBallUtils.saveState(sp,"firststart",false);
        }

        initActionBar();


        //生成4个设置界面
        mInflater = getLayoutInflater().from(this);

        View view_setup1 = mInflater.inflate(R.layout.setup1, null);
        View view_setup2 = mInflater.inflate(R.layout.setup2, null);
        View view_setup3 = mInflater.inflate(R.layout.setup3, null);
        View view_setup4 = mInflater.inflate(R.layout.setup4, null);

        initTab1(view_setup1);
        initTab2(view_setup2);
        initTab3(view_setup3);
        initTab4(view_setup4);

        initViewPager(view_setup1, view_setup2, view_setup3, view_setup4);


    }

    public void initViewPager(View view_setup1, View view_setup2, View view_setup3, View view_setup4) {
        //实例化ViewPager适配器
        viewList = new ArrayList<View>();
        viewList.add(view_setup1);
        viewList.add(view_setup2);
        viewList.add(view_setup3);
        viewList.add(view_setup4);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter(viewList));
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(SETUP_BASE_ACTIVITY); //基础设置页面为首页
    }

    public void initTab4(View view_setup4) {
        //初始化setup4界面
        mFuncNameList = mBallFunctionDao.findFuncsAllName();
        spinner = (Spinner) view_setup4.findViewById(R.id.spinner);
        mSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,mFuncNameList);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                //刷新动作列表
                mActionListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        lv_action = (ListView) view_setup4.findViewById(R.id.lv_action);
        lv_action.setDivider(null);//取消列表分割线

        mActionList = new ArrayList<String>();
        mActionList.add("单击");
        mActionList.add("双击");
        mActionList.add("上滑");
        mActionList.add("下滑");
        mActionList.add("左滑");
        mActionList.add("右滑");

        mActionListViewAdapter = new ActionListViewAdapter();
        lv_action.setAdapter(mActionListViewAdapter);

        lv_action.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                mChangePositon = position;
                Intent intent = new Intent();
                intent.putExtra("value", mBallFunctionDao.findFuncs((String) spinner.getSelectedItem()).get(position));
                intent.setClass(MainActivity.this, ChooseKeyActivity.class);
                startActivityForResult(intent, SETUP_ACTION);
            }
        });
    }

    public void initTab3(View view_setup3) {
        //初始化setup3界面
        sceneAdd = (Button) view_setup3.findViewById(add);
        mSceneListView = (ListView) view_setup3.findViewById(lv_scene);
        mSceneList = mBallFunctionDao.findFuncsAllName();
        mSceneAdapter = new SceneAdapter();
        mSceneListView.setAdapter(mSceneAdapter);

        sceneAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupSceneAddDlg();
            }
        });

        mSceneListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                    popupSceneItemDlg(i);

                return false;
            }
        });
    }

    public void initTab2(View view_setup2) {
        //初始化setup2界面控件
        lvFuncKey = (ListView) view_setup2.findViewById(R.id.lv_funckey);
        funcKeyList = new ArrayList<String>();

        funcKeyList.add("menuA");
        funcKeyList.add("menuB");
        funcKeyList.add("menuC");
        funcKeyList.add("menuD");
        funcKeyList.add("menuE");

        updateKeyView();//绑定适配器

        lvFuncKey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                currentKey = mBallFunctionDao.findAllKeyName().get(i);

                Intent intent = new Intent(MainActivity.this,FuncKeySetupActivity.class);

                intent.putExtra("keyname", currentKey);

                startActivityForResult(intent,SETUPKEY);

            }
        });
    }

    public void initTab1(View view_setup1) {
        //初始化setup1界面控件
        mSwitchOnOff = (Switch) view_setup1.findViewById(R.id.switch_start);
        mSwitchMove = (Switch) view_setup1.findViewById(R.id.switch_move);
        mSwitchAutoStart = (Switch) view_setup1.findViewById(R.id.switch_autostart);
        mSeekBar = (SeekBar)view_setup1.findViewById(R.id.seekbar);

        //获取保存的状态数据，初始化开关状态
        if(sp.getBoolean("ballstate",false))
        {
            mSwitchOnOff.setChecked(true);

            postMsg("ballstate", "showball");

        }
        else
        {
            mSwitchOnOff.setChecked(false);
        }

        if(sp.getBoolean("autostart",false))
        {
            mSwitchAutoStart.setChecked(true);
        }
        else
        {
            mSwitchAutoStart.setChecked(false);
        }


        mSwitchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {

                    //向service发送显示悬浮求消息，并保存悬浮球显示状态
                    postMsg("ballstate", "showball");
                    FloatingBallUtils.saveState(sp, "ballstate", true);

                    mSwitchMove.setEnabled(true);
                    mSwitchAutoStart.setEnabled(true);
                    mSeekBar.setEnabled(true);


                } else {
                    FloatingBallUtils.saveState(sp, "ballstate", false);
                    exitService();
                    mSwitchMove.setChecked(false);

                    //如果悬浮球关闭，禁用所有其它选项
                    mSwitchMove.setEnabled(false);
                    mSwitchAutoStart.setEnabled(false);
                    mSeekBar.setEnabled(false);
                }
            }
        });

        if(sp.getBoolean("movestate",false))
        {
            mSwitchMove.setChecked(true);

        }
        else
        {
            mSwitchMove.setChecked(false);
        }
        mSwitchMove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b) {
                    postMsg("canmove","moveallowed");
                    FloatingBallUtils.saveState(sp, "movestate", true);
                }
                else {
                    postMsg("canmove","moveforbidden");
                    FloatingBallUtils.saveState(sp, "movestate", false);
                }

            }
        });

        mSwitchAutoStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    FloatingBallUtils.saveState(sp, "autostart", true);
                } else {
                    FloatingBallUtils.saveState(sp, "autostart", false);
                }

            }
        });


        //获取seekbar位置
        int position = sp.getInt("seekbar_position",0);
        if( position != 0){

            mSeekBar.setProgress(position);
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub

                //保存seekbar进度
                FloatingBallUtils.saveState(sp,"seekbar_position",arg1);
                    Intent serviceIntent = new Intent();
                    serviceIntent.putExtra("ballsize", arg1);
                    serviceIntent.setClass(MainActivity.this, FloatingBallService.class);
                    startService(serviceIntent);

            }
        });
    }

    public void initActionBar() {
        //设置actionBar为导航栏模式，添加标题
        mActionBar = getSupportActionBar();// 如果不使用Android Support Library, 则调用getActionBar()方法
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);// NAVIGATION_MODE_TABS常量表示Tab导航模式
        mActionBar.setDisplayShowTitleEnabled(true);//显示标题

        mTabs=new ArrayList<ActionBar.Tab>();

        ActionBar.Tab tab0=mActionBar.newTab();
        tab0.setText("基础设置");
        tab0.setTabListener(this);
        mTabs.add(tab0);
        mActionBar.addTab(tab0);

        ActionBar.Tab tab1=mActionBar.newTab();
        tab1.setText("功能键设置");
        tab1.setTabListener(this);
        mTabs.add(tab1);
        mActionBar.addTab(tab1);

        ActionBar.Tab tab2=mActionBar.newTab();
        tab2.setText("场景管理");
        tab2.setTabListener(this);
        mTabs.add(tab2);
        mActionBar.addTab(tab2);

        ActionBar.Tab tab3=mActionBar.newTab();
        tab3.setText("手势设置");
        tab3.setTabListener(this);
        mTabs.add(tab3);
        mActionBar.addTab(tab3);
    }

    /**
     * 场景列表适配器
     */
    public class SceneAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return mSceneList.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {

            View tv_scene = null;
            if(view == null){

                tv_scene = View.inflate(MainActivity.this,R.layout.scene_item,null);
            }
            else{

               tv_scene = view;
            }

            TextView sceneName = (TextView) tv_scene.findViewById(R.id.sceneName);
            sceneName.setText(mSceneList.get(i));

            return tv_scene;
        }
    }

    //初始化参数
    private void initConfig() {
        //初始化5个预设场景，存入数据库
        mBallFunctionDao.addFuncs("默认场景", "返回键", "菜单键", "菜单键", "Home键", "最近任务键", "电源键");
        mBallFunctionDao.addFuncs("预设场景1", "菜单键", "菜单键", "菜单键", "Home键", "最近任务键", "电源键");
        mBallFunctionDao.addFuncs("预设场景2", "Home键", "菜单键", "菜单键", "Home键", "最近任务键", "电源键");
        mBallFunctionDao.addFuncs("预设场景3", "最近任务键", "菜单键", "菜单键", "Home键", "最近任务键", "电源键");
        mBallFunctionDao.addFuncs("预设场景4", "电源键", "菜单键", "菜单键", "Home键", "最近任务键", "电源键");


        //初始化5个功能键对应的功能，存入SharedPreferences
        String menuA = sp.getString("menuA", null);
        if(menuA == null) {
            FloatingBallUtils.saveState(sp, "menuA", "默认场景");
            FloatingBallUtils.saveState(sp,"menuAFunc","scene");
        }

        String menuB = sp.getString("menuB",null);
        if(menuB == null) {
            FloatingBallUtils.saveState(sp,"menuB","预设场景1");
            FloatingBallUtils.saveState(sp,"menuBFunc","scene");
        }

        String menuC = sp.getString("menuC",null);
        if(menuC == null) {
            FloatingBallUtils.saveState(sp,"menuC","预设场景2");
            FloatingBallUtils.saveState(sp,"menuCFunc","scene");
        }

        String menuD = sp.getString("menuD",null);
        if(menuD == null) {
            FloatingBallUtils.saveState(sp,"menuD","预设场景3");
            FloatingBallUtils.saveState(sp,"menuDFunc","scene");
        }

        String menuE = sp.getString("menuE",null);
        if(menuE == null) {
            FloatingBallUtils.saveState(sp,"menuE","预设场景4");
            FloatingBallUtils.saveState(sp,"menuEFunc","scene");
        }

        //初始化悬浮球当前使用的场景
        String currentFunction = sp.getString("currentfunction",null);

        if(currentFunction == null) {

            FloatingBallUtils.saveState(sp,"currentfunction","menuA");
        }

        //初始化５个功能键对应的图标/标题和场景，存入数据库

        mBallFunctionDao.addFuncKey("menuA","nor","","默认场景");
        mBallFunctionDao.addFuncKey("menuB","clover","","预设场景1");
        mBallFunctionDao.addFuncKey("menuC","american","","预设场景2");
        mBallFunctionDao.addFuncKey("menuD","superman","","预设场景3");
        mBallFunctionDao.addFuncKey("menuE","iphone","","预设场景4");
    }


    /**
     * 弹出添加场景对话框
     */
    public void popupSceneAddDlg(){

        View view = View.inflate(this, R.layout.scene_add_dlg, null);
        final EditText sceneName = (EditText) view.findViewById(R.id.sceneName);

        //设置场景名称最大字符数
        sceneName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TEXT
        )});

        TextView  bt_ok = (TextView) view.findViewById(R.id.ok);
        TextView bt_cancel = (TextView) view.findViewById(R.id.cancel);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = sceneName.getText().toString().trim();
                if (text != null) {
                    addScene(text);
                    updateSceneList();
                    updateSpinnerList();//更新手势设置中的场景
                }
                mSceneAddDlg.dismiss();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSceneAddDlg.dismiss();
            }
        });

        mSceneAddDlg =  FloatingBallUtils.showDlg(this, "添加场景", view);

    }

    /**
     * 弹出长按scene Item对话框
     * @param position 场景条目
     */
    public void popupSceneItemDlg(final int position) {

        final String sceneName = mSceneList.get(position);
        View view = View.inflate(this, R.layout.scene_item_dlg, null);
        TextView  bt_set = (TextView) view.findViewById(R.id.set_gesture);
        TextView bt_del = (TextView) view.findViewById(R.id.delete);

        //点击设置手势
        bt_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //跳转到设置手势界面
                spinner.setSelection(position);
                mViewPager.setCurrentItem(SETUP_GESTURE_ACTIVITY);
                mSceneAddDlg.dismiss();
            }
        });

        //点击删除场景
        bt_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteScene(sceneName);
                updateSceneList();
                updateSpinnerList();
                mSceneAddDlg.dismiss();
            }
        });

        mSceneAddDlg =  FloatingBallUtils.showDlg(this, "场景设置", view);

    }

    /**
     * 向数据库添加场景条目
     * @param sceneName
     */
    public void addScene(String sceneName) {

        mBallFunctionDao.addFuncsDefault(sceneName);

    }

    /**
     * 删除数据库中的场景条目
     * @param sceneName
     */
    public void deleteScene(String sceneName) {

        mBallFunctionDao.deleteFuncs(sceneName);
    }

    /**
     * 刷新场景列表
     */
    public void updateSceneList() {

        mSceneList = mBallFunctionDao.findFuncsAllName();
        mSceneAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新spinner列表
     */
    public  void updateSpinnerList(){

        mFuncNameList = mBallFunctionDao.findFuncsAllName();
        mSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,mFuncNameList);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mSpinnerAdapter);

    }

    /**
     * 功能键列表适配器
     */
    public class FuncKeyListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return funcKeyList.size();
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

            View funcKeylv = null;
            if(view == null) {
                funcKeylv = View.inflate(MainActivity.this,R.layout.func_key_item,null);
            }
            else {
                funcKeylv = view;
            }

            ArrayList<String> keyContent = mBallFunctionDao.findFuncKey(mBallFunctionDao.findAllKeyName().get(position));

            TextView tv_funcKey = (TextView) funcKeylv.findViewById(R.id.tv_funckey);
            ImageView img = (ImageView) funcKeylv.findViewById(R.id.img_funckey);
            TextView name_funcKey = (TextView) funcKeylv.findViewById(R.id.scene_funckey);
            tv_funcKey.setText(funcKeyList.get(position));
            String menu = funcKeyList.get(position);
            String func = sp.getString(menu+"Func",null);
            if(func.equals("scene")){
                img.setImageBitmap(FloatingBallUtils.getBitmap(keyContent.get(0)));
                name_funcKey.setText(keyContent.get(2));
            }else if(func.equals("app")){
                ArrayList<String> appKeyContent = mBallFunctionDao.findAppKey(menu);
                img.setImageBitmap(FloatingBallUtils.getBitmap(appKeyContent.get(1)));
                name_funcKey.setText(appKeyContent.get(0));
            }




            return funcKeylv;
        }
    }

    /**
     * 动作列表适配器
     */
    public class ActionListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mActionList.size();
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

            View itemview = null;

            if(view == null) {
                itemview = View.inflate(MainActivity.this,R.layout.funclv_item, null);
            }
            else {
                itemview = view;
            }

            TextView textView = (TextView) itemview.findViewById(R.id.tv_action);
            textView.setText(mActionList.get(position));
            TextView textView1 = (TextView) itemview.findViewById(R.id.tv_function);
            textView1.setText(mBallFunctionDao.findFuncs((String)spinner.getSelectedItem()).get(position));

            return itemview;
        }
    }
    /**
     * 向Ｓervice发送消息
     * @param action
     */
    public  void postMsg(String name,String action) {
        Intent intent = new Intent();
        intent.putExtra(name, action);
        intent.setClass(MainActivity.this, FloatingBallService.class);
        startService(intent);
    }

    public  void postMsg(String name,boolean value) {
        Intent intent = new Intent();
        intent.putExtra(name, value);
        intent.setClass(MainActivity.this, FloatingBallService.class);
        startService(intent);
    }

    /**
     * 停止悬浮球服务
     */
    public void exitService() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, FloatingBallService.class);
        stopService(intent);
    }

    /**
     * 绑定场景
     * @param keyName 功能键
     * @param scene 场景名称
     */
    public void bindScene(String keyName,String scene) {
        FloatingBallUtils.saveState(sp,keyName+"Func","scene");
        FloatingBallUtils.saveState(sp, keyName, scene);
        postMsg("loadfunction", true);
        postMsg("updatemenuicons", true);
    }

    public void bindApp(){

        postMsg("updatemenuicons", true);
    }

    /**
     * 将用户设置的内容保存到数据库
     * @param keyName
     * @param data
     */
    public  void saveKeyData(String keyName,ArrayList<String> data) {
        mBallFunctionDao.updateFuncKey(keyName, data.get(0), data.get(1), data.get(2));
    }

    public void saveAppKeyData(String currentKey,Intent data){

        String iconPath = saveAppIcon(data.getStringExtra("name"),data.getByteArrayExtra("icon"));
        FloatingBallUtils.saveState(sp, currentKey + "Func", data.getStringExtra("func"));

        ArrayList<String> list = mBallFunctionDao.findAppKey(currentKey);
        if(list != null && list.size() > 0){

            mBallFunctionDao.updateAppKey(currentKey,data.getStringExtra("name"),
                    iconPath,data.getStringExtra("package"));
        }else {

            mBallFunctionDao.addAppKey(currentKey,data.getStringExtra("name"),
                    iconPath,data.getStringExtra("package"));
        }


    }

    public String saveAppIcon(String name,byte[] bytes){

        Bitmap bp = ImageUtils.Bytes2Bitmap(bytes);
        String path = null;
        try {
            path = FloatingBallUtils.saveBitmap(bp, name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }
    /**
     * 更新设置后的功能键列表
     */
    public void updateKeyView()
    {
        lvFuncKey.setAdapter(new FuncKeyListViewAdapter());
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


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        if(mViewPager!=null)
        {
            mViewPager.setCurrentItem(tab.getPosition());
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        //设置当前要显示的View
        mViewPager.setCurrentItem(position);
        //选中对应的Tab
        mActionBar.selectTab(mTabs.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //设置功能键对应场景
        if(requestCode == SETUPKEY) {
            if(data != null) {

                String func = data.getStringExtra("func");

                if(func.equals("scene")){
                    currentKeyData = data.getStringArrayListExtra("keycontent");

                    if(currentKeyData != null) {
                        saveKeyData(currentKey,currentKeyData);
                        bindScene(currentKey,currentKeyData.get(2));//currentKeyData.get(2)场景名称
                        updateKeyView();
                    }
                }
                else if(func.equals("app")){

                    saveAppKeyData(currentKey,data);
                    bindApp();
                    updateKeyView();
                }else if(func.equals("oldapp")){

                    FloatingBallUtils.saveState(sp,currentKey+"Func","app");
                    bindApp();
                    updateKeyView();

                }

            }
        }


        //更新动作列表数据库
        String resultvalue = null;
        if(data != null) {
            resultvalue = data.getStringExtra("resultvalue");
        }

        if(resultvalue != null) {
            switch (mChangePositon) {
                case 0:
                    mBallFunctionDao.updateClickFuncs((String)spinner.getSelectedItem(),resultvalue);
                    break;
                case 1:
                    mBallFunctionDao.updateDoubleClickFuncs((String)spinner.getSelectedItem(),resultvalue);
                    break;
                case 2:
                    mBallFunctionDao.updateFlipUpFuncs((String)spinner.getSelectedItem(),resultvalue);
                    break;
                case 3:
                    mBallFunctionDao.updateFlipDownFuncs((String)spinner.getSelectedItem(),resultvalue);
                    break;
                case 4:
                    mBallFunctionDao.updateFlipLeftFuncs((String)spinner.getSelectedItem(),resultvalue);
                    break;
                case 5:
                    mBallFunctionDao.updateFlipRightFuncs((String)spinner.getSelectedItem(),resultvalue);
                    break;
            }

            mActionListViewAdapter.notifyDataSetChanged();

            //更新动作后重新加载悬浮球功能
            postMsg("loadfunction", true);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
