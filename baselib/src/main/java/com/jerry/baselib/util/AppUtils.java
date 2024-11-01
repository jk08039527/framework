package com.jerry.baselib.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.media.SoundPool;
import android.os.SystemClock;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.ViewConfiguration;

import com.jerry.baselib.App;
import com.jerry.baselib.Key;
import com.jerry.baselib.R;

/**
 * 常用方法的工具类
 *
 * @author my
 * @time 2016/9/22 14:41
 */
public class AppUtils {

    private static long lastClickTime;
    private static Pattern chinesePattern = Pattern.compile("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$");

    /**
     * 获取应用程序versionName
     */
    public static String getVersionName() {
        Context context = App.getInstance();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return Key.NIL;
        }
    }

    /**
     * 是否快速点击
     */
    public static boolean isFastDoubleClick() {
        long time = SystemClock.elapsedRealtime();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < ViewConfiguration.getJumpTapTimeout()) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 获取泛型类的type
     *
     * @param raw 泛型类的class, 如BaseResponse4Object.class
     * @param args 泛型实参的class, LotteryBean.class
     * @return 泛型类的type
     */
    public static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return args;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }

            @Override
            public Type getRawType() {
                return raw;
            }
        };
    }


    /**
     * 获取设备ID,如果ID为空,再取Mac地址,都为空最后随机生成
     */
    @SuppressLint("hardwareIds")
    public static String getDeviceId() {
        Context context = App.getInstance();
        String deviceId = PreferenceHelp.getString(Key.DEVICEID);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (tm == null) {
                deviceId = Key.NIL;
            } else {
                deviceId = tm.getDeviceId();
            }
            deviceId = deviceId == null ? Key.NIL : deviceId;
        } catch (SecurityException e) {
            e.printStackTrace();
            deviceId = Key.NIL;
        }

        //状态码权限被关闭:获得的DeviceId全部字符相同或者为空则判定为权限被关闭，使用UUID作为唯一标识
        if (TextUtils.isEmpty(deviceId) || Pattern.matches("(.)(\\1)*", deviceId)) {
            deviceId = UUID.randomUUID().toString();
        }

        if (isChinese(deviceId)) {
            deviceId = changeToUrlEncode(deviceId);
        }
        PreferenceHelp.putString(Key.DEVICEID, deviceId);
        return deviceId;
    }

    private static boolean isChinese(String string) {
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        for (int i = 0; i < string.length(); i++) {
            Matcher m = chinesePattern.matcher(string.charAt(i) + Key.NIL);
            if (m.matches()) {
                return true;
            }
        }
        return false;
    }

    public static String changeToUrlEncode(String str) {
        if (TextUtils.isEmpty(str)) {
            return Key.NIL;
        }
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (Exception ex) {
            return str;
        }
    }

    /**
     * 获取应用程序版本
     */
    public static int getVersionCode() {
        try {
            PackageInfo info = App.getInstance().getPackageManager().getPackageInfo(App.getInstance().getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 震动发声提示
     */
    public static void giveNotice(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(new long[]{500, 1000, 500, 1000}, -1);
        }
        SoundPool mSoundPool;
        mSoundPool = new SoundPool.Builder().setMaxStreams(10).build();
        int mWinMusic = mSoundPool.load(context, R.raw.fadein, 1);
        mSoundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> soundPool.play(mWinMusic, 0.6F, 0.6F, 0, 0, 1.0F));
    }
}
