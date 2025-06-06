package com.jerry.baselib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.jerry.baselib.Key;

/**
 * 字符串相关工具类
 *
 * @author my
 * @time 2017/2/24 15:31
 */
public class StringUtil {

    private static final ArrayMap<String,String> JSCODE_SWITCHER = new ArrayMap<>();
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3456789]\\d{9}$");

    static {
        JSCODE_SWITCHER.put("\\u003C", "<");
        JSCODE_SWITCHER.put("\\u003E", ">");
        JSCODE_SWITCHER.put("\\u002F", "/");
        JSCODE_SWITCHER.put("&#x3D;\\", "=");
        JSCODE_SWITCHER.put("&quot;", "");
        JSCODE_SWITCHER.put("\\", "");
    }

    public static void main(String[] strings) {
        System.out.println(getNumberFromText("塑料袋福建省看62.67dd；‘顺口溜福克斯了；df"));
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

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomNumString(int length) {
        String str = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
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
        String regEx = "([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        while (m.find()) {
            return m.group(0);
        }
        return null;
    }

    public static boolean containsCoin(final String desc) {
        if (TextUtils.isEmpty(desc)) {
            return false;
        }
        int index1 = desc.indexOf("元");
        if (index1 > 2) {
            String numberStr = getNumberFromText(desc.substring(index1 - 2, index1));
            if (numberStr.length() > 0) {
                return true;
            }
        }
        int index2 = desc.indexOf("¥");
        if (index2 > 2) {
            String numberStr = getNumberFromText(desc.substring(index2, index2 + 2));
            if (numberStr.length() > 0) {
                return true;
            }
        }
        if (Math.max(index1, index2 + 1) > 0) {
            String nextStr = desc.substring(index2);
            return containsCoin(nextStr);
        }
        return false;
    }

    public static boolean containsUrl(final String desc) {
        String regex = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
        return !TextUtils.isEmpty(filterSpecialStr(regex, desc));
    }

    public static String switchJsCode(String text) {
        for (Entry<String, String> stringStringEntry : JSCODE_SWITCHER.entrySet()) {
            text = text.replace(stringStringEntry.getKey(), stringStringEntry.getValue());
        }
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }
        return text;
    }

    /**
     * 参数1 regex:我们的正则字符串 参数2 就是一大段文本，这里用data表示
     */
    private static String filterSpecialStr(String regex, String data) {
        //sb存放正则匹配的结果
        StringBuffer sb = new StringBuffer();
        //编译正则字符串
        Pattern p = Pattern.compile(regex);
        //利用正则去匹配
        Matcher matcher = p.matcher(data);
        //如果找到了我们正则里要的东西
        while (matcher.find()) {
            //保存到sb中，"\r\n"表示找到一个放一行，就是换行
            sb.append(matcher.group() + "\r\n");
        }
        return sb.toString();
    }

    /**
     * 是否为手机号
     */
    public static boolean isPhoneNumber(String phoneStr) {
        // 定义手机号正则表达式
        return PHONE_PATTERN.matcher(phoneStr).matches();
    }
}
