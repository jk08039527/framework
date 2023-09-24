package com.jerry.baselib.okhttp.request;

import java.io.IOException;

import com.jerry.baselib.okhttp.OkHttpUtils;
import com.jerry.baselib.okhttp.callback.Callback;
import com.jerry.baselib.util.LogUtils;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class RequestCall {

    private final BaseOkHttpRequest okHttpRequest;
    private Request request;
    private Call call;

    public RequestCall(BaseOkHttpRequest request) {
        this.okHttpRequest = request;
    }

    private void generateCall() {
        request = okHttpRequest.generateRequest();
        call = OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
    }

    public void execute(Callback callback) {
        generateCall();
        if (callback != null) {
            callback.onBefore(request);
        }
        OkHttpUtils.getInstance().execute(this, callback);
    }

    public Call getCall() {
        return call;
    }

    public Request getRequest() {
        return request;
    }

    public BaseOkHttpRequest getOkHttpRequest() {
        return okHttpRequest;
    }

    public Response execute() {
        generateCall();
        try {
            return call.execute();
        } catch (IOException e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return null;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

}
