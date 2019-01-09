package com.jerry.myframwork;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * Created by wzl on 2019/1/9.
 *
 * @Description
 */
public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Application mInstance;

    public static Application getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
