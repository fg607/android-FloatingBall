package com.hardwork.fg607.floatingball;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hardwork.fg607.floatingball.utils.BallFunctionDao;
import com.hardwork.fg607.floatingball.utils.FloatingBallUtils;

import java.io.IOException;
import java.util.ArrayList;

public class FuncKeySetupActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageView mIconImg;
    private TextView mTitle;
    private TextView mScene;
    private BallFunctionDao mBallFunctionDao;
    private ImageView mCheck1;
    private ImageView mCheck2;
    private ImageView mCheck3;
    private ImageView mCheck4;
    private ImageView mCheck5;
    private ImageView mCheck6;
    private RelativeLayout mImg1;
    private RelativeLayout mImg2;
    private RelativeLayout mImg3;
    private RelativeLayout mImg4;
    private RelativeLayout mImg5;
    private RelativeLayout mImg6;
    private RelativeLayout mTitleLayout;
    private RelativeLayout mSceneLayout;
    private ImageView mViewChoosed;
    private Bitmap mUserBitmap = null;
    private Button mNewIconButton;
    private EditText mEditText;
    private AlertDialog mEditDialog;
    private AlertDialog mSceneDialog;
    private ArrayList<String> mSceneList;
    private String mIconPath;
    private ArrayList<String> mKeyContent;
    private String mFileName;

    public static final int REQUEST_PICK = 345;
    public static final int REQUEST_CLIP = 346;
    public static final int KEY_SET_FAILED = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_func_key_setup);

        mIconImg = (ImageView) findViewById(R.id.icon);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mScene = (TextView) findViewById(R.id.tv_scene);
        mImg1 = (RelativeLayout) findViewById(R.id.img1);
        mImg2 = (RelativeLayout) findViewById(R.id.img2);
        mImg3 = (RelativeLayout) findViewById(R.id.img3);
        mImg4 = (RelativeLayout) findViewById(R.id.img4);
        mImg5 = (RelativeLayout) findViewById(R.id.img5);
        mImg6 = (RelativeLayout) findViewById(R.id.img6);
        mTitleLayout = (RelativeLayout) findViewById(R.id.title_layout);
        mSceneLayout = (RelativeLayout) findViewById(R.id.scene_layout);
        mNewIconButton = (Button) findViewById(R.id.iconedit);
        mImg1.setOnClickListener(this);
        mImg2.setOnClickListener(this);
        mImg3.setOnClickListener(this);
        mImg4.setOnClickListener(this);
        mImg5.setOnClickListener(this);
        mImg6.setOnClickListener(this);
        mTitleLayout.setOnClickListener(this);
        mSceneLayout.setOnClickListener(this);
        mNewIconButton.setOnClickListener(this);
        mCheck1 = (ImageView) findViewById(R.id.check1);
        mCheck2 = (ImageView) findViewById(R.id.check2);
        mCheck3 = (ImageView) findViewById(R.id.check3);
        mCheck4 = (ImageView) findViewById(R.id.check4);
        mCheck5 = (ImageView) findViewById(R.id.check5);
        mCheck6 = (ImageView) findViewById(R.id.check6);

        Intent intent = getIntent();
        String keyName = intent.getStringExtra("keyname");

        if(keyName != null) {
            this.setTitle(keyName+"设置");
            mBallFunctionDao = new BallFunctionDao(this);
            mKeyContent = mBallFunctionDao.findFuncKey(keyName);
            mIconImg.setImageBitmap(FloatingBallUtils.getBitmap(mKeyContent.get(0)));
            mViewChoosed =  setIconChoosed(mKeyContent.get(0));
            mTitle.setText(mKeyContent.get(1));
            mScene.setText(mKeyContent.get(2));
        }
        else {
            Intent intent1 =  new Intent(this,MainActivity.class);
            intent1.putExtra("keySetOk",false);
            this.setResult(KEY_SET_FAILED,intent1);
            finish();
        }
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

            if(mCheck6.getVisibility() == View.VISIBLE) {
            }
            //保存功能键信息
            mKeyContent.set(1,mTitle.getText().toString());
            mKeyContent.set(2,mScene.getText().toString());
            Intent intent = new Intent();
            intent.putStringArrayListExtra("keycontent",mKeyContent);
            setResult(MainActivity.SETUPKEY,intent);
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
            case R.id.title_layout:
                popupEditDialog();
                break;
            case R.id.scene_layout:
                popupSceneDialog();
                break;
            case R.id.iconedit:
                cropIcon();
                break;
            default:
                break;
        }

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
     * 弹出编辑标题对话框
     */
    public void popupEditDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("设置标题");
        View view = View.inflate(this, R.layout.edit_popup, null);

        mEditText = (EditText) view.findViewById(R.id.title);
        mEditText.setHint(mTitle.getText());
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2
        )});

        TextView  bt_ok = (TextView) view.findViewById(R.id.ok);
        TextView bt_cancel = (TextView) view.findViewById(R.id.cancel);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditText.getText().toString().trim();

                if (text != null) {
                    mTitle.setText(text);
                }
                mEditDialog.dismiss();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditDialog.dismiss();
            }
        });

        builder.setView(view);
        mEditDialog =  builder.show();

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

    }
}
