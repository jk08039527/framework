package com.jerry.baselib;

import android.content.Context;

public class App {

    private static Context sContext;
    private boolean isBackGround;

    public static Context getInstance() {
        return sContext;
    }

    public static void init(Context context) {
        sContext = context;
    }

    public boolean isBackGround() {
        return isBackGround;
    }

    public void setBackGround(final boolean backGround) {
        isBackGround = backGround;
    }
}
