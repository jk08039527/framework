package com.jerry.myframwork;

import java.util.ArrayList;

import com.jerry.baselib.access.BaseListenerService;
import com.jerry.baselib.access.BaseTask;
import com.jerry.baselib.impl.EndCallback;
import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.WeakHandler;

/**
 * @author Jerry
 * @createDate 2023/5/8
 * @copyright www.axiang.com
 * @description
 */
public class ZzSearchTask extends BaseTask {

    public static final int TASK_SEARCH = 101;
    public static final int TASK_SEARCH_IDLE_FISH = 102;
    public static final int TASK_PUBLISH = 103;
    public static final int TASK_UPDATE = 104;

    public ZzSearchTask(final BaseListenerService listenerService, final WeakHandler weakHandler) {
        super(listenerService, weakHandler);
        PACKAGE_NAME = "com.wuba.zhuanzhuan";
    }

    @Override
    public boolean dispatchTask(int taskId, final EndCallback endCallback) {
        return false;
    }

    @Override
    public ArrayList<String> getTaskUrl() {
        return null;
    }

    @Override
    public void handleHtml(final String url, final String html) {
        LogUtils.d(url);
    }
}
