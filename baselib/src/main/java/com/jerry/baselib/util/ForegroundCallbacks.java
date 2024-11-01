package com.jerry.baselib.util;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import com.jerry.baselib.App;

/**
 * Created by th on 2017/7/18. 类说明:监听应用前后台切换
 */
public class ForegroundCallbacks implements ActivityLifecycleCallbacks {

    /**
     * 当前已经启动的activity数量
     */
    private int mStartCount;
    private WeakReference<Activity> mActivityWeakReference;

    @Override
    public void onActivityStarted(@NotNull Activity activity) {
        if (mStartCount == 0) {
            LogUtils.d("================================    切到到前台    ================================");
            Activity lastActivity = null;
            if (mActivityWeakReference != null) {
                lastActivity = mActivityWeakReference.get();
            }
            if (lastActivity instanceof ForegroundListener) {
                LogUtils.d("================================    onForeground exec    ================================");
                ((ForegroundListener) lastActivity).onForeground();
            }
            App.getInstance().setBackGround(false);
        }
        mStartCount++;
    }

    @Override
    public void onActivityStopped(@NotNull Activity activity) {
        mStartCount--;
        if (mStartCount == 0) {
            LogUtils.d("================================    切换到后台    ================================");
            if (activity instanceof ForegroundListener) {
                ((ForegroundListener) activity).onBackground();
            }
            mActivityWeakReference = new WeakReference<>(activity);
            App.getInstance().setBackGround(true);
        }
    }

    @Override
    public void onActivityCreated(@NotNull Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityResumed(@NotNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NotNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NotNull Activity activity, @NotNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NotNull Activity activity) {
        if (mStartCount == 0 && mActivityWeakReference != null) {
            LogUtils.d("================================    exit app    ================================");
            mActivityWeakReference.clear();
        }
    }
}
