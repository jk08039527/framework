package com.jerry.myframwork;

import java.util.ArrayList;
import java.util.List;

import android.view.accessibility.AccessibilityEvent;

import com.jerry.baselib.access.BaseListenerService;
import com.jerry.baselib.access.BaseListenerService.OnPageChangedListener;
import com.jerry.baselib.access.BaseTask;
import com.jerry.baselib.impl.EndCallback;
import com.jerry.baselib.impl.OnDataCallback;
import com.jerry.baselib.util.AppUtils;
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
                    mService.mWeakHandler.postDelayed(() -> handleRecorders(endCallback), getSShotTime());
                }
            });
        }
    }

    private void handleRecorder(final RecordBean recordBean, final EndCallback endCallback) {
        if (!mService.isPlaying) {
            return;
        }
        LogUtils.i("handleRecorder: taskState =" + taskState);
        switch (taskState) {
            case 0:
                taskState++;
                if (hasNode("fl_payment_methods")) {
                    clickLast("cl_payment_method");
                }
                break;
            case 1:
                if (input("card_number_edit_text", recordBean.cardNum)
                    && input("expiry_date_edit_text", recordBean.cardDate)
                    && input("cvc_edit_text", recordBean.cardCvc)
                    && input("postal_code_edit_text", recordBean.cardAdd)) {
                    taskState++;
                } else {
                    errorCount++;
                }
                break;
            case 2:
                if (hasNode("tv_error")) {
                    recordBean.handleStatus = -1;
                    MyDbManager.getInstance().insertOrReplaceObject(recordBean);
                    errorCount = 0;
                    taskState = 0;
                    endCallback.onEnd(true);
                    return;
                }
                if (click("save_button")) {
                    mService.waitPageChange(accessibilityEvent -> {
                        if (hasNode("payment_method_text") || hasNode("cl_container")) {
                            LogUtils.w("waitPageChange = true");
                            taskState++;
                            mService.mWeakHandler.postDelayed(() -> handleRecorder(recordBean, endCallback), getSShotTime());
                            return true;
                        }
                        LogUtils.w("waitPageChange = false");
                        return false;
                    });
                    return;
                } else {
                    errorCount++;
                }
                break;
            case 3:
                if (hasNode("payment_method_text")) {
                    // 添加成功了
                    recordBean.handleStatus = 1;
                    MyDbManager.getInstance().insertOrReplaceObject(recordBean);
                    // 点卡号
                    if (clickText(recordBean.cardNum.substring(recordBean.cardNum.length() - 4))) {
                        taskState++;
                    } else {
                        errorCount++;
                    }
                } else {
                    if (hasNode("cl_container")) {
                        // 添加失败了
                        recordBean.handleStatus = -1;
                        MyDbManager.getInstance().insertOrReplaceObject(recordBean);
                        if (click("btn_left")) {
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
                break;
            case 4:
                // 点击删除
                if (click("tv_delete")) {
                    errorCount = 0;
                    taskState = 0;
                    endCallback.onEnd(true);
                    return;
                } else {
                    errorCount++;
                }
                break;
        }
        if (errorCount > 0) {
            LogUtils.w("errorCount = " + errorCount + ",taskState =" + taskState);
            if (errorCount > 3) {
                errorCount = 0;
                taskState = 0;
                endCallback.onEnd(false);
            }
        }
        mService.mWeakHandler.postDelayed(() -> handleRecorder(recordBean, endCallback), getShotTime());
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
