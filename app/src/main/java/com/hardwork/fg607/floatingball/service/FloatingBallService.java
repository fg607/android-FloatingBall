package com.hardwork.fg607.floatingball.service;

/**
 * Created by fg607 on 15-8-20.
 *
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.hardwork.fg607.floatingball.MainActivity;
import com.hardwork.fg607.floatingball.R;
import com.hardwork.fg607.floatingball.utils.AnimatorUtils;
import com.hardwork.fg607.floatingball.utils.AppUtils;
import com.hardwork.fg607.floatingball.utils.BallFunctionDao;
import com.hardwork.fg607.floatingball.utils.FloatingBallUtils;
import com.ogaclejapan.arclayout.ArcLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.SharedPreferences.Editor;

public class FloatingBallService extends Service implements View.OnClickListener{

    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams mBallWmParams = null;
    private View mBallView;
    private View mMenuView;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private int mOldOffsetX, mOldOffsetY,mNewOffsetX,mNewOffsetY;
    private int mTag ;
    private FrameLayout mMenuLayout;
    private Button mFloatImage;
    private PopupWindow mPopWindow;
    private boolean mIsmoving = false;
    private boolean mCanmove = false;
    private Notification mNotification = null;
    public SharedPreferences sp;
    private boolean mIsAdd;
    private int mClickCount;
    public static final long CLICK_SPACING_TIME = 200;//双击间隔时间
    public static final long LONG_PRESS_TIME = 300;
    public static final int TRANSPARENT = 150;
    public static final int MIN_BALL_SIZE = 80;
    public static final int MAX_BALL_SIZE = 200;
    public static final int MENU_WINDOW_WIDTH = 500;
    public static final int MENU_WINDOW_HEIGHT = 800;
    private int floatBallSize = MIN_BALL_SIZE;
    private Handler mHandler;
    private LongPressedThread mLongPressedThread;
    private ClickPressedThread mClickPressedThread;
    private ShowPopMenuThread mShowPopMenuThread;
    private HidePopMenuThread mHidePopMenuThread;
    private long mPreClickTime;
    public int transparent;
    private ArcLayout mArcLayout;
    private Button mFab;
    private BallFunctionDao mBallFunctionDao;
    private ArrayList<String> mCurrentFuncList;
    private boolean mLongPressing;

    @Override
    public void onCreate() {
        super.onCreate();

        //生成存储工具
        sp = FloatingBallUtils.getSharedPreferences(this);
        mBallFunctionDao = new BallFunctionDao(this);

        //加载悬浮球布局
        mBallView = LayoutInflater.from(this).inflate(R.layout.floatball, null);
        mFloatImage = (Button)mBallView.findViewById(R.id.float_image);
        transparent = TRANSPARENT;
        mFloatImage.getBackground().setAlpha(transparent);


        mTag = 0;
        mIsAdd = false;
        mClickCount = 0;
        mHandler = new Handler();
        mPreClickTime = 0;

        //加载悬浮球功能
        loadFunction();


        //悬浮球动作不够6个退出
        if(mCurrentFuncList.size() < 6) {
            onDestroy();
        }

        //加载功能键
        setUpFloatMenuView();

        //读取悬浮球大小
        floatBallSize = sp.getInt("ballsize",MIN_BALL_SIZE);
        //加载悬浮球
        createFloatBallView();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //开启关闭自由移动
        String moveAction = intent.getStringExtra("canmove");

        if(moveAction != null) {

            switch (moveAction) {
                case "moveallowed":
                    mCanmove = true;
                    break;
                case "moveforbidden":
                    mCanmove = false;
                    break;
            }
        }

        //开启关闭悬浮球
        String showAction = intent.getStringExtra("ballstate");

        if(showAction != null) {

            switch (showAction) {
                case "showball":
                    showFloatBall();
                    break;
                case "closeball":
                    closeFloatBall();
                    break;

            }
        }

        //调整悬浮球大小
        int ballSizePer = intent.getIntExtra("ballsize",0);

        if(ballSizePer != 0) {

            floatBallSize = (int)(MIN_BALL_SIZE + (MAX_BALL_SIZE - MIN_BALL_SIZE) / 100 * ballSizePer);

            //保存悬浮球大小
            saveStates("ballsize",floatBallSize);
            updateFloatBall();
        }

        //重新加载悬浮球功能
        boolean isLoadFunction = intent.getBooleanExtra("loadfunction",false);

        if (isLoadFunction) {
            loadFunction();
        }

        //更新功能键图标
        boolean isUpdateMenuIcons = intent.getBooleanExtra("updatemenuicons",false);

        if(isUpdateMenuIcons)
        {
            updateMenuIcons();
            updateBallIcon();
        }

        //设置通知栏
        if(mNotification == null)
        {
            //设置通知栏
            Notification mNotification = new Notification(R.drawable.nor,
                    getString(R.string.app_name), System.currentTimeMillis());


            // mNotification.flags=Notification.FLAG_AUTO_CANCEL;用户点击清除能够清除通知


            //点击通知栏返回后台程序，默认新建
            Intent appIntent = new Intent(this, MainActivity.class);//为PlayerActivity组建创建Intent


            //ACTION_MAIN和CATEGORY_LAUNCHER设置启动组建PlayerActivity
            appIntent.setAction(Intent.ACTION_MAIN);
            appIntent.addCategory(Intent.CATEGORY_LAUNCHER);


            //设置启动模式为将后台PlayerActivity前台运行
            appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);


            PendingIntent pendingintent =PendingIntent.getActivity(this,0,appIntent,0);


            mNotification.setLatestEventInfo(this, "FloatBall", "Made By fg607",
                    pendingintent);
            startForeground(0x111, mNotification);//使Service处于前台，避免容易被清除
        }


        flags = START_STICKY;//设置START_STICKY标志，用于Service被杀后重启

        return super.onStartCommand(intent, flags, startId);
    }


/**
 * 窗口菜单初始化
 */
    private void setUpFloatMenuView(){

        mShowPopMenuThread = new ShowPopMenuThread();
        mHidePopMenuThread = new HidePopMenuThread();

        mMenuView = LayoutInflater.from(this).inflate(R.layout.popup, null);
        mMenuLayout = (FrameLayout) mMenuView.findViewById(R.id.menu_layout);
        updateMenuIcons();
        mArcLayout = (ArcLayout) mMenuView.findViewById(R.id.arc_layout);
        mFab = (Button) mMenuView.findViewById(R.id.fab);


        for (int i = 0, size = mArcLayout.getChildCount(); i < size; i++) {
            mArcLayout.getChildAt(i).setOnClickListener(this);
        }

        mFab.setOnClickListener(this);
    }

    /**
     * 显示功能键面板
     */
    private void showMenu() {

        mMenuLayout.setVisibility(View.VISIBLE);
        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = mArcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(mArcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }


    /**
     * 隐藏功能键面板
     */
    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = mArcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(mArcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mMenuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();

    }

    /**
     * 生成显示功能键动画
     * @param item
     * @return
     */
    private Animator createShowItemAnimator(View item) {

        float dx = mFab.getX() - item.getX();
        float dy = mFab.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    /**
     * 生成隐藏功能键动画
     * @param item
     * @return
     */
    private Animator createHideItemAnimator(final View item) {
        float dx = mFab.getX() - item.getX();
        float dy = mFab.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }

    /**
     * 弹出popmenu动画线程
     */
    public class ShowPopMenuThread implements Runnable{

        @Override
        public void run() {
            showMenu();
        }
    }

    /**
     * 隐藏popmenu动画线程
     */
    public class HidePopMenuThread implements Runnable{
        @Override
        public void run() {

            mPopWindow.dismiss();
        }
    }

    /**
     * 长按线程
     */
public class LongPressedThread implements Runnable{

    @Override

    public void run() {

        //长按悬浮球事件
        onFloatBallLongPressed();
        mClickCount = 0;

    }

}

    /**
     * 点击线程
     */
    public class ClickPressedThread implements Runnable{

        @Override

        public void run() {


            if(mClickCount == 1)
            {
                //单击悬浮球
               onFloatBallClick();
            }
            else if (mClickCount == 2)
            {
                //双击悬浮球
                onFloatBallDoubleClick();
            }
            mClickCount = 0;


        }

    }

    /**
     * 创建ＦloatBallView，并初始化显示参数
     */
    private void createFloatBallView() {

        //设置悬浮窗口参数
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mBallWmParams = new WindowManager.LayoutParams();
        mBallWmParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mBallWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mBallWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mBallWmParams.x = sp.getInt("ballWmParamsX",0);
        mBallWmParams.y = sp.getInt("ballWmParamsY",0);

        mBallWmParams.width = floatBallSize;
        mBallWmParams.height = floatBallSize;
        mBallWmParams.format = PixelFormat.RGBA_8888;


        //注册触摸事件监听器
        mFloatImage.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();
                if (mTag == 0) {
                    mOldOffsetX = mBallWmParams.x;
                    mOldOffsetY = mBallWmParams.y;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mClickCount++;
                        mPreClickTime = System.currentTimeMillis();

                        //移除双击检测线程
                        if (mClickPressedThread != null) {
                            mHandler.removeCallbacks(mClickPressedThread);
                        }
                        //启动长按线程
                        mLongPressedThread = new LongPressedThread();
                        mHandler.postDelayed(mLongPressedThread, LONG_PRESS_TIME);
                        mLongPressing = true;

                        mIsmoving = false;
                        mTouchStartX = (int) event.getX();
                        mTouchStartY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mIsmoving = true;
                        mTag = 1;
                        mBallWmParams.x += (int) (x - mTouchStartX) / 3;// 减小偏移量,防止过度抖动
                        mBallWmParams.y += (int) (y - mTouchStartY) / 3;// 减小偏移量,防止过度抖动

                        //滑动量大于50像素取消长按事件
                        if (Math.abs(mOldOffsetX - mBallWmParams.x) > 50 || Math.abs(mOldOffsetY - mBallWmParams.y) > 50) {
                            //取消注册的长按事件
                            mHandler.removeCallbacks(mLongPressedThread);
                            mLongPressing = false;
                        }

                        //更新悬浮球位置，保存位置
                        if (mCanmove) {
                            updateViewPosition();
                            saveStates("ballWmParamsX", mBallWmParams.x);
                            saveStates("ballWmParamsY", mBallWmParams.y);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mTouchStartX = mTouchStartY = 0;
                        mNewOffsetX = mBallWmParams.x;
                        mNewOffsetY = mBallWmParams.y;


                        // 滑动偏移量小于40像素，判定为点击悬浮球
                        if (Math.abs(mOldOffsetX - mNewOffsetX) <= 40 && Math.abs(mOldOffsetY - mNewOffsetY) <= 40) {


                            if (System.currentTimeMillis() - mPreClickTime <= LONG_PRESS_TIME) {

                                //取消注册的长按事件

                                mHandler.removeCallbacks(mLongPressedThread);

                                mClickPressedThread = new ClickPressedThread();
                                mHandler.postDelayed(mClickPressedThread, CLICK_SPACING_TIME);
                            }

                            onClearOffset();//清楚滑动偏移量
                        }
                        else if (mCanmove){

                            mClickCount = 0;
                        }

                        if (!mCanmove && !mLongPressing) {

                            //Y轴滑动偏移量大于40像素并且Y轴滑动偏移量比X轴偏移量多出20像素时判定为向上滑动
                            if ((mOldOffsetY - mNewOffsetY) - Math.abs(mOldOffsetX - mNewOffsetX) > 20 && (mOldOffsetY - mNewOffsetY) > 40) {

                                mClickCount = 0;
                                onFloatBallFlipUp();

                            }
                            //向下滑动
                            else if ((mNewOffsetY - mOldOffsetY) - Math.abs(mOldOffsetX - mNewOffsetX) > 20 && (mNewOffsetY - mOldOffsetY) > 40) {

                                mClickCount = 0;
                                onFloatBallFlipDown();

                            }
                            //向左滑动
                            else if ((mOldOffsetX - mNewOffsetX) - Math.abs(mOldOffsetY - mNewOffsetY) > 20 && (mOldOffsetX - mNewOffsetX) > 40) {

                                mClickCount = 0;
                                onFloatBallFlipLeft();

                            }
                            //向右滑动
                            else if ((mNewOffsetX - mOldOffsetX) - Math.abs(mOldOffsetY - mNewOffsetY) > 20 && (mNewOffsetX - mOldOffsetX) > 40) {

                                mClickCount = 0;
                                onFloatBallFlipRight();

                            }
                            onClearOffset();


                        }

                        mTag = 0;
                        break;
                }
                //如果拖动则返回false，否则返回true
                if (mIsmoving == false) {
                    return false;
                } else {
                    return true;
                }
            }

        });

    }

    /**
     * 将状态数据保存在sharepreferences
     * @param name
     * @param number
     */
    public void saveStates(String name,int number) {
        Editor editor = sp.edit();
        editor.putInt(name, number);
        editor.commit();

    }

    /**
     * 清空滑动位移量
     */
    private void onClearOffset() {
        mBallWmParams.x = mOldOffsetX;
        mBallWmParams.y = mOldOffsetY;
    }

    /**
     * 加载悬浮球功能
     */
    private void loadFunction() {

        String scene = null;
        String menuName = sp.getString("currentfunction",null);
        String func = sp.getString(menuName+"Func",null);

        if(menuName != null && func.equals("scene") ) {
            scene = sp.getString(menuName, null);
            if(scene != null) {
                mCurrentFuncList =  mBallFunctionDao.findFuncs(scene);

            }
        }



    }

    /**
     * 选择触发的功能
     */
    private  void chooseFunction(String action) {

        switch (action) {
            case "移动(固定)悬浮球":
                mCanmove = !mCanmove;

                break;
            case "返回键":
                FloatingBallUtils.keyBack();

                break;
            case "Home键":

                FloatingBallUtils.keyHome();
                break;
            case "最近任务键":

               FloatingBallUtils.openRecnetTask();
                break;
            case "电源键":

                FloatingBallUtils.pressPower();
                break;
            case "菜单键":

                FloatingBallUtils.keyMenu();
                break;
            case "重启":

               FloatingBallUtils.reboot();
                break;
            case "关机":

                FloatingBallUtils.shutdown();
                break;
            case "音量键加":

               FloatingBallUtils.volumeUp();
                break;
            case "音量键减":

                FloatingBallUtils.vloumeDown();
                break;
            default:
                break;

        }



    }

    /**
     * 长按悬浮球
     */
    private void onFloatBallLongPressed() {

        popUpMenu();
    }

    /**
     * 点击悬浮球
     */

    private void  onFloatBallClick(){

        chooseFunction(mCurrentFuncList.get(0));
    }

    /**
     * 双击悬浮球
     */
    private void onFloatBallDoubleClick(){

        chooseFunction(mCurrentFuncList.get(1));

    }

    /**
     * 向上滑动悬浮球
     */
    private void onFloatBallFlipUp() {

        chooseFunction(mCurrentFuncList.get(2));
    }

    /**
     * 向下滑动悬浮球
     */
    private void onFloatBallFlipDown() {
        chooseFunction(mCurrentFuncList.get(3));
    }

    /**
     * 向左滑动悬浮球
     */
    private void onFloatBallFlipLeft() {

        chooseFunction(mCurrentFuncList.get(4));

    }

    /**
     * 向右滑动悬浮球
     */
    private void onFloatBallFlipRight() {

        chooseFunction(mCurrentFuncList.get(5));
    }


    /**
     * 弹出功能菜单
     */
    private  void popUpMenu() {

        mPopWindow = new PopupWindow(mMenuView, MENU_WINDOW_WIDTH, MENU_WINDOW_HEIGHT);
        int offsetX = -(MENU_WINDOW_WIDTH-mBallWmParams.width);
        int offsetY = -(MENU_WINDOW_HEIGHT/2+mBallWmParams.height/2);

        //功能键面板位于悬浮球左边
        mPopWindow.showAsDropDown(mBallView, offsetX, offsetY);
      //  mPopWindow.setFocusable(false);
       // mPopWindow.setOutsideTouchable(true);
        //位于键盘之上
        mPopWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopWindow.update();

        //弹出面板后延迟100ms开始播放功能键显示动画
        mHandler.postDelayed(mShowPopMenuThread, 100);
    }


    /**
     * 更新悬浮球的显示位置
     */
    private void updateViewPosition() {

        mWindowManager.updateViewLayout(mBallView, mBallWmParams);
    }

    /**
     * 更新功能键图标
     */
    public void updateMenuIcons()
    {
        CircleImageView menuA = (CircleImageView) mMenuView.findViewById(R.id.menuA);
        CircleImageView menuB = (CircleImageView) mMenuView.findViewById(R.id.menuB);
        CircleImageView menuC = (CircleImageView) mMenuView.findViewById(R.id.menuC);
        CircleImageView menuD = (CircleImageView) mMenuView.findViewById(R.id.menuD);
        CircleImageView menuE = (CircleImageView) mMenuView.findViewById(R.id.menuE);


        updateViewIcon(menuA,"menuA");
        updateViewIcon(menuB,"menuB");
        updateViewIcon(menuC,"menuC");
        updateViewIcon(menuD,"menuD");
        updateViewIcon(menuE, "menuE");

        //menuA.setText(mBallFunctionDao.findFuncKey("menuA").get(1));
       // menuB.setText(mBallFunctionDao.findFuncKey("menuB").get(1));
        //menuC.setText(mBallFunctionDao.findFuncKey("menuC").get(1));
       // menuD.setText(mBallFunctionDao.findFuncKey("menuD").get(1));
       // menuE.setText(mBallFunctionDao.findFuncKey("menuE").get(1));


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 处理点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab:
                closePopupWindow();
                break;
            case R.id.menuA:
                menuClick("menuA");
                break;
            case R.id.menuB:
                menuClick("menuB");
                break;
            case R.id.menuC:
                menuClick("menuC");
                break;
            case R.id.menuD:
                menuClick("menuD");
                break;
            case R.id.menuE:
                menuClick("menuE");
                break;
            default:
                closePopupWindow();
                break;
        }

    }

    /**
     * 点击功能键
     * @param menuName
     */
    private void menuClick(String menuName) {
        String func = sp.getString(menuName+"Func",null);
        if(func.equals("scene")){

            FloatingBallUtils.saveState(sp,"currentfunction",menuName);
            loadFunction();
            updateBallIcon();
        }
        else if(func.equals("app")) {

            //打开app
            ArrayList<String> AppList = mBallFunctionDao.findAppKey(menuName);

            if (AppList.size() > 0) {

                AppUtils.startApplication(getApplicationContext(),AppList.get(2));
            }
        }

        closePopupWindow();

    }

    /**
     * 更新悬浮球图标
      */
    private void updateBallIcon() {
        String menuName = sp.getString("currentfunction", null);
        updateViewIcon(mFloatImage,menuName);
        mFloatImage.getBackground().setAlpha(transparent);

    }

    /**
     * 更新功能键图标
     * @param view
     * @param menuName
     */
    private void updateViewIcon(View view,String menuName) {
        String func = sp.getString(menuName + "Func", null);
        String icon = null;
        if(func.equals("scene")){
            icon = mBallFunctionDao.findFuncKey(menuName).get(0);
        }
        else if(func.equals("app")){

            icon = mBallFunctionDao.findAppKey(menuName).get(1);
        }

        if(icon != null){

            Bitmap bitmap = FloatingBallUtils.getBitmap(icon);

            if(bitmap != null){

                if(view instanceof CircleImageView){

                   CircleImageView circleImageView = (CircleImageView)view;
                    circleImageView.setImageDrawable(new BitmapDrawable(bitmap));
                }
                else {
                    view.setBackgroundDrawable(new BitmapDrawable(bitmap));
                }

            }

        }

    }

    /**
     * 关闭功能键面板
     */
    private  void closePopupWindow() {
        if(mPopWindow!=null && mPopWindow.isShowing()) {

            hideMenu();
            mHandler.postDelayed(mHidePopMenuThread,500);
        }
    }

    /**
     * 关闭悬浮球
     */
    public void closeFloatBall() {
        if (mIsAdd) {
            mWindowManager.removeView(mBallView);
            mIsAdd = !mIsAdd;
        }


    }

    /**
     * 显示悬浮球
     */
    public void  showFloatBall() {

        if(!mIsAdd) {
            mWindowManager.addView(mBallView, mBallWmParams);
            mIsAdd = !mIsAdd;
        }

    }

    /**
     * 更新悬浮球
     */
    public void updateFloatBall(){

        mBallWmParams.width = floatBallSize;
        mBallWmParams.height = floatBallSize;
        mWindowManager.updateViewLayout(mBallView, mBallWmParams);
    }

    @Override
    public void onDestroy() {

        closeFloatBall();

        //销毁时停止前台
        stopForeground(true);
        super.onDestroy();
    }
}
