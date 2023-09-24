package com.jerry.baselib.okhttp.builder;

import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.jerry.baselib.okhttp.request.RequestCall;

public abstract class BaseOkHttpRequestBuilder {

    protected String url;
    protected Object tag;
    protected Map<String, Object> headers;
    protected Map<String, Object> params;

    public BaseOkHttpRequestBuilder headers(TreeMap<String, Object> headers) {
        this.headers = headers;
        return this;
    }

    public BaseOkHttpRequestBuilder params(TreeMap<String, Object> params) {
        this.params = params;
        return this;
    }


    public <T> BaseOkHttpRequestBuilder addHeader(T data) {
        if (this.headers == null) {
            headers = new TreeMap<>();
        }
        headers.putAll(JSON.parseObject(JSON.toJSONString(data)).getInnerMap());
        return this;
    }

    public <T> BaseOkHttpRequestBuilder addDataParams(T data) {
        if (this.params == null) {
            params = new TreeMap<>();
        }
        params.putAll(JSON.parseObject(JSON.toJSONString(data)).getInnerMap());
        return this;
    }

    public abstract RequestCall build();

}
