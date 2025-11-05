package com.jerry.baselib.parsehelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jerry.baselib.R;
import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.StringUtil;
import com.jerry.baselib.weidgt.MyWebView;
import com.jerry.baselib.weidgt.ptrlib.RefreshingView;
/**
 * @author Jerry
 */
public class WebViewDialog extends Dialog {

    private final Context mContext;
    private RefreshingView mImageView;
    private MyWebView mWebView;
    private TextView mLoadingTv;
    private String mLoadingText;
    private WebCallback mWebCallback;

    public WebViewDialog(@NonNull final Context context) {
        super(context, R.style.refresh_dialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webdialog);
        setCanceledOnTouchOutside(true);
        mImageView = findViewById(R.id.loading_image);
        mLoadingTv = findViewById(R.id.tv_loading);
        mWebView = findViewById(R.id.webview);
        mWebView.setOnPageLoadFinishedCallback(() -> mWebView.evaluateJavascript("document.documentElement.outerHTML", value -> {
            String html = StringUtil.switchJsCode(value);
            if (mWebCallback != null) {
                mWebCallback.onWebCallback(mWebView.getUrl(), html);
            }
        }));
    }

    public void loadUrl(final String url, WebCallback webCallback) {
        LogUtils.d(url);
        mWebView.loadUrl(url);
        mWebCallback = webCallback;
    }

    public interface WebCallback {

        void onWebCallback(String url, String html);
    }

    public void setLoadingText(String loadingText) {
        mLoadingText = loadingText;
    }

    @Override
    public void show() {
        if (mContext == null || ((Activity) mContext).isFinishing()) {
            return;
        }
        dismiss();
        super.show();
        mImageView.post(() -> mImageView.start());
        mLoadingTv.setText(TextUtils.isEmpty(mLoadingText) ? mContext.getString(R.string.loading) : mLoadingText);
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            if (mImageView != null) {
                mImageView.stop();
            }
            super.dismiss();
        }
    }
}