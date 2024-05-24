package com.jerry.baselib.parsehelper;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import com.jerry.baselib.Key;
import com.jerry.baselib.access.BaseListenerService;
import com.jerry.baselib.base.BaseActivity;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.WeakHandler;
import com.jerry.baselib.R;

/**
 * @author Jerry
 * @createDate 2019/4/10
 * @description 拼多多拿cookie
 */
public class WebViewActivity extends BaseActivity {

    private static final String TAG = "WebViewActivity";
    private WebView mWebView;
    private boolean mGetProduct = true;
    private final List<String> mUrls = new ArrayList<>();
    private boolean fromService;
    private final WeakHandler mWeakHandler = new WeakHandler(new Callback() {
        @Override
        public boolean handleMessage(@NonNull final Message msg) {
            if (msg.what == WebViewUtil.MSG_PRODUCT) {
                Bundle bundle = new Bundle();
                bundle.putString("url", mWebView.getUrl());
                bundle.putString("html", String.valueOf(msg.obj));
                EventBus.getDefault().post(bundle);
                setResult(RESULT_OK);
                return true;
            }
            return false;
        }
    });

    @Override
    protected void beforeViews() {
        Intent intent = getIntent();
        fromService = intent.getBooleanExtra(Key.FROM_SERVICE, false);
        mGetProduct = intent.getBooleanExtra(Key.TYPE, true);
        String url = intent.getStringExtra(Key.DATA);
        if (url != null) {
            mUrls.add(url);
        }
        ArrayList<String> urls = intent.getStringArrayListExtra(Key.DATA);
        if (!CollectionUtils.isEmpty(urls)) {
            mUrls.addAll(urls);
        }
    }

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        setTitle(R.string.webpage);
        mWebView = findViewById(R.id.webview);
        try {
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            mWebView.removeJavascriptInterface("accessibilityTraversal");
            mWebView.removeJavascriptInterface("accessibility");

            WebSettings settings = mWebView.getSettings();
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
            mWebView.requestFocus();
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return overrideUrlLoading(url);
                }

                private boolean overrideUrlLoading(String url) {
                    if (URLUtil.isNetworkUrl(url)) {
                        mWebView.loadUrl(url);
                    }
                    return true;
                }
            });
            mWebView.addJavascriptInterface(new WebViewUtil(mWeakHandler), "java_obj");
            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(final WebView view, final int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100 && mGetProduct) {
                        view.postDelayed(() -> view.loadUrl("javascript:window.java_obj.getSource("
                            + "document.documentElement.outerHTML);void(0)"), 800);
                    }
                }
            });
            if (!mUrls.isEmpty()) {
                String url = mUrls.remove(0);
                toast("webLoad:" + url);
                mWebView.loadUrl(url);
            }
            if (fromService) {
                ArrayList<String> urls = BaseListenerService.getInstance().getTaskUrl();
                if (!CollectionUtils.isEmpty(urls)) {
                    mUrls.addAll(urls);
                    if (!mUrls.isEmpty()) {
                        mWebView.loadUrl(mUrls.remove(0));
                    }
                } else {
                    toast("暂无可解析的url");
                }
            }
        } catch (Exception e) {
            LogUtils.e("something error");
        }
    }

    @Override
    public void onClick(final View v) {

    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}