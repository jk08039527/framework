package com.jerry.myframwork;

import java.util.ArrayList;
import java.util.List;

import com.jerry.baselib.asyctask.AppTask;
import com.jerry.baselib.asyctask.BackgroundTask;
import com.jerry.baselib.asyctask.WhenTaskDone;
import com.jerry.baselib.impl.EndCallback;
import com.jerry.myframwork.bean.RecordBean;
import com.jerry.myframwork.dbhelper.MyDbManager;

/**
 * @author Jerry
 * @createDate 2025/5/23
 * @copyright www.axiang.com
 * @description
 */
public class HistoryDataHelper {

    private static volatile HistoryDataHelper sDataHelper;
    private final List<RecordBean> mHistoryData = new ArrayList<>();

    public static HistoryDataHelper getInstance() {
        if (sDataHelper == null) {
            synchronized (HistoryDataHelper.class) {
                if (sDataHelper == null) {
                    sDataHelper = new HistoryDataHelper();
                }
            }
        }
        return sDataHelper;
    }

    public void init(EndCallback endCallback) {
        mHistoryData.clear();
        AppTask.withoutContext().assign((BackgroundTask<List<RecordBean>>) () -> MyDbManager.getInstance().queryAll(RecordBean.class, null)).whenDone((WhenTaskDone<List<RecordBean>>) mHistoryData::addAll).whenTaskEnd(() -> {
            if (endCallback != null) {
                endCallback.onEnd(true);
            }
        }).execute();
    }

    public List<RecordBean> getHistoryData() {
        return mHistoryData;
    }
}
