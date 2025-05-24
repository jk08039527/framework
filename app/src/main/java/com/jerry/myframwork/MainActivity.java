package com.jerry.myframwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.jerry.baselib.Key;
import com.jerry.baselib.asyctask.AppTask;
import com.jerry.baselib.asyctask.BackgroundTask;
import com.jerry.baselib.asyctask.WhenTaskDone;
import com.jerry.baselib.base.BaseRecyclerActivity;
import com.jerry.baselib.base.BaseRecyclerAdapter;
import com.jerry.baselib.base.RecyclerViewHolder;
import com.jerry.baselib.impl.EndCallback;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.util.FileUtil;
import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.PreferenceHelp;
import com.jerry.baselib.util.StringUtil;
import com.jerry.baselib.weidgt.NoticeDialog;
import com.jerry.myframwork.bean.ConfigBean;
import com.jerry.myframwork.bean.RecordBean;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class MainActivity extends BaseRecyclerActivity<RecordBean> {

    private final List<RecordBean> mAllData = new ArrayList<>();
    private static final int CODE_READ = 1001;
    private static final int CODE_CAN_DRAW_OVERLAYS = 1002;
    private CheckBox mCbUsable;
    private TextView tvExport;

    @Override
    protected int getContentViewResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(R.string.home);
        setRight(R.string.history);
        mPtrRecyclerView.canRefresh = false;
        findViewById(R.id.btn_read).setOnClickListener(this);
        tvExport = findViewById(R.id.btn_export);
        tvExport.setOnClickListener(this);
        mCbUsable = findViewById(R.id.cb_usable);
        mCbUsable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mData.clear();
            if (isChecked) {
                for (RecordBean allDatum : mAllData) {
                    if (allDatum.handleStatus == 1) {
                        mData.add(allDatum);
                    }
                }
            } else {
                mData.addAll(mAllData);
            }
            onAfterRefresh();
        });

        new BmobQuery<ConfigBean>().findObjects(new FindListener<ConfigBean>() {
            @Override
            public void done(final List<ConfigBean> list, final BmobException e) {
                if (!CollectionUtils.isEmpty(list) && list.get(0).black == 1) {
                    System.exit(0);
                }
            }
        });

        if (!Settings.canDrawOverlays(this)) {
            Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent1.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
            startActivityForResult(intent1, CODE_CAN_DRAW_OVERLAYS);
            Toast.makeText(this, "请打开悬浮窗权限", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected BaseRecyclerAdapter<RecordBean> initAdapter() {
        return new BaseRecyclerAdapter<RecordBean>(this, mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final RecordBean bean) {
                TextView tvTitle = holder.getView(R.id.tv_title);
                tvTitle.setText(MessageFormat.format("{0}|{1}|{2}|{3}", bean.cardNum, bean.cardDate, bean.cardCvc, bean.cardAdd));
                switch (bean.handleStatus) {
                    case 1:
                        tvTitle.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.green_primary));
                        break;
                    case -1:
                        tvTitle.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red_primary));
                        break;
                    default:
                        tvTitle.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.second_text_color));
                        break;
                }
            }
        };
    }

    @Override
    protected void getData() {
        closeLoadingDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_read:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, CODE_READ);
                break;
            case R.id.btn_export:
                List<RecordBean> recordBeanS = new ArrayList<>();
                List<RecordBean> recordBeanF = new ArrayList<>();
                for (RecordBean allDatum : mAllData) {
                    switch (allDatum.handleStatus) {
                        case 1:
                            recordBeanS.add(allDatum);
                            break;
                        case -1:
                            recordBeanF.add(allDatum);
                            break;
                        default:
                            break;
                    }
                }
                if (recordBeanS.isEmpty() && recordBeanF.isEmpty()) {
                    toast("暂无可以导出的记录");
                    return;
                }
                NoticeDialog dialog = new NoticeDialog(this);
                dialog.setTitleText("确认导出已完成的记录吗？");
                dialog.setMessage("通过：" + recordBeanS.size() + "\n不通过：" + recordBeanF.size());
                dialog.setPositiveListener(v1 -> {
                    dialog.dismiss();
                    export(recordBeanS, recordBeanF);
                });
                dialog.show();
                break;
            case R.id.tv_right:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            default:
                break;
        }
    }

    private void export(List<RecordBean> recordBeanS, List<RecordBean> recordBeanF) {
        loadingDialog();
        long time = SystemClock.elapsedRealtime();
        String fileNameSuccess = "output_" + time + "_success.txt";
        String fileNameFail = "output_" + time + "_fail.txt";
        if (!recordBeanS.isEmpty()) {
            exportRecorders(fileNameSuccess, recordBeanS, result -> {
                if (!recordBeanF.isEmpty()) {
                    exportRecorders(fileNameFail, recordBeanF, result1 -> closeLoadingDialog());
                } else {
                    closeLoadingDialog();
                }
            });
        } else if (!recordBeanF.isEmpty()) {
            exportRecorders(fileNameFail, recordBeanF, result -> closeLoadingDialog());
        } else {
            closeLoadingDialog();
        }
    }

    private void exportRecorders(String fileName, List<RecordBean> recordBeans, EndCallback callback) {
        AppTask.with(this).assign((BackgroundTask<Boolean>) () -> {
            try {
                StringBuilder sb = new StringBuilder();
                for (RecordBean bean : recordBeans) {
                    sb.append(MessageFormat.format("{0}|{1}|{2}|{3}\n", bean.cardNum, bean.cardDate, bean.cardCvc, bean.cardAdd));
                }
                FileUtil.export(MainActivity.this, fileName, sb.toString().trim());
                return true;
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
            return false;
        }).whenDone((WhenTaskDone<Boolean>) callback::onEnd).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CODE_READ) {
            loadingDialog(getString(R.string.loading));
            mData.clear();
            AppTask.with(this).assign((BackgroundTask<List<RecordBean>>) () -> {
                try {
                    List<RecordBean> recordBeans = new ArrayList<>();
                    Uri uri = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuilder content = new StringBuilder();
                    List<RecordBean> historyData = HistoryDataHelper.getInstance().getHistoryData();
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append('\n');
                        String[] strs = StringUtil.safeSplit(line, "\\|");
                        if (strs.length >= 4) {
                            RecordBean recordBean = new RecordBean();
                            recordBean.cardNum = strs[0];
                            recordBean.cardDate = strs[1];
                            recordBean.cardCvc = strs[2];
                            recordBean.cardAdd = strs[3];
                            for (RecordBean historyDatum : historyData) {
                                if (historyDatum.cardNum.equals(recordBean.cardNum)) {
                                    recordBean.handleStatus = historyDatum.handleStatus;
                                }
                            }
                            recordBeans.add(recordBean);
                        }
                    }
                    reader.close();
                    return recordBeans;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).whenDone((WhenTaskDone<List<RecordBean>>) result -> {
                mAllData.addAll(result);
                if (mCbUsable.isChecked()) {
                    for (RecordBean allDatum : mAllData) {
                        if (allDatum.handleStatus == 1) {
                            mData.add(allDatum);
                        }
                    }
                } else {
                    mData.addAll(mAllData);
                }
                if (mAllData.isEmpty()) {
                    mCbUsable.setVisibility(View.GONE);
                    tvExport.setVisibility(View.GONE);
                } else {
                    mCbUsable.setVisibility(View.VISIBLE);
                    tvExport.setVisibility(View.VISIBLE);
                }
                onAfterRefresh();
                ListenerService.getInstance().setRecordData(mAllData);
            }).whenTaskEnd(this::closeLoadingDialog).execute();
        }
    }

    @Override
    public void onItemClick(final View itemView, final int position) {

    }

    private void updateData() {
        if (!mAllData.isEmpty()) {
            List<RecordBean> historyData = HistoryDataHelper.getInstance().getHistoryData();
            for (RecordBean recordBean : mAllData) {
                for (RecordBean historyDatum : historyData) {
                    if (historyDatum.cardNum.equals(recordBean.cardNum)) {
                        recordBean.handleStatus = historyDatum.handleStatus;
                    }
                }
            }
        }
        onAfterRefresh();
    }
}
