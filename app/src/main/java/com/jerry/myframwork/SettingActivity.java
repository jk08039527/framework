package com.jerry.myframwork;

import android.view.View;
import android.widget.CheckBox;

import com.jerry.baselib.R;
import com.jerry.baselib.base.BaseActivity;
import com.jerry.baselib.util.FileLog;

/**
 * @author Jerry
 * @createDate 2025/5/26
 * @copyright www.axiang.com
 * @description
 */
public class SettingActivity extends BaseActivity {

    private static final String TAG = "SettingActivity";

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        setTitle(R.string.log_open);
        CheckBox cb_log  = findViewById(R.id.cb_log);
        cb_log.setChecked(FileLog.getInstance().getOpened());
        cb_log.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                FileLog.getInstance().open();
            } else {
                FileLog.getInstance().close();
            }
        });
    }

    @Override
    public void onClick(final View v) {

    }
}
