package com.jerry.baselib.parsehelper;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.jerry.baselib.Key;
import com.jerry.baselib.access.BaseListenerService;
import com.jerry.baselib.access.BaseListenerService.MyBinder;
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

    private static final int TYPE_PDD = 1;
    private static final int TYPE_TAOBAO = 2;
    private static final int TYPE_JINGDONG = 3;
    private static final int TYPE_ZHUAN = 4;
    private final List<String> mProductIds = new ArrayList<>();
    private WebView mWebView;
    private boolean mGetProduct = true;
    private final List<String> mUrls = new ArrayList<>();
    private final WeakHandler mWeakHandler = new WeakHandler(new Callback() {
        @Override
        public boolean handleMessage(@NonNull final Message msg) {
            if (msg.what == WebViewUtil.MSG_PRODUCT) {
                String html = (String) msg.obj;
                String url = mWebView.getUrl();
                return true;
            }
            return false;
        }
    });

    @Override
    protected void beforeViews() {
        Intent intent = getIntent();
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
            if (mUrls.size() > 0) {
                String url = mUrls.remove(0);
                toast("webLoad:" + url);
                mWebView.loadUrl(url);
            }
        } catch (Exception e) {
            LogUtils.e("something error");
        }
    }

    @Override
    public void onClick(final View v) {

    }

    private int getUrlType(String url) {
        if (url.contains("yangkeduo") || url.contains("pinduoduo")) {
            return TYPE_PDD;
        }
        if (url.contains("jd.com")) {
            return TYPE_JINGDONG;
        }
        if (url.contains("zz3.cn") || url.contains("zhuanzhuan")) {
            return TYPE_ZHUAN;
        }
        return TYPE_TAOBAO;
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