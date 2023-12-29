package com.jerry.myframwork;

import com.jerry.baselib.access.BaseListenerService;

public class ListenerService extends BaseListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
        currentTask = new ZzSearchTask(ListenerService.this, mWeakHandler);
    }

    @Override
    public void onItemClick(final String bean, final int position) {

    }
}
