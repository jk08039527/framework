package com.jerry.baselib.okhttp.request;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class BaseOkHttpRequest {

    protected String url;
    protected Object tag;
    protected Map<String, Object> params;
    protected Map<String, Object> headers;

    protected Request.Builder builder = new Request.Builder();

    protected BaseOkHttpRequest(String url, Object tag, Map<String, Object> params, Map<String, Object> headers) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.put("accept", "application/json, text/plain, */*");
        this.headers.put("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        if (url == null) {
            throw new IllegalArgumentException("url can not be null.");
        }
    }

    protected abstract RequestBody buildRequestBody();

    protected abstract Request buildRequest(Request.Builder builder, RequestBody requestBody);

    public RequestCall build() {
        return new RequestCall(this);
    }

    public Request generateRequest() {
        RequestBody requestBody = buildRequestBody();
        prepareBuilder();
        return buildRequest(builder, requestBody);
    }

    private void prepareBuilder() {
        builder.url(url).tag(tag);
        appendHeaders();
    }

    private void appendHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) {
            return;
        }

        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key).toString());
        }
        builder.headers(headerBuilder.build());
    }

    @NotNull
    @Override
    public String toString() {
        return "BaseOkHttpRequest{" + "url='" + url + '\'' + ", tag=" + tag + ", params=" + params + ", headers=" + headers + '}';
    }
}
