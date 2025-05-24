package com.jerry.myframwork;

import java.util.ArrayList;
import java.util.List;

import com.jerry.baselib.access.BaseListenerService;
import com.jerry.baselib.access.BaseTask;
import com.jerry.baselib.impl.EndCallback;
import com.jerry.baselib.util.AppUtils;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.util.LogUtils;
import com.jerry.baselib.util.ToastUtil;
import com.jerry.baselib.util.WeakHandler;
import com.jerry.myframwork.bean.RecordBean;
import com.jerry.myframwork.dbhelper.MyDbManager;

/**
 * @author Jerry
 * @createDate 2023/5/8
 * @copyright www.axiang.com
 * @description
 */
public class ZzSearchTask extends BaseTask {

    private final List<RecordBean> mData = new ArrayList<>();

    public ZzSearchTask(final BaseListenerService listenerService, final WeakHandler weakHandler) {
        super(listenerService, weakHandler);
        PACKAGE_NAME = "com.pgt.veoride:id/";
    }

    @Override
    public boolean dispatchTask(int taskId, final EndCallback endCallback) {
        if (taskId == 0) {
            handleRecorders(result -> {
                if (result) {
                    AppUtils.giveNotice(mService);
                    ToastUtil.showShortText("处理完成");
                }
                mService.stop();
            });
        }
        return false;
    }

    private void handleRecorders(EndCallback endCallback) {
        if (mService.isPlaying) {
            RecordBean recordBean = findNextHandleRecordBean();
            if (recordBean == null) {
                endCallback.onEnd(true);
                return;
            }
            handleRecorder(recordBean, result -> {
                if (mService.isPlaying) {
                    mService.mWeakHandler.postDelayed(() -> handleRecorders(endCallback), getShotTime());
                }
            });
        }
    }

    private void handleRecorder(final RecordBean recordBean, final EndCallback endCallback) {
        if (!mService.isPlaying) {
            return;
        }
        switch (taskState) {
            case 0:
                if (input("card_number_edit_text", recordBean.cardNum)) {
                    taskState++;
                    mService.mWeakHandler.postDelayed(() -> handleRecorder(recordBean, endCallback), getSShotTime());
                } else {
                    errorCount++;
                }
                break;
            case 1:
                if (input("expiry_date_edit_text", recordBean.cardDate)) {
                    taskState++;
                    mService.mWeakHandler.postDelayed(() -> handleRecorder(recordBean, endCallback), getSShotTime());
                } else {
                    errorCount++;
                }
                break;
            case 2:
                if (input("cvc_edit_text", recordBean.cardCvc)) {
                    taskState++;
                    mService.mWeakHandler.postDelayed(() -> handleRecorder(recordBean, endCallback), getSShotTime());
                } else {
                    errorCount++;
                }
                break;
            case 3:
                if (input("postal_code_edit_text", recordBean.cardAdd)) {
                    taskState++;
                    mService.mWeakHandler.postDelayed(() -> handleRecorder(recordBean, endCallback), getSShotTime());
                } else {
                    errorCount++;
                }
                break;
            case 4:
                if (CollectionUtils.isEmpty(mService.getRootInActiveWindow().findAccessibilityNodeInfosByViewId(PACKAGE_NAME + "tv_error"))) {
                    if (click("save_button")) {
                        taskState++;
                        mService.mWeakHandler.postDelayed(() -> handleRecorder(recordBean, endCallback), getMiddleTime());
                    } else {
                        errorCount++;
                    }
                } else {
                    recordBean.handleStatus = -1;
                    MyDbManager.getInstance().insertOrReplaceObject(recordBean);
                    errorCount = 0;
                    taskState = 0;
                    endCallback.onEnd(true);
                    return;
                }
                break;
            case 5:
                if (mService.hasText("钱包")) {
                    // 添加成功了
                    if (clickText("添加信用卡/借记卡")) {
                        recordBean.handleStatus = 1;
                        MyDbManager.getInstance().insertOrReplaceObject(recordBean);
                        errorCount = 0;
                        taskState = 0;
                        endCallback.onEnd(true);
                        return;
                    } else {
                        errorCount++;
                    }
                } else {
                    if (!CollectionUtils.isEmpty(mService.getRootInActiveWindow().findAccessibilityNodeInfosByViewId(PACKAGE_NAME + "tv_error"))) {
                        // 添加失败了
                        recordBean.handleStatus = -1;
                        MyDbManager.getInstance().insertOrReplaceObject(recordBean);
                        if (mService.back()) {
                            errorCount = 0;
                            taskState = 0;
                            endCallback.onEnd(true);
                            return;
                        } else {
                            errorCount++;
                        }
                    } else {
                        errorCount++;
                    }
                }
                mService.mWeakHandler.postDelayed(() -> handleRecorder(recordBean, endCallback), getMiddleTime());
                break;
            case 6:
                break;
        }
        if (errorCount > 3) {
            errorCount = 0;
            taskState = 0;
            mService.mWeakHandler.postDelayed(() -> handleRecorders(endCallback), getShotTime());
        }
    }

    private RecordBean findNextHandleRecordBean() {
        for (RecordBean datum : mData) {
            if (datum.handleStatus == 0) {
                return datum;
            }
        }
        return null;
    }

    public void setRecordData(final List<RecordBean> data) {
        mData.clear();
        mData.addAll(data);
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
