package com.jerry.myframwork;

import android.content.Intent;

import com.jerry.baselib.App;

import cn.bmob.v3.Bmob;

/**
 * Created by wzl on 2019/1/9.
 *
 * @Description
 */
public class MyApplication extends App {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, ListenerService.class));
        HistoryDataHelper.getInstance().init(null);
        Bmob.initialize(this, BuildConfig.AVOS_APP_ID);
    }
}
