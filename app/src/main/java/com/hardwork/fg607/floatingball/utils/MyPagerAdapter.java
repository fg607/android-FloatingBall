package com.hardwork.fg607.floatingball.utils;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MyPagerAdapter extends PagerAdapter {

	private List<View> mListView;
	
	public MyPagerAdapter(List<View> mListView) {
		super();
		this.mListView = mListView;
	}


    //销毁position位置的界面
    public void destroyItem(View arg0, int arg1, Object arg2) {
        // TODO Auto-generated method stub
        ((ViewGroup)arg0).removeView(mListView.get(arg1));
    }

    @Override
    public void finishUpdate(View arg0) {
        // TODO Auto-generated method stub
       
    }

    ////获取当前窗体界面数
    public int getCount() {
        // TODO Auto-generated method stub
        return mListView.size();
    }
  //初始化position位置的界面
    @Override
    public Object instantiateItem(View arg0, int arg1) {
        // TODO Auto-generated method stub
        ((ViewGroup)arg0).addView(mListView.get(arg1), 0);
        return mListView.get(arg1);
    }

 // 判断是否由对象生成界面
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0==(arg1);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
        // TODO Auto-generated method stub
       
    }

    @Override
    public Parcelable saveState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
        // TODO Auto-generated method stub
       
    }
   
}

