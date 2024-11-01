package com.jerry.baselib.weidgt;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jerry.baselib.util.LogUtils;

/**
 * @author Jerry
 * @createDate 2024/10/31
 * @description
 */
public class MyWebView extends WebView {

    private OnPageLoadFinishedCallback mOnPageLoadFinishedCallback;
    private CanLoadUrlCallback mCanLoadUrlCallback;

    public MyWebView(@NonNull final Context context) {
        this(context, null);
    }

    public MyWebView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyWebView(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            removeJavascriptInterface("searchBoxJavaBridge_");
            removeJavascriptInterface("accessibilityTraversal");
            removeJavascriptInterface("accessibility");

            WebSettings settings = getSettings();
            settings.setDefaultTextEncodingName("UTF-8");
            settings.setJavaScriptEnabled(true);
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
            settings.setAllowFileAccess(false);

            settings.setDisplayZoomControls(false);// 不显示缩放按钮
            settings.setBuiltInZoomControls(true);// 设置内置的缩放控件
            settings.setSupportZoom(true);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 支持内容重新布局
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
            settings.setLoadsImagesAutomatically(true);// 支持自动加载图片
            settings.setNeedInitialFocus(true); // 当WebView调用requestFocus时为WebView设置节点
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
            requestFocus();
            setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return overrideUrlLoading(url);
                }

                private boolean overrideUrlLoading(String url) {
                    if (URLUtil.isNetworkUrl(url) && (mCanLoadUrlCallback == null || mCanLoadUrlCallback.canLoad(url))) {
                        loadUrl(url);
                    }
                    return true;
                }
            });
            setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(final WebView view, final int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100) {
                        if (mOnPageLoadFinishedCallback != null) {
                            mOnPageLoadFinishedCallback.onPageLoadFinished();
                        }
                    }
                }
            });
        } catch (Exception e) {
            LogUtils.e("something error");
        }
    }

    public void setCanLoadUrlCallback(final CanLoadUrlCallback canLoadUrlCallback) {
        mCanLoadUrlCallback = canLoadUrlCallback;
    }

    public void setOnPageLoadFinishedCallback(final OnPageLoadFinishedCallback onPageLoadFinishedCallback) {
        mOnPageLoadFinishedCallback = onPageLoadFinishedCallback;
    }

    public interface OnPageLoadFinishedCallback {

        void onPageLoadFinished();
    }

    public interface CanLoadUrlCallback {

        boolean canLoad(String url);
    }
}
