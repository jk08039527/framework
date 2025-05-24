package com.jerry.baselib.access;

import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import com.jerry.baselib.BuildConfig;
import com.jerry.baselib.Key;
import com.jerry.baselib.R;
import com.jerry.baselib.flow.FloatWindowManager;
import com.jerry.baselib.impl.EndCallback;
import com.jerry.baselib.impl.OnDataCallback;
import com.jerry.baselib.util.ActionCode;
import com.jerry.baselib.util.AppUtils;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.MathUtil;
import com.jerry.baselib.util.ToastUtil;
import com.jerry.baselib.util.WeakHandler;

/**
 * @author cxk
 * @date 2017/2/4. email:471497226@qq
 * <p>
 * 获取即时微信聊天记录服务类
 */
public abstract class BaseListenerService extends AccessibilityService {

    private static final String NOTIFICATION_CHANNEL_ID = BuildConfig.LIBRARY_PACKAGE_NAME + ".Foreground";
    private static final int NOTIFICATION_FOREGROUND_ID = 9527;
    protected static BaseListenerService instance;
    private final List<OnAccessibilityEventListener> mOnAccessibilityEventListeners = new ArrayList<>();
    protected GlobalActionAutomator mGlobalActionAutomator;
    public boolean isPlaying;
    protected int errorCount;
    protected BaseTask currentTask;
    public static int mWidth;
    public static int mHeight;
    protected final List<OnDataCallback<?>> mOnDataCallbacks = new ArrayList<>();

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getString(R.string.channel_description));
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID).setContentTitle(
                    getString(R.string.notification_foreground_title)).setContentText(getString(R.string.notification_foreground_title))
                .setSmallIcon(R.drawable.idlefish_ic_launcher);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 及以上版本的处理
                startForeground(NOTIFICATION_FOREGROUND_ID, builder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
            } else {
                // 较低版本的处理
                startForeground(NOTIFICATION_FOREGROUND_ID, builder.build());
            }
        }
        mWidth = getResources().getDisplayMetrics().widthPixels;
        mHeight = getResources().getDisplayMetrics().heightPixels;
        mGlobalActionAutomator = new GlobalActionAutomator(this);
        FloatWindowManager.getInstance().show();
    }

    /**
     * 开启任务
     */
    public boolean start(int start) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null && startScript()) {
            mWeakHandler.sendEmptyMessage(start);
            return true;
        }
        if (nodeInfo == null){
            ToastUtil.showShortText("需要开启辅助");
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return false;
    }

    /**
     * 结束任务
     */
    public void stop() {
        stopScript();
        mWeakHandler.removeCallbacksAndMessages(null);
    }

    public abstract ArrayList<String> getTaskUrl();

    public abstract void handleHtml(final String url, final String html);

    public boolean startScript() {
        if (isPlaying) {
            ToastUtil.showShortText("任务已启动，请先暂停任务");
            return false;
        }
        errorCount = 0;
        isPlaying = true;
        if (currentTask != null) {
            currentTask.setIsPlaying(true);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Key.DATA, ActionCode.SERVICE_START);
        EventBus.getDefault().post(bundle);
        FloatWindowManager.getInstance().startScript();
        return true;
    }

    public void stopScript() {
        errorCount = 0;
        isPlaying = false;
        if (currentTask != null) {
            currentTask.setIsPlaying(false);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Key.DATA, ActionCode.SERVICE_STOP);
        EventBus.getDefault().post(bundle);
        FloatWindowManager.getInstance().stopScript();
    }

    public long getSshotTime() {
        return MathUtil.random(800, 1000);
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

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        for (OnAccessibilityEventListener onAccessibilityEventListener : mOnAccessibilityEventListeners) {
            onAccessibilityEventListener.onAccessibilityEvent(accessibilityEvent);
        }
    }

    public void addOnAccessibilityEventListener(OnAccessibilityEventListener accessibilityEventListener) {
        mOnAccessibilityEventListeners.add(accessibilityEventListener);
    }

    public void removeOnAccessibilityEventListener(OnAccessibilityEventListener accessibilityEventListener) {
        mOnAccessibilityEventListeners.remove(accessibilityEventListener);
    }

    /**
     * 判断是否为首页
     */
    public boolean hasNode(String id) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> indicators = rootNode.findAccessibilityNodeInfosByViewId(id);
        return !CollectionUtils.isEmpty(indicators);
    }

    /**
     * 判断是否为首页
     */
    public boolean hasText(String text) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> indicators = rootNode.findAccessibilityNodeInfosByText(text);
        return !CollectionUtils.isEmpty(indicators);
    }

    /**
     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */
    @Override
    public void onInterrupt() {
        ToastUtil.showShortText("我快被终结了啊-----");
    }

    public String getNodeText(AccessibilityNodeInfo root) {
        CharSequence txt = root.getText();
        if (txt == null) {
            txt = Key.NIL;
        }
        return txt.toString();
    }

    public boolean input(String id, String text) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            List<AccessibilityNodeInfo> inputs = root.findAccessibilityNodeInfosByViewId(id);
            if (!CollectionUtils.isEmpty(inputs)) {
                AccessibilityNodeInfo node = inputs.get(0);
                return input(node, text);

            }
        }
        return false;
    }

    public boolean input(AccessibilityNodeInfo node, String text) {
        try {
            //粘贴板
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            return true;
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return false;
    }

    public String getNodeText(AccessibilityNodeInfo root, String id) {
        List<AccessibilityNodeInfo> inputs = root.findAccessibilityNodeInfosByViewId(id);
        if (!CollectionUtils.isEmpty(inputs)) {
            AccessibilityNodeInfo node = inputs.get(inputs.size() - 1);
            CharSequence txt = node.getText();
            if (txt == null) {
                txt = Key.NIL;
            }
            return txt.toString();
        }
        return Key.NIL;
    }

    public boolean back() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    @SuppressLint("DefaultLocale")
    public boolean exeSwip(int startX, int startY, int endX, int endY) {
        try {
            if (mGlobalActionAutomator == null) {
                mGlobalActionAutomator = new GlobalActionAutomator(this);
            }
            return mGlobalActionAutomator.swipe(startX, startY, endX, endY, 800);
        } catch (Throwable e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public boolean exeLongClick(AccessibilityNodeInfo target) {
        Rect rect = new Rect();
        target.getBoundsInScreen(rect);
        int x = (rect.left + rect.right) >> 1;
        int y = (rect.top + rect.bottom) >> 1;
        return exeLongClick(x, y);
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClickById(String id, int index) {
        AccessibilityNodeInfo newRootNode = getRootInActiveWindow();
        if (newRootNode != null) {
            List<AccessibilityNodeInfo> nodes = newRootNode.findAccessibilityNodeInfosByViewId(id);
            if (nodes != null && nodes.size() > index) {
                return exeClick(nodes.get(index));
            }
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClickById4Last(String id) {
        AccessibilityNodeInfo newRootNode = getRootInActiveWindow();
        if (newRootNode != null) {
            List<AccessibilityNodeInfo> nodes = newRootNode.findAccessibilityNodeInfosByViewId(id);
            if (!CollectionUtils.isEmpty(nodes)) {
                return exeClick(nodes.get(nodes.size()-1));
            }
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClick(AccessibilityNodeInfo target) {
        Rect rect = new Rect();
        target.getBoundsInScreen(rect);
        int x = (rect.left + rect.right) >> 1;
        int y = (rect.top + rect.bottom) >> 1;
        return exeClick(x, y);
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClickText(String text) {
        AccessibilityNodeInfo newRootNode = getRootInActiveWindow();
        if (newRootNode != null) {
            List<AccessibilityNodeInfo> nodes = newRootNode.findAccessibilityNodeInfosByText(text);
            if (!CollectionUtils.isEmpty(nodes)) {
                for (AccessibilityNodeInfo node : nodes) {
                    CharSequence txt = node.getText();
                    if (txt == null) {
                        txt = Key.NIL;
                    }
                    String nodeText = txt.toString();
                    if (text.equals(nodeText)) {
                        return exeClick(node);
                    }
                }
            }
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public void exeClickTexts(List<String> strs, EndCallback endCallback) {
        if (CollectionUtils.isEmpty(strs)) {
            endCallback.onEnd(false);
        } else {
            exeClickTexts(strs, 0, endCallback);
        }
    }

    @SuppressLint("DefaultLocale")
    private void exeClickTexts(List<String> strs, int index, EndCallback endCallback) {
        if (errorCount > 3) {
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        if (CollectionUtils.isItemInCollection(index, strs)) {
            String str = strs.get(index);
            if (exeClickText(str)) {
                mWeakHandler.postDelayed(() -> exeClickTexts(strs, index + 1, endCallback), getSshotTime());
            } else {
                errorCount++;
                mWeakHandler.postDelayed(() -> exeClickTexts(strs, index, endCallback), getSshotTime());
            }
            return;
        }
        errorCount = 0;
        mWeakHandler.postDelayed(() -> endCallback.onEnd(true), getSshotTime());
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClick(int x, int y) {
        try {
            if (mGlobalActionAutomator == null) {
                mGlobalActionAutomator = new GlobalActionAutomator(this);
            }
            return mGlobalActionAutomator.click(x, y);
        } catch (Throwable e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public boolean exeLongClick(int x, int y) {
        if (mGlobalActionAutomator == null) {
            mGlobalActionAutomator = new GlobalActionAutomator(this);
        }
        try {
            if (!mGlobalActionAutomator.longClick(x, y)) {
                ToastUtil.showShortText("辅助停止喽 重启试试");
                return false;
            }
        } catch (Throwable e) {
            LogUtils.e(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public boolean registerDataCallBack(final OnDataCallback<?> onDataCallback) {
        if (mOnDataCallbacks.contains(onDataCallback)) {
            LogUtils.w("onDataCallback already registered");
            return false;
        }
        return mOnDataCallbacks.add(onDataCallback);
    }

    public boolean unregisterDataCallBack(final OnDataCallback<?> onDataCallback) {
        return mOnDataCallbacks.remove(onDataCallback);
    }

    public interface OnAccessibilityEventListener {

        void onAccessibilityEvent(final AccessibilityEvent accessibilityEvent);
    }
}