package com.hardwork.fg607.floatingball.utils;

/**
 * Created by fg607 on 15-8-27.
 */
public class ChooseKeyItem {

    private int mDrawableId;
    private String mKey;
    private boolean mIsChecked;

    public ChooseKeyItem(int mDrawableId, String key,boolean mIsChecked) {
        this.mDrawableId = mDrawableId;
        mKey = key;
        this.mIsChecked = mIsChecked;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public boolean getIsChecked() {
        return mIsChecked;
    }

    public void setIsChecked(boolean mIsChecked) {
        this.mIsChecked = mIsChecked;
    }

    public int getDrawableId() {
        return mDrawableId;
    }

    public void setDrawable(int mDrawableId) {
        this.mDrawableId = mDrawableId;
    }
}
