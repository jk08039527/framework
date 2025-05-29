package com.jerry.baselib.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jerry.baselib.App;
import com.jerry.baselib.R;

public class FileLog {

    private static final String TAG = "FileLog";
    private static final int FLUSH_INTERVAL_MS = 3000;
    private static final int MAX_BUFFER_SIZE = 50;
    private static volatile FileLog sFileLog;

    private final List<String> logBuffer = new ArrayList<>();
    private File logFile;
    private Uri fileUri;
    private ParcelFileDescriptor pfd;
    private Handler handler;
    private boolean opened;
    private boolean hasWrite;

    public static FileLog getInstance() {
        if (sFileLog == null) {
            synchronized (FileLog.class) {
                if (sFileLog == null) {
                    sFileLog = new FileLog();
                }
            }
        }
        return sFileLog;
    }

    public boolean getOpened() {
        return opened;
    }

    public void open() {
        if (opened) {
            Log.w(TAG, "FileLog has opened");
            return;
        }
        try {
            String fileName = "log_" + DateUtils.getDateTimeByLong(System.currentTimeMillis()) + ".txt";
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/veoLog");
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                ContentResolver resolver = App.getInstance().getContentResolver();
                fileUri = resolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values);
                if (fileUri == null) {
                    ToastUtil.showShortText(R.string.file_create_fail);
                    return;
                }
                pfd = resolver.openFileDescriptor(fileUri, "rw");
            } else {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    File filedir = new File(path, "veoLog");
                    if (!filedir.exists()) {
                        filedir.mkdirs();
                    }
                    logFile = new File(filedir, fileName);
                } else {
                    ToastUtil.showShortText(R.string.external_storage_not_available);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "open error");
        }
        opened = true;

        HandlerThread thread = new HandlerThread("LoggerThread");
        thread.start();
        handler = new Handler(thread.getLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                flush();
                handler.postDelayed(this, FLUSH_INTERVAL_MS);
            }
        }, FLUSH_INTERVAL_MS);
    }

    // 退出前调用
    public void close() {
        try {
            flush();
            if (!hasWrite) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    pfd.close();
                    App.getInstance().getContentResolver().delete(fileUri, null, null);
                } else {
                    logFile.delete();
                }
            }
            opened = false;
            hasWrite = false;
        } catch (Exception e) {
            Log.e(TAG, "close error");
        }
    }

    public synchronized void log(String level, String tag, String message) {
        if (opened) {
            String timestamp = DateUtils.getDateTimeByLong(System.currentTimeMillis());
            String line = timestamp + " " + level + "/" + tag + ": " + message;
            logBuffer.add(line);
            if (logBuffer.size() >= MAX_BUFFER_SIZE) {
                flush();
            }
        }
    }

    public synchronized void flush() {
        if (logBuffer.isEmpty()) {
            return;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            try (FileWriter writer = new FileWriter(pfd.getFileDescriptor())) {
                for (String line : logBuffer) {
                    writer.write(line + "\n");
                    if (!hasWrite) {
                        hasWrite = true;
                    }
                }
                logBuffer.clear();
            } catch (IOException e) {
                Log.e(TAG, "写日志失败", e);
            }
        } else {
            try (FileWriter writer = new FileWriter(logFile, true)) {
                for (String line : logBuffer) {
                    writer.write(line + "\n");
                    if (!hasWrite) {
                        hasWrite = true;
                    }
                }
                logBuffer.clear();
            } catch (IOException e) {
                Log.e(TAG, "写日志失败", e);
            }
        }

    }

    // 快捷方法
    public void v(String tag, String msg) {
        log("V", tag, msg);
    }

    public void d(String tag, String msg) {
        log("D", tag, msg);
    }

    public void i(String tag, String msg) {
        log("I", tag, msg);
    }

    public void e(String tag, String msg) {
        log("E", tag, msg);
    }

    public void w(String tag, String msg) {
        log("W", tag, msg);
    }
}
