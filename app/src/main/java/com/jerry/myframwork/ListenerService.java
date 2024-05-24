package com.jerry.myframwork;

import java.util.ArrayList;

import com.jerry.baselib.access.BaseListenerService;

public class ListenerService extends BaseListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
        currentTask = new ZzSearchTask(ListenerService.this, mWeakHandler);
    }

    @Override
    public ArrayList<String> getTaskUrl() {
        return null;
    }

    @Override
    public void handleHtml(final String url, final String html) {

    }
}
