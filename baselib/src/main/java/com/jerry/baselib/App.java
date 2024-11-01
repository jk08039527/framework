package com.jerry.baselib;

import android.annotation.SuppressLint;
import android.app.Application;

public abstract class App extends Application {

    @SuppressLint("StaticFieldLeak")
    private static App sContext;
    private boolean isBackGround;

    public static App getInstance() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public boolean isBackGround() {
        return isBackGround;
    }

    public void setBackGround(final boolean backGround) {
        isBackGround = backGround;
    }
}
