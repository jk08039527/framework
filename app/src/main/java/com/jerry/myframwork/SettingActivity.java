package com.jerry.myframwork;

import java.util.List;

import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jerry.baselib.App;
import com.jerry.baselib.access.BaseListenerService;
import com.jerry.baselib.asyctask.AppTask;
import com.jerry.baselib.asyctask.BackgroundTask;
import com.jerry.baselib.asyctask.WhenTaskDone;
import com.jerry.baselib.base.BaseActivity;
import com.jerry.baselib.base.BaseRecyclerActivity;
import com.jerry.baselib.base.BaseRecyclerAdapter;
import com.jerry.baselib.base.RecyclerViewHolder;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.util.FileLog;
import com.jerry.baselib.util.PreferenceHelp;
import com.jerry.baselib.util.WeakHandler;
import com.jerry.myframwork.bean.ActionConfigBean;
import com.jerry.myframwork.dbhelper.MyDbManager;

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
