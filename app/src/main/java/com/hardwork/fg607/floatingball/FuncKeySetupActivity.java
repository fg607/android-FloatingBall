package com.hardwork.fg607.floatingball;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hardwork.fg607.floatingball.ui.ChooseAppActivity;
import com.hardwork.fg607.floatingball.utils.BallFunctionDao;
import com.hardwork.fg607.floatingball.utils.FloatingBallUtils;
import com.hardwork.fg607.floatingball.utils.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FuncKeySetupActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageView mIconImg;
    private TextView mScene;
    private BallFunctionDao mBallFunctionDao;
    private ImageView mCheck1;
    private ImageView mCheck2;
    private ImageView mCheck3;
    private ImageView mCheck4;
    private ImageView mCheck5;
    private ImageView mCheck6;
    private CircleImageView mAppIcon;
    private CheckBox mCheckBoxApp;
    private CheckBox mCheckBoxScene;
    private TextView mAppName;
    private RelativeLayout mImg1;
    private RelativeLayout mImg2;
    private RelativeLayout mImg3;
    private RelativeLayout mImg4;
    private RelativeLayout mImg5;
    private RelativeLayout mImg6;
    private RelativeLayout mSceneLayout;
    private RelativeLayout mAppLayout;
    private ImageView mViewChoosed;
    private Bitmap mUserBitmap = null;
    private Button mNewIconButton;
    private EditText mEditText;
    private AlertDialog mEditDialog;
    private AlertDialog mSceneDialog;
    private ArrayList<String> mSceneList;
    private String mIconPath;
    private ArrayList<String> mKeyContent;
    private ArrayList<String> mAppKeyContent = null;
    private String mFileName;
    String keyName;
    private CircleImageView circleImageView;
    private Intent mMenuSavedIntent;
    private SharedPreferences sp;
    public static final int CHOOSE_APPCODE = 1<<0;

    public static final int REQUEST_PICK = 345;
    public static final int REQUEST_CLIP = 346;
    public static final int KEY_SET_FAILED = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_func_key_setup);

        sp = FloatingBallUtils.getSharedPreferences(this);
        circleImageView = (CircleImageView) findViewById(R.id.icon_app);

        bindViews();

        initViews();
    }

    public void initViews() {

        Intent intent = getIntent();
         keyName = intent.getStringExtra("keyname");

        if(keyName != null) {
            this.setTitle(keyName+"设置");
            String func = sp.getString(keyName+"Func",null);
            if(func.equals("scene")){

                mCheckBoxScene.setChecked(true);
                mCheckBoxApp.setChecked(false);
            }
            else if(func.equals("app")){

                mCheckBoxScene.setChecked(false);
                mCheckBoxApp.setChecked(true);

            }

            mBallFunctionDao = new BallFunctionDao(this);
            //读取场景信息
            mKeyContent = mBallFunctionDao.findFuncKey(keyName);
            mIconImg.setImageBitmap(FloatingBallUtils.getBitmap(mKeyContent.get(0)));
            mViewChoosed =  setIconChoosed(mKeyContent.get(0));
            mScene.setText(mKeyContent.get(2));

            //读取app信息
            mAppKeyContent = mBallFunctionDao.findAppKey(keyName);
            if(mAppKeyContent != null && mAppKeyContent.size() > 0){

                mAppName.setText(mAppKeyContent.get(0));

                Bitmap bitmap = ImageUtils.scaleBitmap(mAppKeyContent.get(1),35);
                mAppIcon.setImageDrawable(ImageUtils.bitmap2Drawable(bitmap));
            }

        }
        else {
            Intent intent1 =  new Intent(this,MainActivity.class);
            intent1.putExtra("keySetOk", false);
            this.setResult(KEY_SET_FAILED, intent1);
            finish();
        }
    }

    public void bindViews() {
        mIconImg = (ImageView) findViewById(R.id.icon);
        mScene = (TextView) findViewById(R.id.tv_scene);
        mAppIcon = (CircleImageView) findViewById(R.id.icon_app);
        mAppName = (TextView) findViewById(R.id.app_name);
        mImg1 = (RelativeLayout) findViewById(R.id.img1);
        mImg2 = (RelativeLayout) findViewById(R.id.img2);
        mImg3 = (RelativeLayout) findViewById(R.id.img3);
        mImg4 = (RelativeLayout) findViewById(R.id.img4);
        mImg5 = (RelativeLayout) findViewById(R.id.img5);
        mImg6 = (RelativeLayout) findViewById(R.id.img6);
        mSceneLayout = (RelativeLayout) findViewById(R.id.scene_layout);
        mAppLayout = (RelativeLayout) findViewById(R.id.app_layout);
        mCheckBoxApp = (CheckBox) findViewById(R.id.checkbox_app);
        mCheckBoxScene = (CheckBox) findViewById(R.id.checkbox_scene);
        mNewIconButton = (Button) findViewById(R.id.iconedit);
        mImg1.setOnClickListener(this);
        mImg2.setOnClickListener(this);
        mImg3.setOnClickListener(this);
        mImg4.setOnClickListener(this);
        mImg5.setOnClickListener(this);
        mImg6.setOnClickListener(this);
        mSceneLayout.setOnClickListener(this);
        mAppLayout.setOnClickListener(this);
        mCheckBoxScene.setOnClickListener(this);
        mCheckBoxApp.setOnClickListener(this);
        mNewIconButton.setOnClickListener(this);
        mCheck1 = (ImageView) findViewById(R.id.check1);
        mCheck2 = (ImageView) findViewById(R.id.check2);
        mCheck3 = (ImageView) findViewById(R.id.check3);
        mCheck4 = (ImageView) findViewById(R.id.check4);
        mCheck5 = (ImageView) findViewById(R.id.check5);
        mCheck6 = (ImageView) findViewById(R.id.check6);
    }

    /**
     * 显示选中标志小图标
     * @param iconName
     * @return
     */
    public ImageView setIconChoosed(String iconName) {

        ImageView imageView = null;
        switch (iconName) {
            case "nor":
                imageView = mCheck1;
                break;
            case "clover":
                imageView = mCheck2;
                break;
            case "american":
                imageView = mCheck3;
                break;
            case "iphone":
                imageView = mCheck4;
                break;
            case "superman":
                imageView = mCheck5;
                break;
            default:
                imageView = mCheck6;

                //加载用户自定义图标
                if(mUserBitmap == null) {
                    mUserBitmap = FloatingBallUtils.getBitmap(mKeyContent.get(0));
                    if(mUserBitmap != null) {
                        showImg6(mUserBitmap);
                    }

                }
                break;
        }

        //保存功能键图标信息
        mKeyContent.set(0,iconName);

        if(imageView != null) {
            imageView.setVisibility(View.VISIBLE);
        }

        return imageView;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_func_key_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        //点击完成图标
        if (id == R.id.action_compeleted) {

            if(mCheckBoxScene.isChecked()){
                Intent intent = new Intent();
                mKeyContent.set(2,mScene.getText().toString());

                intent.putStringArrayListExtra("keycontent", mKeyContent);
                intent.putExtra("func", "scene");
                mMenuSavedIntent = intent;
            }
            else {
                String menuName = sp.getString("currentfunction", null);
                if(menuName.equals(keyName)){

                    Toast.makeText(this,"当前功能键场景正在应用中，请先解绑功能键场景！",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                if(mMenuSavedIntent != null){
                    mMenuSavedIntent.putExtra("func","app");
                }
                else {
                    mMenuSavedIntent  = new Intent();
                    mMenuSavedIntent.putExtra("func","oldapp");
                }


            }

            setResult(MainActivity.SETUPKEY,mMenuSavedIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.img1:
                mIconImg.setImageDrawable(getResources().getDrawable(R.drawable.nor));
                mViewChoosed.setVisibility(View.INVISIBLE);
                mViewChoosed = setIconChoosed("nor");
                break;
            case R.id.img2:
                mIconImg.setImageDrawable(getResources().getDrawable(R.drawable.clover));
                mViewChoosed.setVisibility(View.INVISIBLE);
                mViewChoosed = setIconChoosed("clover");
                break;
            case R.id.img3:
                mIconImg.setImageDrawable(getResources().getDrawable(R.drawable.american));
                mViewChoosed.setVisibility(View.INVISIBLE);
                mViewChoosed = setIconChoosed("american");
                break;
            case R.id.img4:
                mIconImg.setImageDrawable(getResources().getDrawable(R.drawable.iphone));
                mViewChoosed.setVisibility(View.INVISIBLE);
                mViewChoosed = setIconChoosed("iphone");
                break;
            case R.id.img5:
                mIconImg.setImageDrawable(getResources().getDrawable(R.drawable.superman));
                mViewChoosed.setVisibility(View.INVISIBLE);
                mViewChoosed = setIconChoosed("superman");
                break;
            case R.id.img6:
                if(mUserBitmap != null) {
                    mIconImg.setImageBitmap(mUserBitmap);
                    mViewChoosed.setVisibility(View.INVISIBLE);
                    mViewChoosed = setIconChoosed(mIconPath);
                }

                break;
            case R.id.scene_layout:
                popupSceneDialog();
                break;
            case R.id.iconedit:
                cropIcon();
                break;
            case R.id.app_layout:
                openChooseAppDialog();
                break;
            case R.id.checkbox_app:
                setApp();
                break;
            case R.id.checkbox_scene:
                setScene();
                break;
            default:
                break;
        }

    }

    public void setApp(){

        if(mCheckBoxApp.isChecked()){

            mCheckBoxScene.setChecked(false);
        }else {
            mCheckBoxScene.setChecked(true);
        }
    }

    public void setScene(){

        if(mCheckBoxScene.isChecked()){

            mCheckBoxApp.setChecked(false);
        }else {
            mCheckBoxApp.setChecked(true);

        }

    }
    public void openChooseAppDialog(){

        Intent intent = new Intent();
        intent.putExtra("app_name",mAppName.getText());
        intent.setClass(this, ChooseAppActivity.class);
        startActivityForResult(intent,CHOOSE_APPCODE);
    }

    /**
     * 显示用户自定义的图标
     * @param bitmap
     */
    public void showImg6(Bitmap bitmap) {
        if(!mImg6.isClickable()) {
            mImg6.setClickable(true);
        }

        mImg6.setBackgroundDrawable(new BitmapDrawable(bitmap));
    }


    /**
     * 弹出选择场景对话框
     */
    public void popupSceneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("设置场景");
        View view = View.inflate(this, R.layout.scenelist_popup, null);

        ListView listView = (ListView) view.findViewById(R.id.lv_scene);

        mSceneList = mBallFunctionDao.findFuncsAllName();
        listView.setAdapter(new SceneAdapter());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mScene.setText(mSceneList.get(i));
                mSceneDialog.dismiss();
            }
        });

        builder.setView(view);
        mSceneDialog =  builder.show();

    }

    public void closeSceneDialog() {

        mSceneDialog.dismiss();
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

            View sceneView = null;

            if(view == null) {
                sceneView = View.inflate(FuncKeySetupActivity.this,R.layout.scene_item_layout,null);
            }
            else {
                sceneView = view;
            }

            TextView textView = (TextView) sceneView.findViewById(R.id.tv_scene);
            textView.setText(mSceneList.get(i));

            return sceneView;
        }
    }

    /**
     * 选择图片浏览器
     */
    public void cropIcon() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        try {
            startActivityForResult(intent, REQUEST_PICK);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //裁剪图片
        if(requestCode == REQUEST_PICK) {

            if(data != null) {
                Uri imageUri = data.getData();
            try {
                //将选择的图片进行暂存
                FloatingBallUtils.bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Intent intent = new Intent();
                intent.setClass(this, ClipImageActivity.class);
                startActivityForResult(intent,REQUEST_CLIP);

            } catch (IOException e) {
                e.printStackTrace();
            }
            }

        }
        //得到裁剪后的图标
       else if (requestCode == REQUEST_CLIP) {
            mFileName = null;

            if(data != null) {
                mFileName = data.getStringExtra("filename");
                //将裁剪后的图标保存到hwfb文件夹
                String rootdir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/hwfb";
                mIconPath = rootdir +"/"+mFileName;
            }


            if (mIconPath != null) {
                mUserBitmap = FloatingBallUtils.bitmap;
                try {
                    FloatingBallUtils.saveBitmap(mUserBitmap,mFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //显示裁剪后的图标
                mIconImg.setImageBitmap(mUserBitmap);
                showImg6(mUserBitmap);
                mViewChoosed.setVisibility(View.INVISIBLE);
                mViewChoosed = setIconChoosed(mIconPath);
            }
        }
        else if(requestCode == CHOOSE_APPCODE){

            if(data != null){

                //更新控件内容
                mAppName.setText(data.getStringExtra("name"));
                mAppIcon.setImageDrawable(ImageUtils.Bytes2Drawable(data.getByteArrayExtra("icon")));
                //保存设置
                mMenuSavedIntent = data;

            }

        }

    }
}
