package com.jerry.baselib.access;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import com.jerry.baselib.Key;
import com.jerry.baselib.flow.FloatWindowManager;
import com.jerry.baselib.impl.OnDataCallback;
import com.jerry.baselib.impl.OnItemClickListener;
import com.jerry.baselib.util.ActionCode;
import com.jerry.baselib.util.MathUtil;
import com.jerry.baselib.util.WeakHandler;
import com.jerry.baselib.R;

/**
 *
 * @author cxk
 * @date 2017/2/4. email:471497226@qq
 * <p>
 * 获取即时微信聊天记录服务类
 */
public abstract class BaseListenerService extends Service implements OnItemClickListener<String> {

    protected static BaseListenerService instance;
    public boolean isPlaying;
    protected int errorCount;
    protected String packageName;
    protected BaseTask currentTask;

    public static BaseListenerService getInstance() {
        return instance;
    }

    public WeakHandler mWeakHandler = new WeakHandler(new Callback() {
        @Override
        public boolean handleMessage(@NonNull final Message message) {
            return currentTask.dispatchTask(message.what, null);
        }
    });

    @SuppressLint("CheckResult")
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FloatWindowManager.getInstance().init(this);
    }

    /**
     * 开启任务
     */
    public void start(int start) {
        startScript();
        mWeakHandler.sendEmptyMessage(start);
        Bundle bundle = new Bundle();
        bundle.putInt(Key.DATA, ActionCode.SERVICE_START);
        EventBus.getDefault().post(bundle);
    }

    /**
     * 结束任务
     */
    public void stop() {
        stopScript();
        mWeakHandler.removeCallbacksAndMessages(null);
        Bundle bundle = new Bundle();
        bundle.putInt(Key.DATA, ActionCode.SERVICE_STOP);
        EventBus.getDefault().post(bundle);
    }

    /**
     * 震动发声提示
     */
    public void giveNotice() {
        Vibrator vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(new long[]{500, 1000, 500, 1000}, -1);
        }
        SoundPool mSoundPool;
        mSoundPool = new SoundPool.Builder().setMaxStreams(10).build();
        int mWinMusic = mSoundPool.load(this, R.raw.fadein, 1);
        mSoundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> soundPool.play(mWinMusic, 0.6F, 0.6F, 0, 0, 1.0F));
    }

    public abstract ArrayList<String> getTaskUrl();

    public abstract void handleHtml(final String url, final String html);

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return new MyBinder();
    }

    public void startScript() {
        errorCount = 0;
        isPlaying = true;
        if (currentTask != null) {
            currentTask.setIsPlaying(true);
        }
        FloatWindowManager.getInstance().startScript();
    }

    public void stopScript() {
        errorCount = 0;
        isPlaying = false;
        if (currentTask != null) {
            currentTask.setIsPlaying(false);
        }
        FloatWindowManager.getInstance().stopScript();
    }

    public long getShotTime() {
        return MathUtil.random(2000, 4000);
    }

    public long getShotMiddleTime() {
        return MathUtil.random(3000, 5000);
    }

    public long getMiddleTime() {
        return MathUtil.random(5000, 10000);
    }

    public long getLongTime() {
        return MathUtil.random(10000, 20000);
    }

    public void registerDataCallBack(final OnDataCallback<?> onDataCallback) {
        if (currentTask == null) {
            onDataCallback.onDataCallback(null);
        } else {
            currentTask.registerDataCallBack(onDataCallback);
        }
    }

    public void unregisterDataCallBack(final OnDataCallback<?> onDataCallback){
        if (currentTask==null) {
            onDataCallback.onDataCallback(null);
        }else {
            currentTask.unregisterDataCallBack(onDataCallback);
        }
    }

    public class ListenCallback implements Callback {

        @Override
        public boolean handleMessage(@NotNull final Message msg) {
            if (!isPlaying && msg.what < 800) {
                return false;
            }
            return false;
        }
    }

    public class MyBinder extends Binder {

        public BaseListenerService getService() {
            return BaseListenerService.this;
        }
    }
}