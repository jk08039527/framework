package com.jerry.baselib.okhttp.callback;

import java.io.IOException;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jerry.baselib.okhttp.OkHttpUtils;
import com.jerry.baselib.util.ListCacheUtil;
import com.jerry.baselib.util.LogUtils;

import okhttp3.Response;
/**
 * Created by wzl on 2018/9/8.
 */
public class SBCallBack<T> extends Callback<T> {

    private final Class<T> mClass;
    private String cache;

    public SBCallBack(Class<T> tClass) {
        mClass = tClass;
    }

    public SBCallBack(Class<T> tClass, String cache) {
        mClass = tClass;
        this.cache = cache;
    }

    @Override
    public T parseNetworkResponse(Response response) throws IOException {
        try {
            String content = response.body().string();
            LogUtils.d(content);
            T parseObject = JSON.parseObject(content, mClass);
            if (!TextUtils.isEmpty(cache) && !TextUtils.isEmpty(content)) {
                ListCacheUtil.saveValueToJsonFile(cache, content);
            }
            return parseObject;
        } catch (Exception e) {
            LogUtils.w(e.toString());
            e.printStackTrace();
            OkHttpUtils.getInstance().sendFailResultCallback(errorMsg, this);
        }
        return super.parseNetworkResponse(response);
    }
}
