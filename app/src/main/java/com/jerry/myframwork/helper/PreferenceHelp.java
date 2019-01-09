package com.jerry.myframwork.helper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jerry.myframwork.MyApplication;

/**
 * SharedPreference操作类
 */
public class PreferenceHelp {

    private PreferenceHelp() {
    }

    private static SharedPreferences sp =  PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());

    public static String getString(String strKey) {
        return sp.getString(strKey, "");
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

    public static boolean getBoolean(String strKey) {
        return sp.getBoolean(strKey, false);
    }

    public static boolean getBoolean(String strKey, boolean bDefault) {
        return sp.getBoolean(strKey, bDefault);
    }

    public static void putBoolean(String strKey, boolean bValue) {
        sp.edit().putBoolean(strKey, bValue).apply();
    }
}