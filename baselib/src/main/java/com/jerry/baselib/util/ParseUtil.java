package com.jerry.baselib.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import android.text.TextUtils;

/**
 * 基本数据类型解析工具类
 */
public class ParseUtil {

    private ParseUtil() {
    }

    /**
     * 解析以字符串表示的整数类型
     */
    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    /**
     * 解析以字符串表示的整数类型，如果发生异常则返回默认值
     */
    public static int parseInt(String s, int defaultInt) {
        if (TextUtils.isEmpty(s)) {
            return defaultInt;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
            return defaultInt;
        }
    }

    /**
     * 解析以字符串表示的长整数类型
     */
    public static long parseLong(String s) {
        return parseLong(s, 0L);
    }

    /**
     * 解析以字符串表示的长整数类型，如果发生异常则返回默认值
     */
    public static long parseLong(String s, long defaultLong) {
        if (TextUtils.isEmpty(s)) {
            return defaultLong;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
        }
        return defaultLong;
    }

    /**
     * 解析以字符串表示的单精度浮点类型
     */
    public static float parseFloat(String s) {
        return parseFloat(s, 0.0F);
    }

    /**
     * 解析以字符串表示的单精度浮点类型，如果发生异常则返回默认值
     */
    public static float parseFloat(String s, float defaultFloat) {
        if (TextUtils.isEmpty(s)) {
            return defaultFloat;
        }
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
        }
        return defaultFloat;
    }

    /**
     * 解析以字符串表示的双精度浮点类型
     */
    public static float parse2Float(String s) {
        try {
            return new BigDecimal(s).setScale(2, RoundingMode.HALF_EVEN).floatValue();
        } catch (Exception e) {
            LogUtils.w("parse2Float error");
        }
        return 0;
    }

    /**
     * 解析以字符串表示的双精度浮点类型
     */
    public static double parse2Double(String s) {
        return MathUtil.halfEven(parseDouble(s, 0.0), 2);
    }

    /**
     * 解析以字符串表示的双精度浮点类型
     */
    public static double parseDouble(String s) {
        return parseDouble(s, 0.0);
    }

    /**
     * 解析以字符串表示的双精度浮点类型，如果发生异常则返回默认值
     */
    public static double parseDouble(String s, double defaultDouble) {
        if (TextUtils.isEmpty(s)) {
            return defaultDouble;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
        }
        return defaultDouble;
    }

    /**
     * 转换小数为百分比
     *
     * @param fraction 精度
     */
    public static String parse2Percent(double d, int fraction) {
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMaximumIntegerDigits(3);
        numberFormat.setMinimumFractionDigits(fraction);
        return numberFormat.format(d);
    }

    /**
     * 转换小数为百分比, 默认保留一位小数
     */
    public static String parse2Percent(String s) {
        return parse2Percent(parseDouble(s), 0);
    }

    /**
     * 转换小数为百分比
     *
     * @param fraction 精度
     */
    public static String parse2Percent(String s, int fraction) {
        return parse2Percent(parseDouble(s), fraction);
    }

    /**
     * 数字格式化为#,###类型
     */
    public static String parseNum2USFormat(int num) {
        return NumberFormat.getInstance(Locale.US).format(num);
    }

    /**
     * 文字转数字
     */
    public static int parseStr2Int(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        if (text.contains("万")) {
            return (int) (ParseUtil.parse2Double(text.replace("万", "").trim()) * 10000);
        }
        return ParseUtil.parseInt(text);
    }
}
