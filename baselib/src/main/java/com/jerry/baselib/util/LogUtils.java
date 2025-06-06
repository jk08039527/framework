package com.jerry.baselib.util;

import android.annotation.SuppressLint;
import android.util.Log;

import com.jerry.baselib.BuildConfig;

/**
 * Author：OTMAGIC
 * WeChat：Longalei888
 * Date：2018/5/31
 * Signature:每一个Bug修改,每一次充分思考,都会是一种进步.
 * Describtion: log日志
 */

public class LogUtils {

    //debug 日志输入开关
    @SuppressLint("DefaultLocale")
    private static String getTAG() {
        StackTraceElement element = new Throwable().getStackTrace()[2];
        String TAG = "%s.%s(L:%d)";
        String className = element.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);
        TAG = String.format(TAG, className, element.getMethodName(), element.getLineNumber());
        return BuildConfig.LIBRARY_PACKAGE_NAME + ":" + TAG;
    }

    public static void v(String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(getTAG(), msg);
        }
        FileLog.getInstance().v(getTAG(), msg);
    }

    public static void v(String TAG, String msg) {
        if (BuildConfig.DEBUG) {
            LogUtils.v(msg);
        }
        FileLog.getInstance().v(TAG, msg);
    }

    public static void i(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(getTAG(), msg);
        }
        FileLog.getInstance().i(getTAG(), msg);
    }

    public static void i(String TAG, String msg) {
        if (BuildConfig.DEBUG) {
            LogUtils.i(msg);
        }
        FileLog.getInstance().i(TAG, msg);
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(getTAG(), msg);
        }
        FileLog.getInstance().d(getTAG(), msg);
    }

    public static void d(String TAG, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg);
        }
        FileLog.getInstance().d(TAG, msg);
    }

    public static void w(String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(getTAG(), msg);
        }
        FileLog.getInstance().w(getTAG(), msg);
    }

    public static void e(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(getTAG(), msg);
        }
        FileLog.getInstance().e(getTAG(), msg);
    }

    public static void e(String TAG, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg);
        }
        FileLog.getInstance().e(TAG, msg);
    }

}
