package com.jerry.baselib.impl;

/**
 * @author Jerry
 * @createDate 2023/9/22
 * @description
 */
public interface OnItemClickListener<T> {

    void onItemClick(T bean, int position);
}
