package com.jerry.baselib.parsehelper;

import android.webkit.JavascriptInterface;

import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.WeakHandler;

/**
 * @author Jerry
 * @createDate 2019-05-31
 * @description
 */
public class WebViewUtil {

    private static final String TAG = "WebViewUtil";
    public static final int MSG_PRODUCT = 101;

    private final WeakHandler mWeakHandler;

    public WebViewUtil(final WeakHandler weakHandler) {
        mWeakHandler = weakHandler;
    }

    @JavascriptInterface
    public void getSource(String html) {
        LogUtils.d(TAG, html);
        mWeakHandler.sendMessage(mWeakHandler.obtainMessage(MSG_PRODUCT, html));
    }
}
