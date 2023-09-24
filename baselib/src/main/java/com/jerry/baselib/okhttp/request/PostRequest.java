package com.jerry.baselib.okhttp.request;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import com.jerry.baselib.okhttp.builder.PostBuilder.FileInput;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostRequest extends BaseOkHttpRequest {

    private String body;
    private List<FileInput> files;

    public PostRequest(String url, String body) {
        super(url, null, null, null);
        this.body = body;
    }

    public PostRequest(String url, Object tag, Map<String, Object> params, Map<String, Object> headers, List<FileInput> files) {
        super(url, tag, params, headers);
        this.files = files;
    }

    @Override
    protected RequestBody buildRequestBody() {
        if (files == null || files.isEmpty()) {
            if (TextUtils.isEmpty(body)) {
                FormBody.Builder builder = new FormBody.Builder();
                addParams(builder);
                return builder.build();
            }
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
            return RequestBody.create(mediaType, body);
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            addParams(builder);
            for (int i = 0; i < files.size(); i++) {
                FileInput fileInput = files.get(i);
                RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file);
                builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
            }
            return builder.build();
        }
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.post(requestBody).build();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void addParams(MultipartBody.Builder builder) {
        if (params == null || params.isEmpty()) {
            builder.addFormDataPart("1", "1");
            return;
        }
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value != null) {
                builder.addFormDataPart(key, value.toString());
            }
        }
    }

    private void addParams(FormBody.Builder builder) {
        if (params == null || params.isEmpty()) {
            builder.add("1", "1");
            return;
        }

        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value != null) {
                builder.add(key, value.toString());
            }
        }
    }

    @NotNull
    @Override
    public String toString() {
        return "BaseOkHttpRequest{" + "url='" + url + '\'' + ", tag=" + tag + ", params=" + params + ", headers=" + headers + ", body=" + body + '}';
    }
}
