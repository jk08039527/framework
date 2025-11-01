package com.jerry.baselib;

import android.app.Application;
import android.content.Context;

public abstract class App extends Application {

    private static App sContext;
    private boolean isBackGround;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getInstance() {
        return sContext;
    }

    public boolean isBackGround() {
        return isBackGround;
    }

    public void setBackGround(final boolean backGround) {
        isBackGround = backGround;
    }
}
