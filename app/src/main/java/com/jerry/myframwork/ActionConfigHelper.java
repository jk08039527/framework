package com.jerry.myframwork;

import java.util.ArrayList;
import java.util.List;

import com.jerry.baselib.access.BaseListenerService;
import com.jerry.baselib.asyctask.AppTask;
import com.jerry.baselib.asyctask.BackgroundTask;
import com.jerry.baselib.asyctask.WhenTaskDone;
import com.jerry.baselib.impl.EndCallback;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.myframwork.bean.ActionConfigBean;
import com.jerry.myframwork.dbhelper.MyDbManager;

/**
 * @author Jerry
 * @createDate 2025/5/23
 * @copyright www.axiang.com
 * @description
 */
public class ActionConfigHelper {

    private static volatile ActionConfigHelper sDataHelper;
    private final List<ActionConfigBean> mActionConfigBeans = new ArrayList<>();
    public final static ActionConfigBean defaultConfigBean = new ActionConfigBean();

    static {
        defaultConfigBean.key = "Remove card";
        defaultConfigBean.x = BaseListenerService.getInstance().mWidth >> 1;
        defaultConfigBean.y = (int) (BaseListenerService.getInstance().mHeight * 0.83F);
    }

    public static ActionConfigHelper getInstance() {
        if (sDataHelper == null) {
            synchronized (ActionConfigHelper.class) {
                if (sDataHelper == null) {
                    sDataHelper = new ActionConfigHelper();
                }
            }
        }
        return sDataHelper;
    }

    public void init(EndCallback endCallback) {
        mActionConfigBeans.clear();
        AppTask.withoutContext().assign((BackgroundTask<List<ActionConfigBean>>) () -> MyDbManager.getInstance().queryAll(ActionConfigBean.class, null)).whenDone((WhenTaskDone<List<ActionConfigBean>>) result -> {
            if (CollectionUtils.isEmpty(result)) {
                result.add(defaultConfigBean);
                MyDbManager.getInstance().insertOrReplaceObject(defaultConfigBean);
            }
            mActionConfigBeans.addAll(result);
        }).whenTaskEnd(() -> {
            if (endCallback != null) {
                endCallback.onEnd(true);
            }
        }).execute();
    }

    public List<ActionConfigBean> getActionConfigBeans() {
        return mActionConfigBeans;
    }
}
