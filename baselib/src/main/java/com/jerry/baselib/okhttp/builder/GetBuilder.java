package com.jerry.baselib.okhttp.builder;

import java.util.Map;

import com.jerry.baselib.okhttp.request.GetRequest;
import com.jerry.baselib.okhttp.request.RequestCall;

public class GetBuilder extends BaseOkHttpRequestBuilder {

    @Override
    public RequestCall build() {
        if (params != null) {
            url = appendParams(url, params);
        }

        return new GetRequest(url, tag, params, headers).build();
    }

    public GetBuilder url(String url) {
        this.url = url;
        return this;
    }

    public GetBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    private String appendParams(String url, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                sb.append(key).append("=").append(params.get(key)).append("&");
            }
        }

        if (sb.length() > 0) {
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
