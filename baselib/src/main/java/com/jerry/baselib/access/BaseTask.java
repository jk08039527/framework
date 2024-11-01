
package com.jerry.baselib.access;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseLongArray;

import com.jerry.baselib.impl.EndCallback;
import com.jerry.baselib.impl.OnDataCallback;
import com.jerry.baselib.util.MathUtil;
import com.jerry.baselib.util.WeakHandler;

/**
 * @author Jerry
 * @createDate 1/11/21
 * @description 任务基类
 */
public abstract class BaseTask {

    protected static String PACKAGE_NAME;

    protected final BaseListenerService mService;
    protected final WeakHandler mWeakHandler;
    protected final SparseLongArray startTime = new SparseLongArray();
    protected boolean isPlaying;
    protected List<OnDataCallback<?>> mOnDataCallbacks;

    public BaseTask(final BaseListenerService listenerService, final WeakHandler weakHandler) {
        mService = listenerService;
        mWeakHandler = weakHandler;
    }

    /**
     * 分发任务
     */
    public abstract boolean dispatchTask(int taskId, EndCallback endCallback);

    public abstract ArrayList<String> getTaskUrl();

    public abstract void handleHtml(final String url, final String html);

    public void setIsPlaying(final boolean isPlaying) {
        this.isPlaying = isPlaying;
        mWeakHandler.removeCallbacksAndMessages(null);
    }

    protected long getShotTime() {
        return MathUtil.random(2000, 4000);
    }

    protected long getMiddleTime() {
        return MathUtil.random(5000, 10000);
    }


    protected long getLongTime() {
        return MathUtil.random(10000, 20000);
    }

    public void setOnDataCallbacks(final List<OnDataCallback<?>> onDataCallbacks) {
        mOnDataCallbacks = onDataCallbacks;
    }
}
