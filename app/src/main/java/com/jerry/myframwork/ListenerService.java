package com.jerry.myframwork;

import java.util.ArrayList;

import com.jerry.baselib.access.BaseListenerService;

public class ListenerService extends BaseListenerService {

    private static ListenerService instance;

    public static ListenerService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public ArrayList<String> getTaskUrl() {
        return null;
    }

    @Override
    public void handleHtml(final String url, final String html) {

    }
}
