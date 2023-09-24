package com.jerry.baselib.okhttp.callback;

import java.io.IOException;

import com.jerry.baselib.okhttp.OkHttpUtils;

import okhttp3.Request;
import okhttp3.Response;

public class Callback<T> {

    protected String errorMsg = OkHttpUtils.ERROR_MSG;

    /**
     * UI Thread
     */
    public void onBefore(Request request) {}

    /**
     * UI Thread
     */
    public void onAfter() {}

    /**
     * UI Thread
     */
    public void inProgress(float progress) {

    }

    public T parseNetworkResponse(Response response) throws IOException {
        return null;
    }

    public void onResponse(T response) {}

    public void onError(String response) {}

    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public Object parseNetworkResponse(Response response) {
            return null;
        }

        @Override
        public void onResponse(Object response) {

        }

        @Override
        public void onError(final String response) {

        }
    };

}