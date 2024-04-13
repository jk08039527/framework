package com.jerry.myframwork;

import java.util.List;

import android.view.View;
import android.widget.TextView;

import com.jerry.baselib.asyctask.AppTask;
import com.jerry.baselib.asyctask.BackgroundTask;
import com.jerry.baselib.asyctask.WhenTaskDone;
import com.jerry.baselib.base.BaseRecyclerActivity;
import com.jerry.baselib.base.BaseRecyclerAdapter;
import com.jerry.baselib.base.RecyclerViewHolder;
import com.jerry.baselib.util.DateUtils;
import com.jerry.baselib.util.LogUtils;

import io.gate.gateapi.ApiException;
import io.gate.gateapi.api.SpotApi;
import io.gate.gateapi.models.CurrencyPair;

public class MainActivity extends BaseRecyclerActivity<CurrencyPair> {

    @Override
    protected BaseRecyclerAdapter<CurrencyPair> initAdapter() {
        return new BaseRecyclerAdapter<CurrencyPair>(this, mData) {
            @Override
            public int getItemLayoutId(final int viewType) {
                return R.layout.item_text;
            }

            @Override
            public void convert(final RecyclerViewHolder holder, final int position, final int viewType, final CurrencyPair bean) {
                TextView tvTitle = holder.getView(R.id.tv_title);
                TextView tvContent = holder.getView(R.id.tv_content);
                tvTitle.setText(bean.getId());
                tvContent.setText(DateUtils.getDateWTimesByLong(bean.getBuyStart() * 1000L));
            }
        };
    }

    @Override
    protected void getData() {
        AppTask.with(this).assign((BackgroundTask<List<CurrencyPair>>) () -> {
            try {
                SpotApi spotApi = new SpotApi();
                return spotApi.listCurrencyPairs();
            } catch (ApiException e) {
                LogUtils.e(e.getResponseBody());
            }
            return null;
        }).whenDone((WhenTaskDone<List<CurrencyPair>>) result -> {
            mData.clear();
            mData.addAll(result);
            onAfterRefresh();
        }).execute();
    }

    @Override
    public void onClick(final View v) {

    }

    @Override
    public void onItemClick(final View itemView, final int position) {

    }
}
