package com.jerry.baselib.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jerry.baselib.App;
import com.jerry.baselib.Key;

/**
 * SharedPreference操作类
 */
public class PreferenceHelp {

    public static final String PDD_COOKIE = "pddCookie";
    public static final String DIRNAME = "dirname";

    private PreferenceHelp() {
    }

    private static final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getInstance());

    public static String getString(String strKey) {
        return sp.getString(strKey, Key.NIL);
    }

    public static String getString(String strKey, String strDefault) {
        return sp.getString(strKey, strDefault);
    }

    public static void putString(String strKey, String strData) {
        sp.edit().putString(strKey, strData).apply();
    }

    public static int getInt(String strKey) {
        return sp.getInt(strKey, 0);
    }

    public static int getInt(String strKey, int strDefault) {
        return sp.getInt(strKey, strDefault);
    }

    public static void putInt(String strKey, int strData) {
        sp.edit().putInt(strKey, strData).apply();
    }

    public static float getFloat(String strKey) {
        return sp.getFloat(strKey, 0);
    }

    public static float getFloat(String strKey, float strDefault) {
        return sp.getFloat(strKey, strDefault);
    }

    public static void putFloat(String strKey, float strData) {
        sp.edit().putFloat(strKey, strData).apply();
    }

    public static long getLong(String strKey) {
        return sp.getLong(strKey, 0);
    }

    public static long getLong(String strKey, long strDefault) {
        return sp.getLong(strKey, strDefault);
    }

    public static void putLong(String strKey, long l) {
        sp.edit().putLong(strKey, l).apply();
    }

    public static boolean getBoolean(String strKey) {
        return sp.getBoolean(strKey, false);
    }

    public static boolean getBoolean(String strKey, boolean bDefault) {
        return sp.getBoolean(strKey, bDefault);
    }

    public static void putBoolean(String strKey, boolean bValue) {
        sp.edit().putBoolean(strKey, bValue).apply();
    }

    public static void remove(String strKey){
        sp.edit().remove(strKey).apply();
    }

    public static boolean isFirstDo(final String firstKey) {
        return sp.getBoolean("first_" + firstKey, true);
    }

    public static void setNotFirstDo(final String firstKey) {
        sp.edit().putBoolean("first_" + firstKey, false).apply();
    }
}