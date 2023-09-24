package com.jerry.baselib.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

/**
 * @author Jerry
 * @createDate 2022/11/2
 * @copyright www.axiang.com
 * @description
 */
public class TxtUtil {
    private TxtUtil() {}

    /**
     * 读入TXT文件
     */
    public static List<String> readFile(String path) {
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try {
            FileReader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            List<String> urls = new ArrayList<>();
            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                urls.add(line);
            }
            return urls;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读入TXT文件
     */
    public static List<String> readFile(Context context, @NonNull Uri uri) {
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        BufferedReader reader = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            List<String> sbf = new ArrayList<>();
            reader = new BufferedReader(inputStreamReader);
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.add(tempStr);
            }
            return sbf;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }
}
