package com.jerry.baselib.impl;

/**
 * Created by wzl on 2018/10/18.
 *
 * @Description 数据变化监听
 */
public interface OnDataCallback<T> {

    void onDataCallback(T data);
}
