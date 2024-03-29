package com.jerry.baselib.weidgt;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.jerry.baselib.R;

/**
 * Created by wzl on 2016/3/16.
 *
 * @Description 通知类型对话框, (有title)
 */
public class NoticeDialog extends BaseDialog {

    private int layoutId;
    protected TextView mTitleView;
    protected TextView mMessageView;
    @StringRes
    private int title;
    private String titleStr;
    @StringRes
    private int msg;
    private String msgStr;

    public NoticeDialog(Context context) {
        super(context, false);
        setCanceledOnTouchOutside(true);
    }

    public NoticeDialog(Context context, int layoutId) {
        this(context);
        this.layoutId = layoutId;
    }

    @Override
    protected int getContentId() {
        if (layoutId != 0) {
            return layoutId;
        }
        return R.layout.dialog_notice;
    }

    @Override
    protected void initView() {
        super.initView();
        mTitleView = findViewById(R.id.title_tv);
        mMessageView = findViewById(R.id.msg_tv);
        if (title != 0) {
            mTitleView.setText(title);
        } else if (titleStr != null) {
            mTitleView.setText(titleStr);
        }
        if (msg == 0) {
            if (TextUtils.isEmpty(msgStr)) {
                mMessageView.setVisibility(View.GONE);
            } else {
                mMessageView.setVisibility(View.VISIBLE);
                mMessageView.setText(msgStr);
            }
        } else {
            mMessageView.setVisibility(View.VISIBLE);
            mMessageView.setText(msg);
        }
    }

    public void setTitleText(@StringRes int id) {
        title = id;
    }

    public void setTitleText(String title) {
        titleStr = title;
    }

    public void setMessage(@StringRes int id) {
        msg = id;
    }

    public void setMessage(final String msgStr) {
        this.msgStr = msgStr;
    }
}
