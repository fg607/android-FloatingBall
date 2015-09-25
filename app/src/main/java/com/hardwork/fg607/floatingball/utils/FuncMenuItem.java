package com.hardwork.fg607.floatingball.utils;

import android.graphics.Bitmap;

/**
 * Created by fg607 on 15-8-29.
 */
public class FuncMenuItem {

    private String mFuncKeyName;
    private Bitmap mFuncIcon;
    private String mFuncText;
    private String mFuncScene;

    public FuncMenuItem(String mFuncKeyName, Bitmap mFuncIcon, String mFuncText, String mFuncScene) {
        this.mFuncKeyName = mFuncKeyName;
        this.mFuncIcon = mFuncIcon;
        this.mFuncText = mFuncText;
        this.mFuncScene = mFuncScene;
    }

    public String getFuncKeyName() {
        return mFuncKeyName;
    }

    public void setFuncKeyName(String mFuncKeyName) {
        this.mFuncKeyName = mFuncKeyName;
    }

    public Bitmap getFuncIcon() {
        return mFuncIcon;
    }

    public void setFuncIcon(Bitmap mFuncIcon) {
        this.mFuncIcon = mFuncIcon;
    }

    public String getFuncText() {
        return mFuncText;
    }

    public void setFuncText(String mFuncText) {
        this.mFuncText = mFuncText;
    }

    public String getFuncScene() {
        return mFuncScene;
    }

    public void setFuncScene(String mFuncScene) {
        this.mFuncScene = mFuncScene;
    }
}

