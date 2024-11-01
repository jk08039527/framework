package com.jerry.baselib.weidgt;

import android.content.Context;
import android.widget.TextView;

import com.jerry.baselib.R;

/**
 * Created by wzl on 2016/3/16.
 *
 * @Description 通知类型对话框, (有title)
 */
public class KeyValueTextDialog extends BaseDialog {

    private TextView tvTitle;
    private MyEditText etKey;
    private MyEditText etValue;

    public KeyValueTextDialog(Context context) {
        super(context, false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected int getContentId() {
        return R.layout.dialog_key_value;
    }

    @Override
    protected void initView() {
        super.initView();
        tvTitle = findViewById(R.id.tv_title);
        etKey = findViewById(R.id.et_key);
        etValue = findViewById(R.id.et_value);
    }

    public String geKeyText() {
        return etKey.getText().toString();
    }

    public String getValueText() {
        return etValue.getText().toString();
    }

    public void setTitleText(final String titleText) {
        tvTitle.setText(titleText);
    }

    public void setKeyHint(final String keyHint) {
        etKey.setHint(keyHint);
    }

    public void setValueHint(final String valueHint) {
        etValue.setHint(valueHint);
    }

    public void setKeyText(final String keyText) {
        etKey.setText(keyText);
    }

    public void setValueText(final String valueText) {
        etValue.setText(valueText);
    }
}
