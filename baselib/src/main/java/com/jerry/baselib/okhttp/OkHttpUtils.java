package com.jerry.baselib.okhttp;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import android.os.Looper;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import com.jerry.baselib.App;
import com.jerry.baselib.BuildConfig;
import com.jerry.baselib.Key;
import com.jerry.baselib.okhttp.builder.PostBuilder;
import com.jerry.baselib.util.NetworkUtil;
import com.jerry.baselib.okhttp.builder.GetBuilder;
import com.jerry.baselib.okhttp.callback.Callback;
import com.jerry.baselib.okhttp.request.RequestCall;
import com.jerry.baselib.util.FileUtil;
import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.WeakHandler;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
public class OkHttpUtils {

    public static final int TIMEOUT_MILLISECONDS = 15000;// default:10s
    public static final String ERROR_MSG = Key.NIL;
    public static final String TIMEOUT_MSG = "超时啦，请刷新重试！";
    private static final String TAG = "OkHttpUtils";
    private static final String BROKEN_MSG = "网络不给力，请检查网络连接后再试！";
    private static final int CACHE_SIZE = 10 * 1024 * 1024;// 10M
    private static volatile OkHttpUtils mInstance;
    private final OkHttpClient mOkHttpClient;
    private final WeakHandler mDelivery;
    private final boolean debug = BuildConfig.DEBUG;
    private String tag = Key.NIL;

    private OkHttpUtils() {
        mDelivery = new WeakHandler(Looper.getMainLooper());

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);

        Cache cache = new Cache(App.getInstance().getCacheDir(), CACHE_SIZE);
        okHttpClientBuilder.cache(cache);

        mOkHttpClient = okHttpClientBuilder.build();
    }

    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostBuilder post() {
        return new PostBuilder();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public WeakHandler getDelivery() {
        return mDelivery;
    }

    public Response execute(final RequestCall requestCall) {
        if (debug) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            LogUtils.d(tag, "{method:" + requestCall.getRequest().method() + ", detail:" + requestCall.getOkHttpRequest().toString() + "}");
        }
        try {
            return requestCall.getCall().execute();
        } catch (IOException e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return null;
    }

    public void execute(final RequestCall requestCall, Callback callback) {
        if (debug) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            LogUtils.d(tag, "{method:" + requestCall.getRequest().method() + ", detail:" + requestCall.getOkHttpRequest().toString() + "}");
        }

        if (callback == null) {
            callback = Callback.CALLBACK_DEFAULT;
        }
        final Callback finalCallback = callback;
        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (e instanceof SocketTimeoutException) {
                    sendFailResultCallback(TIMEOUT_MSG, finalCallback);
                } else {
                    if (NetworkUtil.isNetworkAvailable(false)) {
                        sendFailResultCallback(ERROR_MSG, finalCallback);
                    } else {
                        sendFailResultCallback(BROKEN_MSG, finalCallback);
                    }
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.code() >= 400 && response.code() <= 599) {
                    try {
                        sendFailResultCallback(ERROR_MSG, finalCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Object o = finalCallback.parseNetworkResponse(response);
                        sendSuccessResultCallback(o, finalCallback);
                    } catch (IOException | OutOfMemoryError e) {
                        sendFailResultCallback(ERROR_MSG, finalCallback);
                    } finally {
                        FileUtil.close(response.body());
                    }
                }
            }
        });
    }

    public void sendFailResultCallback(final String msg, final Callback callback) {
        if (callback == null) {
            return;
        }

        mDelivery.post(() -> {
            callback.onError(msg);
            callback.onAfter();
        });
    }

    public void sendProgressCallback(final float progress, final Callback callback) {
        if (callback == null) {
            return;
        }
        mDelivery.post(() -> callback.inProgress(progress));
    }

    @SuppressWarnings("unchecked")
    private void sendSuccessResultCallback(final Object object, final Callback callback) {
        if (callback == null) {
            return;
        }
        mDelivery.post(() -> {
            callback.onResponse(object);
            callback.onAfter();
        });
    }

    public void cancelTag(Object tag) {
        if (tag == null) {
            return;
        }
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

}
