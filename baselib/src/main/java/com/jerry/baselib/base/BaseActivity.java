package com.jerry.baselib.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.jerry.baselib.R;
import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.ToastUtil;
import com.jerry.baselib.weidgt.RefreshDialog;

/**
 * @author Jerry
 * @createDate 2016/9/22
 * @description 基类Activity
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    private RefreshDialog progressDialog;
    private TextView tvTitle;
    private TextView tvRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeViews();
        setContentView(getContentViewResourceId());
        tvTitle = findViewById(R.id.tv_title);
        tvRight = findViewById(R.id.tv_right);
        if (tvRight != null) {
            tvRight.setOnClickListener(this);
        }
        initView();
    }

    /**
     * 接收数据/初始化变量等与view无关的内容
     */
    protected void beforeViews() {
    }

    /**
     * 获取当前Activity使用的layout
     */
    protected abstract int getContentViewResourceId();

    @Override
    public final void setTitle(@StringRes int titleId) {
        super.setTitle(titleId);
        if (tvTitle != null) {
            tvTitle.setText(titleId);
        }
    }

    @Override
    public final void setTitle(CharSequence title) {
        super.setTitle(title);
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public void setRight(@StringRes int titleId) {
        if (tvRight != null) {
            tvRight.setText(titleId);
        }
    }

    public final void setRightDrawable(@DrawableRes int drawableRes) {
        if (tvRight != null) {
            Drawable drawable = ContextCompat.getDrawable(this, drawableRes);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvRight.setCompoundDrawables(drawable, null, null, null);
        }
    }

    public void setRight(CharSequence text) {
        if (tvRight != null) {
            tvRight.setText(text);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i("onResume: " + getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.i("onPause: " + getClass().getSimpleName());
    }

    /**
     * 初始化
     */
    protected abstract void initView();

    protected void setGone(@IdRes int id) {
        findViewById(id).setVisibility(View.GONE);
    }

    /**
     * 短时间显示Toast提示
     *
     * @param resId 字符串资源Id
     */
    protected void toast(int resId) {
        ToastUtil.showShortText(resId);
    }

    /**
     * 长时间显示Toast提示
     *
     * @param s 字符串
     */
    protected void toast(String s) {
        ToastUtil.showShortText(s);
    }

    public void loadingDialog() {
        loadingDialog(null);
    }

    public void loadingDialog(String loadingText) {
        if (null == progressDialog) {
            progressDialog = new RefreshDialog(this);
        }
        if (progressDialog.isShowing()) {
            return;
        }
        progressDialog.setLoadingText(loadingText);
        progressDialog.show();
    }

    public void closeLoadingDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}