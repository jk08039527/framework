package com.jerry.myframwork;

import java.util.ArrayList;
import java.util.List;

import com.jerry.baselib.access.BaseListenerService;
import com.jerry.baselib.asyctask.AppTask;
import com.jerry.baselib.asyctask.BackgroundTask;
import com.jerry.baselib.asyctask.WhenTaskDone;
import com.jerry.myframwork.bean.RecordBean;
import com.jerry.myframwork.dbhelper.MyDbManager;

public class ListenerService extends BaseListenerService {

    private static ListenerService instance;

    public static ListenerService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        currentTask = new ZzSearchTask(ListenerService.this, mWeakHandler);
    }

    @Override
    public ArrayList<String> getTaskUrl() {
        return null;
    }

    @Override
    public void handleHtml(final String url, final String html) {

    }

    public void setRecordData(final List<RecordBean> data) {
        ((ZzSearchTask) currentTask).setRecordData(data);
    }
}
