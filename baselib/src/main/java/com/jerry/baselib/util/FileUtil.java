package com.jerry.baselib.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 文件处理类
 *
 * @author Tina
 */
public class FileUtil {

    public static void export(Context context, String fileName, String content) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
        values.put(MediaStore.Downloads.IS_PENDING, 1);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri fileUri = resolver.insert(collection, values);
            if (fileUri == null) {
                ToastUtil.showShortText("创建文件失败");
                return;
            }
            try (OutputStream out = resolver.openOutputStream(fileUri); OutputStreamWriter osw = new OutputStreamWriter(
                out); BufferedWriter writer = new BufferedWriter(osw)) {
                writer.write(content);
                writer.flush();

                // 标记写入完成
                values.clear();
                values.put(MediaStore.Downloads.IS_PENDING, 0);
                resolver.update(fileUri, values, null, null);

                ToastUtil.showShortText("导出成功: " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                ToastUtil.showShortText("写入失败: " + e.getMessage());
            }
        }
    }
    /**
     * 清空文件：参数为文件夹时，只清理其内部文件，不清理本身, 参数为文件时，删除
     */
    public static boolean clearFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return false;
            }
            for (File f : files) {
                if (f.isDirectory()) {
                    clearFile(f);
                } else {
                    f.delete();
                }
            }
        } else {
            file.delete();
        }
        return true;
    }

    public static void close(final Closeable... closeables) {
        try {
            for (Closeable closeable : closeables) {
                if (closeable == null) {
                    continue;
                }
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
