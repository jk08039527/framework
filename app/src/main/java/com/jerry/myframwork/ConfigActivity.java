package com.jerry.myframwork;

import java.util.List;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.jerry.baselib.R;
import com.jerry.baselib.access.BaseListenerService;
import com.jerry.baselib.asyctask.AppTask;
import com.jerry.baselib.asyctask.BackgroundTask;
import com.jerry.baselib.asyctask.WhenTaskDone;
import com.jerry.baselib.base.BaseRecyclerActivity;
import com.jerry.baselib.base.BaseRecyclerAdapter;
import com.jerry.baselib.base.RecyclerViewHolder;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.util.WeakHandler;
import com.jerry.myframwork.bean.ActionConfigBean;
import com.jerry.myframwork.dbhelper.MyDbManager;

/**
 * @author Jerry
 * @createDate 2025/5/26
 * @copyright www.axiang.com
 * @description
 */
public class ConfigActivity extends BaseRecyclerActivity<ActionConfigBean> {

    private static final String TAG = "ConfigActivity";
    private int mEditPosition = -1;

    @Override
    protected void initView() {
        super.initView();
        setTitle(R.string.config);
    }

    @Override
    protected BaseRecyclerAdapter<ActionConfigBean> initAdapter() {
        return new BaseRecyclerAdapter<ActionConfigBean>(this, mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_config;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final ActionConfigBean bean) {
                TextView tvKey = holder.getView(R.id.tv_key);
                TextView tvValue = holder.getView(R.id.tv_value);
                tvKey.setText(bean.key);
                tvValue.setText(getString(R.string.axisxy, bean.x, bean.y));
            }
        };
    }

    @Override
    protected void getData() {
        mData.clear();
        AppTask.with(this).assign(
            (BackgroundTask<List<ActionConfigBean>>) () -> MyDbManager.getInstance().queryAll(ActionConfigBean.class, null)).whenDone(
            (WhenTaskDone<List<ActionConfigBean>>) result -> {
                if (CollectionUtils.isEmpty(result)) {
                    ActionConfigBean actionConfigBean = new ActionConfigBean();
                    actionConfigBean.key = "Remove card";
                    actionConfigBean.x = BaseListenerService.getInstance().mWidth >> 1;
                    actionConfigBean.y = (int) (BaseListenerService.getInstance().mHeight * 0.83F);
                    result.add(actionConfigBean);
                    MyDbManager.getInstance().insertOrReplaceObject(actionConfigBean);
                }
                mData.addAll(result);
            }).whenTaskEnd(this::onAfterRefresh).execute();
    }

    @Override
    public void onClick(final View v) {

    }

    @Override
    public void onItemClick(final View itemView, final int position) {
        loadingDialog();
        toast("点击采集样点");
        new WeakHandler().postDelayed(() -> {
            mEditPosition = position;
            closeLoadingDialog();
        },800);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        if (mEditPosition >= 0 && ev.getAction() == MotionEvent.ACTION_DOWN) {
            ActionConfigBean actionConfigBean = mData.get(mEditPosition);
            actionConfigBean.x = (int) ev.getX();
            actionConfigBean.y = (int) ev.getY();
            MyDbManager.getInstance().insertOrReplaceObject(actionConfigBean);
            mEditPosition = -1;
            onAfterRefresh();
        }
        return super.dispatchTouchEvent(ev);
    }
}
