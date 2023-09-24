package com.jerry.baselib;

import android.annotation.SuppressLint;
import android.content.Context;

public class App {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public static Context getInstance() {
        return sContext;
    }
    public static void init(Context context) {
        sContext = context;
    }
}
