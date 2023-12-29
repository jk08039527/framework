package com.jerry.myframwork;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;

import com.jerry.baselib.App;

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
        App.init(this);
        startService(new Intent(this, ListenerService.class));
    }
}
