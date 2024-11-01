package com.jerry.baselib.weidgt;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.jerry.baselib.R;
import com.jerry.baselib.util.ToastUtil;

/**
 * Created by wzl on 2016/3/16.
 *
 * @Description 升级弹窗
 */
public class UpdateDialog extends BaseDialog {

    protected TextView mMessageView;
    protected TextView mTvLink;
    private String msgStr;
    private String link;

    public UpdateDialog(Context context) {
        super(context, false);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_update;
    }

    @Override
    protected void initView() {
        super.initView();
        mMessageView = findViewById(R.id.tv_desc);
        mMessageView.setText(msgStr);
        mTvLink = findViewById(R.id.tv_link);
        mTvLink.setOnClickListener(v -> {
            if (TextUtils.isEmpty(link)) {
                ToastUtil.showShortText(R.string.link_empty);
                return;
            }
            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", link);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            ToastUtil.showShortText(R.string.copy_success);
        });
    }

    public void setMessage(final String msgStr) {
        this.msgStr = msgStr;
        if (mMessageView != null) {
            mMessageView.setText(msgStr);
        }
    }

    public void setLink(final String link) {
        this.link = link;
    }
}
