package com.jerry.baselib.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.jerry.baselib.R;

/**
 * 文件处理类
 *
 * @author Tina
 */
public class FileUtil {

    public static void export(Context context, String fileName, String content) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/veoas");
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            ContentResolver resolver = context.getContentResolver();
            Uri collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri fileUri = resolver.insert(collection, values);
            if (fileUri == null) {
                ToastUtil.showShortText(R.string.file_create_fail);
                return;
            }
            try (OutputStream out = resolver.openOutputStream(fileUri); OutputStreamWriter osw = new OutputStreamWriter(
                out); BufferedWriter writer = new BufferedWriter(osw)) {
                writer.write(content);
                writer.flush();

                // 标记写入完成
                values.clear();
                resolver.update(fileUri, values, null, null);
                ToastUtil.showShortText(context.getString(R.string.file_export_success, "Documents/veoas/" + fileName));
            } catch (IOException e) {
                e.printStackTrace();
                ToastUtil.showShortText(context.getString(R.string.file_export_fail, "Documents/veoas/" + fileName));
            }
        } else {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                if (!path.exists()) {
                    path.mkdirs();
                }
                File filedir = new File(path, "veoOutput");
                if (!filedir.exists()) {
                    filedir.mkdirs();
                }
                File file = new File(filedir, fileName);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(content.getBytes(StandardCharsets.UTF_8));
                    ToastUtil.showShortText(context.getString(R.string.file_export_success, filedir + "/" + fileName));
                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtil.showShortText(context.getString(R.string.file_export_fail, filedir + "/" + fileName));
                }
            } else {
                ToastUtil.showShortText(R.string.external_storage_not_available);
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
