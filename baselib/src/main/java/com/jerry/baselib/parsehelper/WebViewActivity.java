package com.jerry.baselib.parsehelper;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.webkit.WebView;

import com.jerry.baselib.BuildConfig;
import com.jerry.baselib.Key;
import com.jerry.baselib.R;
import com.jerry.baselib.asyctask.AppTask;
import com.jerry.baselib.asyctask.BackgroundTask;
import com.jerry.baselib.asyctask.WhenTaskDone;
import com.jerry.baselib.base.BaseActivity;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.StringUtil;
import com.jerry.baselib.weidgt.MyWebView;

/**
 * @author Jerry
 * @createDate 2019/4/10
 * @description 拼多多拿cookie
 */
public class WebViewActivity extends BaseActivity {

    private MyWebView mWebView;
    private final List<String> mUrls = new ArrayList<>();

    @Override
    protected void beforeViews() {
        Intent intent = getIntent();
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
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        findViewById(R.id.btn_test).setOnClickListener(this);
        mWebView = findViewById(R.id.webview);
        if (!mUrls.isEmpty()) {
            mWebView.loadUrl(mUrls.remove(0));
        }
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.btn_test) {
            mWebView.evaluateJavascript("document.documentElement.outerHTML", value -> AppTask.withoutContext().assign((BackgroundTask<String>) () -> {
                String html = StringUtil.switchJsCode(value);
                LogUtils.d("dd");
                return html;
            }).whenDone((WhenTaskDone<String>) product -> {
                LogUtils.d("whenDone");
                LogUtils.d("whenDone");
            }).execute());
        }
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