package com.jerry.baselib.access;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Service;
import android.app.UiAutomation;
import android.app.UiAutomation.OnAccessibilityEventListener;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Vibrator;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import com.jerry.baselib.Key;
import com.jerry.baselib.flow.FloatWindowManager;
import com.jerry.baselib.impl.OnDataCallback;
import com.jerry.baselib.impl.OnItemClickListener;
import com.jerry.baselib.util.ActionCode;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.util.FileUtil;
import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.MathUtil;
import com.jerry.baselib.util.ToastUtil;
import com.jerry.baselib.util.WeakHandler;
import com.jerry.baselib.R;

/**
 * @author cxk
 * @date 2017/2/4. email:471497226@qq
 * <p>
 * 获取即时微信聊天记录服务类
 */
public abstract class BaseListenerService extends AccessibilityService implements OnItemClickListener<String> {

    protected static BaseListenerService instance;
    private final List<OnAccessibilityEventListener> mOnAccessibilityEventListeners = new ArrayList<>();
    protected GlobalActionAutomator mGlobalActionAutomator;
    public boolean isPlaying;
    protected int errorCount;
    protected String packageName;
    protected BaseTask currentTask;
    public static int mWidth;
    public static int mHeight;

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
        mGlobalActionAutomator = new GlobalActionAutomator(this);
        mWidth = getResources().getDisplayMetrics().widthPixels;
        mHeight = getResources().getDisplayMetrics().heightPixels;
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
    protected boolean hasText(String text) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> indicators = rootNode.findAccessibilityNodeInfosByText(text);
        return !CollectionUtils.isEmpty(indicators);
    }

    protected void getllText(final AccessibilityNodeInfo rootNode, Set<String> list) {
        if (rootNode.getChildCount() == 0) {
            CharSequence charSequence = rootNode.getContentDescription();
            if (charSequence != null) {
                list.add(charSequence.toString());
            }
            return;
        }
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if (node != null) {
                getllText(node, list);
            }
        }
    }

    /**
     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */
    @Override
    public void onInterrupt() {
        ToastUtil.showShortText("我快被终结了啊-----");
    }

    public void selectPicker(String id, String text, int x, int enter, EndCallback endCallback) {
        if (errorCount > 6) {
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            Rect rect = new Rect();
            root.getBoundsInScreen(rect);
            int up = (int) (rect.top * 0.9 + rect.bottom * 0.1);
            int down = (int) (rect.top * 0.4 + rect.bottom * 0.6);
            List<AccessibilityNodeInfo> pickers = root.findAccessibilityNodeInfosByViewId(packageName + id);
            if (!CollectionUtils.isEmpty(pickers)) {
                AccessibilityNodeInfo picker = pickers.get(0);
                if (enter == 0) {
                    exeSwip(x, up, x, down);
                } else {
                    String pickerText;
                    if (enter == 1 && picker.getChildCount() == 2) {
                        pickerText = getNodeText(picker.getChild(0));
                    } else {
                        pickerText = getNodeText(picker.getChild(1));
                    }
                    if (text.equals(pickerText)) {
                        errorCount = 0;
                        endCallback.onEnd(true);
                        return;
                    }
                    errorCount++;
                    exeClick(x, down);
                }
            }
        }
        this.mWeakHandler.postDelayed(() -> selectPicker(id, text, x, enter + 1, endCallback), getShotTime());
    }

    public void swipToClickText(String text, EndCallback endCallback) {
        if (errorCount > 3) {
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        if ((exeClickText(text, 0, (int) (mHeight * 0.1), mWidth, (int) (mHeight * 0.9)))) {
            errorCount = 0;
            this.mWeakHandler.postDelayed(() -> endCallback.onEnd(true), getShotTime());
            return;
        }
        errorCount++;
        exeSwip(mWidth >> 2, (int) (mHeight * 0.75), mWidth >> 2, mHeight >> 2);
        this.mWeakHandler.postDelayed(() -> swipToClickText(text, endCallback), getShotTime());
    }

    public void swipToLongClickText(List<String> texts, EndCallback endCallback) {
        if (errorCount > 3) {
            errorCount = 0;
            endCallback.onEnd(false);
            return;
        }
        AccessibilityNodeInfo newRootNode = getRootInActiveWindow();
        if (newRootNode != null) {
            for (String text : texts) {
                List<AccessibilityNodeInfo> nodes = newRootNode.findAccessibilityNodeInfosByText(text);
                if (nodes.size() > 0) {
                    if (exeLongClick(nodes.get(0))) {
                        errorCount = 0;
                        this.mWeakHandler.postDelayed(() -> endCallback.onEnd(true), getShotTime());
                    }
                    return;
                }
            }
        }
        errorCount++;
        exeSwip(mWidth >> 2, (int) (mHeight * 0.75), mWidth >> 2, (int) (mHeight * 0.25));
        this.mWeakHandler.postDelayed(() -> swipToLongClickText(texts, endCallback), getShotTime());
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
            List<AccessibilityNodeInfo> inputs = root.findAccessibilityNodeInfosByViewId(packageName + id);
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
        List<AccessibilityNodeInfo> inputs = root.findAccessibilityNodeInfosByViewId(packageName + id);
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

    public void back() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    @SuppressLint("DefaultLocale")
    public void exeSwip(int startX, int startY, int endX, int endY) {
        if (mGlobalActionAutomator == null) {
            mGlobalActionAutomator = new GlobalActionAutomator(this);
        }
        try {
            if (!mGlobalActionAutomator.swipe(startX, startY, endX, endY, 800)) {
                ToastUtil.showShortText("辅助停止喽 重启试试");
            }
        } catch (Throwable e) {
            LogUtils.e(e.getLocalizedMessage());
        }
    }

    @SuppressLint("DefaultLocale")
    public void exeClicks(List<Point> points, int index, OnDataCallback<Integer> callBack) {
        if (CollectionUtils.isItemInCollection(index, points)) {
            Point point = points.get(index);
            if (mGlobalActionAutomator == null) {
                mGlobalActionAutomator = new GlobalActionAutomator(this);
            }
            try {
                if (!mGlobalActionAutomator.click(point.x, point.y)) {
                    ToastUtil.showShortText("辅助停止喽 重启试试");
                }
            } catch (Throwable e) {
                LogUtils.e(e.getLocalizedMessage());
            }
            mWeakHandler.postDelayed(() -> exeClicks(points, index + 1, callBack), getSshotTime());
        } else {
            if (callBack != null) {
                callBack.onDataCallback(index);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    public boolean exeLongClick(AccessibilityNodeInfo target) {
        Rect rect = new Rect();
        target.getBoundsInScreen(rect);
        int x = (rect.left + rect.right) >> 1;
        int y = (rect.top + rect.bottom) >> 1;
        if (rect.left >= 0 && rect.right <= mWidth && rect.top >= 0 && rect.bottom <= mHeight) {
            return exeLongClick(x, y);
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

    @SuppressLint("DefaultLocale")
    public void exeClick(int x, int y) {
        if (mGlobalActionAutomator == null) {
            mGlobalActionAutomator = new GlobalActionAutomator(this);
        }
        try {
            if (!mGlobalActionAutomator.click(x, y)) {
                ToastUtil.showShortText("辅助停止喽 重启试试");
            }
        } catch (Throwable e) {
            LogUtils.e(e.getLocalizedMessage());
        }
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClick(AccessibilityNodeInfo target) {
        Rect rect = new Rect();
        target.getBoundsInScreen(rect);
        int x = (rect.left + rect.right) >> 1;
        int y = (rect.top + rect.bottom) >> 1;
        if (rect.left >= 0 && rect.right <= mWidth && rect.top >= 0 && rect.bottom <= mHeight) {
            exeClick(x, y);
            return true;
        }
        return exeClick(target, rect.left, rect.top, rect.right, rect.bottom);
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClick(AccessibilityNodeInfo target, int left, int top, int right, int bottom) {
        Rect rect = new Rect();
        target.getBoundsInScreen(rect);
        int x = (rect.left + rect.right) >> 1;
        int y = (rect.top + rect.bottom) >> 1;
        if (rect.left >= left && rect.right <= right && rect.top >= top && rect.bottom <= bottom) {
            exeClick(x, y);
            return true;
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClickId(String id, int parentIn) {
        return exeClickId(getRootInActiveWindow(), id, parentIn);
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClickId(AccessibilityNodeInfo parent, String id, int parentIn) {
        if (parent != null) {
            List<AccessibilityNodeInfo> nodes = parent.findAccessibilityNodeInfosByViewId(packageName + id);
            if (!CollectionUtils.isEmpty(nodes)) {
                AccessibilityNodeInfo target = nodes.get(nodes.size() - 1);
                for (int i = 0; i < parentIn; i++) {
                    target = target.getParent();
                }
                return target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public boolean quickClickText(String text) {
        AccessibilityNodeInfo newRootNode = getRootInActiveWindow();
        if (newRootNode != null) {
            List<AccessibilityNodeInfo> nodes = newRootNode.findAccessibilityNodeInfosByText(text);
            if (!CollectionUtils.isEmpty(nodes)) {
                AccessibilityNodeInfo target = nodes.get(nodes.size() - 1);
                return target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClickText(String text) {
        AccessibilityNodeInfo newRootNode = getRootInActiveWindow();
        if (newRootNode != null) {
            List<AccessibilityNodeInfo> nodes = newRootNode.findAccessibilityNodeInfosByText(text);
            if (!CollectionUtils.isEmpty(nodes)) {
                return exeClick(nodes.get(nodes.size() - 1));
            }
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClickText(String text, int left, int top, int right, int bottom) {
        AccessibilityNodeInfo newRootNode = getRootInActiveWindow();
        if (newRootNode != null) {
            List<AccessibilityNodeInfo> nodes = newRootNode.findAccessibilityNodeInfosByText(text);
            if (!CollectionUtils.isEmpty(nodes)) {
                return exeClick(nodes.get(nodes.size() - 1), left, top, right, bottom);
            }
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public boolean exeClickText(String text, int index) {
        AccessibilityNodeInfo newRootNode = getRootInActiveWindow();
        if (newRootNode != null) {
            List<AccessibilityNodeInfo> nodes = newRootNode.findAccessibilityNodeInfosByText(text);
            if (CollectionUtils.isItemInCollection(index, nodes)) {
                exeClick(nodes.get(index));
                return true;
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
                mWeakHandler.postDelayed(() -> exeClickTexts(strs, index + 1, endCallback), getShotTime());
            } else {
                errorCount++;
                mWeakHandler.postDelayed(() -> exeClickTexts(strs, index, endCallback), getSshotTime());
            }
            return;
        }
        errorCount = 0;
        mWeakHandler.postDelayed(() -> endCallback.onEnd(true), getSshotTime());
    }

    private void execShellCmd(String cmd) {
        LogUtils.d(cmd);
        OutputStream outputStream = null;
        DataOutputStream dataOutputStream = null;
        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            outputStream = process.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd + "\n");
            dataOutputStream.flush();
        } catch (IOException e) {
            ToastUtil.showShortText("请查看是否获取root权限");
            e.printStackTrace();
        } finally {
            FileUtil.close(dataOutputStream, outputStream);
        }
    }

    protected interface EndCallback {

        void onEnd(boolean result);
    }

    public void registerDataCallBack(final OnDataCallback<?> onDataCallback) {
        if (currentTask == null) {
            onDataCallback.onDataCallback(null);
        } else {
            currentTask.registerDataCallBack(onDataCallback);
        }
    }

    public void unregisterDataCallBack(final OnDataCallback<?> onDataCallback) {
        if (currentTask == null) {
            onDataCallback.onDataCallback(null);
        } else {
            currentTask.unregisterDataCallBack(onDataCallback);
        }
    }

    public class ListenCallback implements Callback {

        @Override
        public boolean handleMessage(@NotNull final Message msg) {
            return false;
        }
    }

    public class MyBinder extends Binder {

        public BaseListenerService getService() {
            return BaseListenerService.this;
        }
    }

    public interface OnAccessibilityEventListener {

        void onAccessibilityEvent(final AccessibilityEvent accessibilityEvent);
    }
}