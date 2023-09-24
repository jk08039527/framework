package com.jerry.baselib.okhttp.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

public class GetRequest extends BaseOkHttpRequest {

    public GetRequest(String url, Object tag, Map<String, Object> params, Map<String, Object> headers) {
        super(url, tag, params, headers);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.get().build();
    }

}
