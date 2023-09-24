package com.jerry.baselib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.jerry.baselib.Key;

/**
 * 字符串相关工具类
 *
 * @author my
 * @time 2017/2/24 15:31
 */
public class StringUtil {

    public static void main(String[] strings) {
        System.out.println(getRandomInt(0, 2));
    }

    /**
     * 分割字段
     */
    public static String[] safeSplit(String str) {
        return safeSplit(str, " ");
    }

    /**
     * 分割字段
     */
    public static String[] safeSplit(String str, String regularExpression) {
        if (TextUtils.isEmpty(str)) {
            return new String[]{Key.NIL};
        }
        return str.split(regularExpression);
    }

    public static String toSpiltStr(final List<String> tags) {
        return toSpiltStr(tags, " ");
    }

    public static String toSpiltStr(final List<String> tags, final String regularExpression) {
        StringBuilder sb = new StringBuilder();
        if (tags != null) {
            for (int i = 0; i < tags.size(); i++) {
                sb.append(tags.get(i)).append(regularExpression);
            }
        }
        return sb.toString().trim();
    }

    public static int getRandomInt(int min, int max) {
        if (min > max) {
            max = max + min;
            min = max - min;
            max = max - min;
        }
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static List<String> getPicsFromStr(String str) {
        List<String> pics = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String[] sdfs = safeSplit(str);
        for (String sdf : sdfs) {
            sb.append(sdf);
            if (sdf.contains(".jpg") || sdf.contains(".png") || sdf.contains(".jpeg") || sdf.contains(".mp4")) {
                pics.add(sb.toString());
                sb.delete(0, sb.length());
            } else {
                sb.append(" ");
            }
        }
        return pics;
    }

    public static String getSimpleStrFromLink(String str) {
        int index = str.indexOf("?");
        if (index > 0) {
            return str.substring(0, index);
        }
        return str;
    }

    public static String toPriceInCent(final double price) {
        String str = String.valueOf(price * 100);
        int index = str.indexOf(".");
        if (index >= 0) {
            return str.substring(0, index);
        }
        return str;
    }

    public static String getNumberFromText(String text) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        return m.replaceAll("");
    }
}
